/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement;

import name.martingeisse.esdk.core.library.procedural.statement.target.AssignmentTarget;
import name.martingeisse.esdk.core.library.procedural.statement.target.AssignmentTargetFactory;
import name.martingeisse.esdk.core.library.procedural.statement.target.BitAssignmentTarget;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.library.signal.getter.BitSignalGetter;
import name.martingeisse.esdk.core.library.signal.getter.DefaultSignalGetterFactory;

/**
 *
 */
public final class BitAssignment extends Assignment {

	private final BitSignal destination;
	private BitAssignmentTarget assignmentTarget;
	private final BitSignal source;
	private BitSignalGetter sourceGetter;

	public BitAssignment(BitSignal destination, BitSignal source) {
		this.destination = checkSameDesign(destination);
		this.source = checkSameDesign(source);
	}

	@Override
	public Signal getDestination() {
		return destination;
	}

	@Override
	protected AssignmentTarget getAssignmentTarget() {
		if (assignmentTarget == null) {
			assignmentTarget = AssignmentTargetFactory.buildBitAssignmentTarget(destination);
		}
		return assignmentTarget;
	}

	@Override
	public BitSignal getSource() {
		return source;
	}

	@Override
	public boolean isEffectivelyNop() {
		return false;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void initializeSimulation() {
		super.initializeSimulation();
		this.sourceGetter = DefaultSignalGetterFactory.getGetter(source);
		getAssignmentTarget();
	}

	@Override
	public void execute() {
		assignmentTarget.setNextValue(sourceGetter.getValue());
	}

}
