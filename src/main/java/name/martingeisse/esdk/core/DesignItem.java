/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.SynthesisNotSupportedException;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.validation.ValidationContext;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.PrintWriter;

/**
 * Each design item belongs to exactly one design, and registers itself with that design in the DesignItem constructor.
 *
 * An item can have an optional name and optional hierarchy parent. Both are used to make the generated output code
 * and documentation more readable. If omitted, nothing bad should happen except for the output being less readable.
 */
public abstract class DesignItem implements DesignItemOwned {

	/**
	 * This flag causes each DesignItem to generate and store a stack trace at its point of creation. If an error occurs
	 * later, this helps to pinpoint the problem. The performance impact is usually negligible since it occurs only
	 * at item creation. If it does become a problem, such as in automated testing, this flag is public so it can be
	 * turned off.
	 */
	public static boolean TRACE_POINT_OF_CREATION = true;

	private final Design design;
	private final Exception pointOfCreation;
	private DesignItem designHierarchyParent;
	private String name;

	public DesignItem() {
		this.design = ImplicitGlobalDesign.getOrFail();
		design.registerDesignItem(this);
		if (TRACE_POINT_OF_CREATION) {
			pointOfCreation = new Exception();
			pointOfCreation.fillInStackTrace();
		} else {
			pointOfCreation = null;
		}
	}

	/**
	 * Gets the design this item belongs to.
	 */
	@Override
	public final Design getDesign() {
		return design;
	}

	@Override
	public final DesignItem getDesignItem() {
		return this;
	}

	/**
	 * Ensures that this item and the other item (or some object that belongs to an item) belong to the same design.
	 * Throws an exception if that is not the case.
	 */
	protected final <T extends DesignItemOwned> T checkSameDesign(T designItemOwned) {
		DesignItem otherItem = designItemOwned.getDesignItem();
		if (otherItem.getDesign() != getDesign()) {
			String argumentDescription = (otherItem == designItemOwned) ?
					("item (" + otherItem + ")") : ("object (" + designItemOwned + " from item " + otherItem + ")");
			throw new IllegalArgumentException("the specified " + argumentDescription + " is not part of the same design as this item (" + this + ")");
		}
		return designItemOwned;
	}

	/**
	 * Ensures that this item belongs to the specified design.
	 * Throws an exception if that is not the case.
	 */
	protected final void checkSameDesign(Design design) {
		if (getDesign() != design) {
			throw new IllegalArgumentException("this item (" + this + ") is not part of the expected design (" + design + ")");
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// metadata for more readable output
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Gets the parent item in the design hierarchy.
	 */
	public DesignItem getDesignHierarchyParent() {
		return designHierarchyParent;
	}

	/**
	 * Sets the parent item in the design hierarchy.
	 */
	public void setDesignHierarchyParent(DesignItem designHierarchyParent) {
		this.designHierarchyParent = designHierarchyParent;
	}

	/**
	 * Gets the name of this item.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name of this item.
	 */
	public final void setName(String name) {
		this.name = name;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// construction and validation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Performs finishing steps before validation of all items. This method must not assume the item to be valid, but
	 * in turn may contribute to it becoming valid.
	 */
	protected void finalizeConstructionBeforeValidation() {
	}

	/**
	 * Validates this item. Should not validate linked items or design hierarchy children -- the outer validation loop
	 * will do that.
	 */
	public void validate(ValidationContext context) {
	}

	/**
	 * Performs finishing steps after validation of all items. This method can therefore assume all items to be valid.
	 */
	protected void finalizeConstructionAfterValidation() {
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Performs the steps needed to initialize the simulation for this item.
	 */
	protected void initializeSimulation() {
	}

	/**
	 * Convenience method to fire a simulation event.
	 */
	protected final void fire(Runnable callback, long ticks) {
		design.fire(callback, ticks);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// synthesis
	// ----------------------------------------------------------------------------------------------------------------

	public abstract VerilogContribution getVerilogContribution();

	public SynthesisNotSupportedException newSynthesisNotSupportedException() {
		return new SynthesisNotSupportedException("synthesis not supported for " + this);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// other
	// ----------------------------------------------------------------------------------------------------------------

	public void writePointOfCreationTo(StringBuilder builder) {
		if (pointOfCreation != null) {
			builder.append("\nThis item was created at: \n");
			pointOfCreation.printStackTrace(new PrintWriter(new StringBuilderWriter(builder)));
			builder.append("\n");
		}
	}

	public String writePointOfCreationToString() {
		StringBuilder builder = new StringBuilder();
		writePointOfCreationTo(builder);
		return builder.toString();
	}

	/**
	 * Show signal values in toString().
	 */
	@Override
	public String toString() {
		String suffix = writePointOfCreationToString();
		if (this instanceof BitSignal) {
			BitSignal signal = (BitSignal)this;
			String value;
			try {
				value = Boolean.toString(signal.getValue());
			} catch (Exception e) {
				value = "???";
			}
			return getName() + " = " + value + " (" + super.toString() + ")" + suffix;
		} else if (this instanceof VectorSignal) {
			VectorSignal signal = (VectorSignal)this;
			String value;
			try {
				value = signal.getValue().toString();
			} catch (Exception e) {
				value = "???";
			}
			return getName() + " = " + value + " (" + super.toString() + ")" + suffix;
		} else {
			return super.toString() + suffix;
		}
	}

}
