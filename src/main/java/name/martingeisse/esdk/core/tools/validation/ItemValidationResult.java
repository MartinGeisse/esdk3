package name.martingeisse.esdk.core.tools.validation;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.DesignItem;

/**
 *
 */
public final class ItemValidationResult {

	private final DesignItem item;
	private final ImmutableList<String> errors;
	private final ImmutableList<String> warnings;

	public ItemValidationResult(DesignItem item, ImmutableList<String> errors, ImmutableList<String> warnings) {
		if (item == null) {
			throw new IllegalArgumentException("item is null");
		}
		if (errors == null) {
			throw new IllegalArgumentException("errors is null");
		}
		if (warnings == null) {
			throw new IllegalArgumentException("warnings is null");
		}
		this.item = item;
		this.errors = errors;
		this.warnings = warnings;
	}

	public DesignItem getItem() {
		return item;
	}

	public ImmutableList<String> getErrors() {
		return errors;
	}

	public ImmutableList<String> getWarnings() {
		return warnings;
	}

}
