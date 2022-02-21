/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement.target;

import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public interface VectorAssignmentTarget extends AssignmentTarget {

	int getWidth();

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	void setNextValue(Vector nextValue);

}
