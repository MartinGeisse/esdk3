/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core;

/**
 *
 */
public interface DesignItemOwned {

	default Design getDesign() {
		return getDesignItem().getDesign();
	}

	DesignItem getDesignItem();

}
