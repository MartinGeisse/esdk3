package name.martingeisse.esdk.core.tools.validation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.DesignItem;

import java.util.*;

/**
 * TODO: I don't have a clear understanding of errors vs. warnings. In what scenario is the difference made? If
 * there is a difference made, how does the user get notified about warnings?
 */
public class DesignValidator {

	private final Design design;
	private final Set<DesignItem> visitedItems = new HashSet<>();
	private final Map<DesignItem, ItemValidationResult> itemResults = new HashMap<>();

	public DesignValidator(Design design) {
		this.design = design;
	}

	public DesignValidationResult validate() {
		for (DesignItem item : design.getItems()) {
			validate(item);
		}
		return new DesignValidationResult(design, ImmutableMap.copyOf(itemResults));
	}

	private void validate(DesignItem item) {
		if (!visitedItems.add(item)) {
			return;
		}
		List<String> errors = new ArrayList<>();
		List<String> warnings = new ArrayList<>();
		ValidationContext context = new ValidationContext() {

			@Override
			public void reportError(String message) {
				errors.add(message);
			}

			@Override
			public void reportWarning(String message) {
				warnings.add(message);
			}

		};
		item.validate(context);
		itemResults.put(item, new ItemValidationResult(item, ImmutableList.copyOf(errors), ImmutableList.copyOf(warnings)));
	}

}
