/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util;

import java.io.InputStream;

/**
 *
 */
public final class AssociatedResourceUtil {

	// prevent instantiation
	private AssociatedResourceUtil() {
	}

	public static InputStream open(Class<?> anchorClass, String dotExtension) {
		return open(anchorClass, null, dotExtension);
	}

	public static InputStream open(Class<?> anchorClass, String suffix, String dotExtension) {
		Class<?> currentClass = anchorClass;
		while (currentClass != null) {
			InputStream inputStream;
			if (suffix == null) {
				inputStream = currentClass.getResourceAsStream(currentClass.getSimpleName() + dotExtension);
			} else {
				inputStream = currentClass.getResourceAsStream(currentClass.getSimpleName() + '_' + suffix + dotExtension);
			}
			if (inputStream != null) {
				return inputStream;
			}
			currentClass = currentClass.getSuperclass();
		}
		throw new ResourceNotFoundException("resource not found: class = " + anchorClass + ", suffix = " + suffix + ", dotExtension = " + dotExtension);
	}

	public static class ResourceNotFoundException extends RuntimeException {
		public ResourceNotFoundException(String message) {
			super(message);
		}
	}

}
