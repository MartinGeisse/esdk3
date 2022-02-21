/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.ImplicitGlobalDesign;

/**
 *
 */
public abstract class Pin extends DesignItem implements DesignItemOwned {

	private String id;
	private PinConfiguration configuration;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		if (getName() == null) {
			setName(id);
		}
	}

	public PinConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(PinConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getNetName() {
		return "pin" + id;
	}

	public static <T extends Pin> T getByNetName(Class<T> pinClass, String netName) {
		for (T pin : ImplicitGlobalDesign.getOrFail().getItems(pinClass)) {
			if (pin.getNetName().equals(netName)) {
				return pin;
			}
		}
		return null;
	}

}
