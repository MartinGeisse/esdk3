package name.martingeisse.esdk.core.tools.structure;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.DesignItem;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses reflection to obtain an item graph. This can be used for things such as visualizing the design, printing
 * validation errors, and so on.
 *
 * Instances of this class cannot be re-used.
 *
 * TODO consider using the design hierarchy instead of this class
 */
public final class DesignStructureAnalyzer {

	private final Design design;
	private final Map<DesignItem, Map<String, DesignItem>> subItemRelation = new HashMap<>();

	public DesignStructureAnalyzer(Design design) {
		this.design = design;
	}

	public Map<DesignItem, Map<String, DesignItem>> getSubItemRelation() {
		return subItemRelation;
	}

	public void analyze() {
		try {
			for (DesignItem item : design.getItems()) {
				analyze(item);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void analyze(DesignItem item) throws Exception {
		if (subItemRelation.containsKey(item)) {
			return;
		}
		Map<String, DesignItem> subItemCollector = new HashMap<>();
		subItemRelation.put(item, subItemCollector);
		collectSubItems(item, item.getClass(), subItemCollector);
	}


	private void collectSubItems(DesignItem item, Class<?> currentClass, Map<String, DesignItem> subItemCollector) throws Exception {
		if (currentClass == DesignItem.class) {
			return;
		}
		for (Field field : currentClass.getDeclaredFields()) {

			String propertyName = field.getName();
			if (subItemCollector.containsKey(propertyName)) {
				propertyName = currentClass.getSimpleName() + '.' + field.getName();
				if (subItemCollector.containsKey(propertyName)) {
					propertyName = currentClass.getName() + '.' + field.getName();
				}
			}

			field.setAccessible(true);
			Object value = field.get(item);
			if (value instanceof DesignItem) {
				subItemCollector.put(propertyName, (DesignItem) value);
			} else if (value instanceof Iterable<?>) {
				int i = 0;
				for (Object element : (Iterable<?>)value) {
					if (element instanceof DesignItem) {
						subItemCollector.put(propertyName + '[' + i + ']', (DesignItem)element);
					}
					i++;
				}
			}
		}
		collectSubItems(item, currentClass.getSuperclass(), subItemCollector);
	}

}
