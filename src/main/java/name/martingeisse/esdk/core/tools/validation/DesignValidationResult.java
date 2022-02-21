package name.martingeisse.esdk.core.tools.validation;

import com.google.common.collect.ImmutableMap;
import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.tools.validation.print.ValidationResultFormatter;
import name.martingeisse.esdk.core.tools.validation.print.ValidationResultPrinter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 *
 */
public final class DesignValidationResult {

	private final Design design;
	private final ImmutableMap<DesignItem, ItemValidationResult> itemResults;

	public DesignValidationResult(Design design, ImmutableMap<DesignItem, ItemValidationResult> itemResults) {
		this.design = design;
		this.itemResults = itemResults;
	}

	public Design getDesign() {
		return design;
	}

	public ImmutableMap<DesignItem, ItemValidationResult> getItemResults() {
		return itemResults;
	}

	public boolean isValid(boolean failOnWarnings) {
		for (ItemValidationResult itemResult : itemResults.values()) {
			if (!itemResult.getErrors().isEmpty()) {
				return false;
			}
			if (failOnWarnings && !itemResult.getWarnings().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void format(ValidationResultPrinter printer) {
		new ValidationResultFormatter(this, printer, false).format();
	}

	/**
	 * Even if warnings are included, this method preferably returns errors.
	 */
	public Pair<DesignItem, ItemValidationResult> getSampleError(boolean includeWarnings) {
		DesignItem warningItem = null;
		ItemValidationResult warningResult = null;
		for (Map.Entry<DesignItem, ItemValidationResult> entry : itemResults.entrySet()) {
			DesignItem item = entry.getKey();
			ItemValidationResult itemResult = entry.getValue();
			if (!itemResult.getErrors().isEmpty()) {
				return Pair.of(item, itemResult);
			}
			if (warningItem == null && !itemResult.getWarnings().isEmpty()) {
				warningItem = item;
				warningResult = itemResult;
			}
		}
		return warningItem == null ? null : Pair.of(warningItem, warningResult);
	}

}
