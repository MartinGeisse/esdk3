/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.ImplicitGlobalDesign;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.SignalConnector;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.FakeVerilogExpressionWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.AbsoluteNames;

import java.io.Writer;
import java.util.*;

/**
 * Generates the contents of the main Verilog file.
 */
public class VerilogCodeGenerator {

	private final VerilogWriter out;
	private final String toplevelModuleName;
	private final AuxiliaryFileFactory auxiliaryFileFactory;
	private final Map<Signal, SignalDeclaration> signalDeclarations = new HashMap<>();
	private final VerilogNames names;

	public VerilogCodeGenerator(Writer out, String toplevelModuleName, AuxiliaryFileFactory auxiliaryFileFactory) {
		this.out = new VerilogWriter(out) {

			@Override
			public String getNameOrNull(DesignItem item) {
				if (item == null) {
					throw new IllegalArgumentException("item argument is null");
				}
				return names.getName(item);
			}

		};
		this.toplevelModuleName = toplevelModuleName;
		this.auxiliaryFileFactory = auxiliaryFileFactory;
		this.names = new VerilogNames(new AbsoluteNames(ImplicitGlobalDesign.getOrFail()));
	}

	public void generate() {
		Design design = ImplicitGlobalDesign.getOrFail();

		// validate items
		design.finalizeConstruction();

		// collect contributions from all items
		List<VerilogContribution> contributions = new ArrayList<>();
		for (DesignItem item : design.getItems()) {
			VerilogContribution contribution = item.getVerilogContribution();
			if (contribution == null) {
				throw new RuntimeException("Got null verilog contribution which is not allowed; item = " + item);
			}
			contributions.add(contribution);
		}

		// prepare contributions. This also collects signals that must be declared.
		{
			SynthesisPreparationContext synthesisPreparationContext = new SynthesisPreparationContext() {

				@Override
				public void assignFixedName(String name, DesignItem item) {
					names.assignFixedName(name, item);
				}

				@Override
				public String assignGeneratedName(DesignItem item) {
					return names.assignGeneratedName(item);
				}

				@Override
				public void declareFixedNameSignal(Signal signal, String name, VerilogSignalDeclarationKeyword keyword, boolean generateAssignment) {
					names.assignFixedName(name, signal.getDesignItem());
					internalDeclareSignal(signal, name, keyword, generateAssignment);
				}

				@Override
				public String declareSignal(Signal signal, VerilogSignalDeclarationKeyword keyword, boolean generateAssignment) {
					String name = names.assignGeneratedName(signal.getDesignItem());
					internalDeclareSignal(signal, name, keyword, generateAssignment);
					return name;
				}

				private void internalDeclareSignal(Signal signal, String name, VerilogSignalDeclarationKeyword keyword, boolean generateAssignment) {
					signalDeclarations.put(signal, new SignalDeclaration(signal, name, keyword, generateAssignment));
				}

				@Override
				public AuxiliaryFileFactory getAuxiliaryFileFactory() {
					return auxiliaryFileFactory;
				}

			};
			for (VerilogContribution contribution : contributions) {
				contribution.prepareSynthesis(synthesisPreparationContext);
			}
		}

		// Analyze all signals for shared usage. These signals will be declared too.
		{
			final Set<Signal> analyzedSignals = new HashSet<>();
			SignalUsageConsumer signalUsageConsumer = new SignalUsageConsumer() {

				final VerilogExpressionWriter fakeExpressionWriter = new FakeVerilogExpressionWriter() {

					@Override
					protected void visitSignal(Signal subSignal, VerilogExpressionNesting subNesting) {
						consumeSignalUsage(subSignal, subNesting);
					}

					@Override
					protected void visitMemory(ProceduralMemory memory) {
					}

				};

				@Override
				public void consumeSignalUsage(Signal signal, VerilogExpressionNesting nesting) {

					// for convenience, so this "if" does not have to be repeated over and over again
					if (signal == null) {
						return;
					}

					// when a connector is used, it is actually the connected signal that is used
					while (signal instanceof SignalConnector) {
						signal = ((SignalConnector) signal).getConnected();
					}

					// Extract all signals that are used in more than one place. Those have been analyzed already when we found them
					// the first time.
					if (!analyzedSignals.add(signal)) {
						declareSignal(signal);
						return;
					}

					// Also extract signals that do not comply with their current nesting level, but since we didn't find them
					// above, they have not been analyzed yet, so continue below.
					boolean compliesWithNesting = signal.compliesWith(nesting);
					if (!compliesWithNesting) {
						declareSignal(signal);
					}

					// analyze signals for shared sub-expressions
					signal.analyzeSignalUsage(this);

				}

				private void declareSignal(Signal signal) {
					if (signalDeclarations.get(signal) == null) {
						String name = names.assignGeneratedName(signal.getDesignItem());
						signalDeclarations.put(signal, new SignalDeclaration(signal, name, VerilogSignalDeclarationKeyword.WIRE, true));
					}
				}

				@Override
				public VerilogExpressionWriter getFakeExpressionWriter() {
					return fakeExpressionWriter;
				}

			};
			for (VerilogContribution contribution : contributions) {
				contribution.analyzeSignalUsage(signalUsageConsumer);
			}
		}

		// consume toplevel ports
		List<ToplevelPortContribution> toplevelPorts = new ArrayList<>();
		for (VerilogContribution contribution : contributions) {
			contribution.analyzeToplevelPorts((direction, name, width) -> toplevelPorts.add(new ToplevelPortContribution(direction, name, width)));
		}

		// assemble the toplevel module
		out.println("`default_nettype none");
		out.println("`timescale 1ns / 1ps");
		out.println();
		out.println("module " + toplevelModuleName + "(");
		if (!toplevelPorts.isEmpty()) {
			out.startIndentation();
			boolean first = true;
			for (ToplevelPortContribution toplevelPort : toplevelPorts) {
				if (first) {
					first = false;
				} else {
					out.println(',');
				}
				out.indent();
				out.print(toplevelPort.name);
			}
			out.println();
			out.endIndentation();
		}
		out.println(");");
		out.println();
		if (!toplevelPorts.isEmpty()) {
			for (ToplevelPortContribution toplevelPort : toplevelPorts) {
				if (toplevelPort.width == null) {
					out.println(toplevelPort.direction + ' ' + toplevelPort.name + ';');
				} else {
					out.println(toplevelPort.direction + '[' + (toplevelPort.width - 1) + ":0] " + toplevelPort.name + ';');
				}
			}
			out.println();
		}
		out.println();
		for (Map.Entry<Signal, SignalDeclaration> signalEntry : signalDeclarations.entrySet()) {
			Signal signal = signalEntry.getKey();
			SignalDeclaration signalDeclaration = signalEntry.getValue();
			if (signalDeclaration.keyword != VerilogSignalDeclarationKeyword.NONE) {
				out.print(signalDeclaration.keyword.name().toLowerCase());
				if (signal instanceof VectorSignal) {
					VectorSignal vectorSignal = (VectorSignal) signal;
					out.print('[');
					out.print(vectorSignal.getWidth() - 1);
					out.print(":0] ");
				} else if (signal instanceof BitSignal) {
					out.print(' ');
				} else {
					throw new RuntimeException("signal is neither a bit signal nor a vector signal: " + signal);
				}
				out.print(signalDeclaration.name);
				out.println(';');
			}
		}
		out.println();
		for (VerilogContribution contribution : contributions) {
			contribution.printDeclarations(out);
		}
		out.println();
		for (Map.Entry<Signal, SignalDeclaration> signalEntry : signalDeclarations.entrySet()) {
			Signal signal = signalEntry.getKey();
			SignalDeclaration signalDeclaration = signalEntry.getValue();
			if (signalDeclaration.assignment) {
				out.print("assign " + signalDeclaration.name + " = ");
				out.printImplementationExpression(signal);
				out.println(";");
			}
		}
		out.println();
		for (VerilogContribution contribution : contributions) {
			contribution.printImplementation(out);
		}
		out.println();
		out.println("endmodule");
		out.println();

	}

	public VerilogNames getNames() {
		return names;
	}

	static class SignalDeclaration {

		final Signal signal;
		final String name;
		final VerilogSignalDeclarationKeyword keyword;
		final boolean assignment;

		SignalDeclaration(Signal signal, String name, VerilogSignalDeclarationKeyword keyword, boolean assignment) {
			if (signal == null) {
				throw new IllegalArgumentException("signal is null");
			}
			if (name == null) {
				throw new IllegalArgumentException("name is null");
			}
			if (keyword == null) {
				throw new IllegalArgumentException("keyword is null");
			}
			this.signal = signal;
			this.name = name;
			this.keyword = keyword;
			this.assignment = assignment;
		}

	}

	static class ToplevelPortContribution {

		final String direction;
		final String name;
		final Integer width;

		ToplevelPortContribution(String direction, String name, Integer width) {
			this.direction = direction;
			this.name = name;
			this.width = width;
		}

	}

}
