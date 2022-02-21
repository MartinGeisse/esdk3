package name.martingeisse.esdk.core.tools.validation.print;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.tools.structure.DesignRootItemFinder;
import name.martingeisse.esdk.core.tools.structure.DesignStructureAnalyzer;
import name.martingeisse.esdk.core.tools.validation.DesignValidationResult;
import name.martingeisse.esdk.core.tools.validation.ItemValidationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public final class ValidationResultFormatter {

	private final DesignValidationResult designResult;
	private final ValidationResultPrinter printer;
	private final boolean foldValid;
	private Map<DesignItem, Map<String, DesignItem>> subItemRelation;
	private Set<DesignItem> rootItems;
	private Map<DesignItem, String> references;

	public ValidationResultFormatter(DesignValidationResult designResult, ValidationResultPrinter printer, boolean foldValid) {
		this.designResult = designResult;
		this.printer = printer;
		this.foldValid = foldValid;
	}

	public void format() {
		DesignStructureAnalyzer structureAnalyzer = new DesignStructureAnalyzer(designResult.getDesign());
		structureAnalyzer.analyze();
		subItemRelation = structureAnalyzer.getSubItemRelation();
		DesignRootItemFinder rootFinder = new DesignRootItemFinder(subItemRelation);
		rootFinder.findRootItems();
		rootItems = rootFinder.getRootItems();
		references = new HashMap<>();
		for (DesignItem root : rootItems) {
			String reference = "ref" + references.size();
			references.put(root, reference);
		}
		for (DesignItem root : rootItems) {
			print(references.get(root), root);
		}
	}

	private void print(String propertyName, DesignItem item) {
		String itemClass = item.getClass().getSimpleName();
		ItemValidationResult itemResult = designResult.getItemResults().get(item);
		if (hasErrorsOrWarnings(itemResult) || !foldValid) {
			printer.beginItem(propertyName, itemClass);
			printContents(itemResult);
			printer.endItem();
		} else {
			printer.printFoldedSubItem(propertyName, itemClass);
		}

	}

	private boolean hasErrorsOrWarnings(ItemValidationResult itemResult) {
		if (!itemResult.getErrors().isEmpty() || !itemResult.getWarnings().isEmpty()) {
			return true;
		}
		for (DesignItem subItem : subItemRelation.get(itemResult.getItem()).values()) {
			if (rootItems.contains(subItem)) {
				continue;
			}
			if (hasErrorsOrWarnings(designResult.getItemResults().get(subItem))) {
				return true;
			}
		}
		return false;
	}

	private void printContents(ItemValidationResult itemResult) {
		for (String error : itemResult.getErrors()) {
			printer.printError(error);
		}
		for (String warning : itemResult.getWarnings()) {
			printer.printWarning(warning);
		}
		for (Map.Entry<String, DesignItem> itemEntry : subItemRelation.get(itemResult.getItem()).entrySet()) {
			String propertyName = itemEntry.getKey();
			DesignItem subItem = itemEntry.getValue();
			String reference = references.get(subItem);
			if (reference == null) {
				print(propertyName, subItem);
			} else {
				printer.printReference(propertyName, reference);
			}
		}
	}

}
