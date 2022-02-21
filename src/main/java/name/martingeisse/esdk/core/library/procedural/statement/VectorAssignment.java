/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement;

import name.martingeisse.esdk.core.library.procedural.statement.target.AssignmentTarget;
import name.martingeisse.esdk.core.library.procedural.statement.target.AssignmentTargetFactory;
import name.martingeisse.esdk.core.library.procedural.statement.target.VectorAssignmentTarget;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.getter.DefaultSignalGetterFactory;
import name.martingeisse.esdk.core.library.signal.getter.VectorSignalGetter;

/**
 *
 */
public final class VectorAssignment extends Assignment {

	private final VectorSignal destination;
	private VectorAssignmentTarget assignmentTarget;
	private final VectorSignal source;
	private VectorSignalGetter sourceGetter;

	public VectorAssignment(VectorSignal destination, VectorSignal source) {
		if (destination.getWidth() != source.getWidth()) {
			throw new IllegalArgumentException("destination width (" + destination.getWidth() + ") and source width (" + source.getWidth() + ") differ");
		}
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
			assignmentTarget = AssignmentTargetFactory.buildVectorAssignmentTarget(destination);
		}
		return assignmentTarget;
	}

	@Override
	public VectorSignal getSource() {
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
