package name.martingeisse.esdk.core.util;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstance;
import name.martingeisse.esdk.core.library.memory.Memory;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.signal.Signal;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Determines an absolute name for each {@link DesignItem} based on the item's (relative) name, its hierarchy parent,
 * its class, and how it is used.
 * <p>
 * TODO take hierarchy parent into account
 */
public class AbsoluteNames {

	private final Design design;
	private UsageBasedNameSuggestions usageBasedNameSuggestions;
	private InfiniteRecursionDetector<DesignItem> infiniteRecursionDetector;
	private final Map<DesignItem, String> absoluteNames;

	public AbsoluteNames(Design design) {
		this.design = design;
		usageBasedNameSuggestions = new UsageBasedNameSuggestions(this.design);
		infiniteRecursionDetector = new InfiniteRecursionDetector<>();
		absoluteNames = new HashMap<>();
		for (DesignItem item : this.design.getItems()) {
			determineAbsoluteName(item);
		}
		usageBasedNameSuggestions = null;
		infiniteRecursionDetector = null;
	}

	private String determineAbsoluteName(DesignItem item) {
		if (infiniteRecursionDetector.begin(item)) {
			String result = determineAbsoluteNameWithoutRecursionDetection(item);
			infiniteRecursionDetector.end(item);
			return result;
		} else {
			return getDefaultName(item);
		}
	}

	private String determineAbsoluteNameWithoutRecursionDetection(DesignItem item) {
		String absoluteName = absoluteNames.get(item);
		if (absoluteName == null) {
			if (item.getName() != null) {
				absoluteName = item.getName();
			} else {
				UsageBasedNameSuggestions.PropagatingSuggestion propagatingSuggestion = usageBasedNameSuggestions.getPropagatingSuggestions().get(item);
				if (propagatingSuggestion != null) {
					String originName = determineAbsoluteName(propagatingSuggestion.getOrigin());
					absoluteName = propagatingSuggestion.getNameTransformation().apply(originName);
				} else {
					String independentSuggestion = usageBasedNameSuggestions.getIndependentSuggestions().get(item);
					if (independentSuggestion != null) {
						absoluteName = independentSuggestion;
					} else {
						absoluteName = getDefaultName(item);
					}
				}
			}
			if (absoluteName == null) {
				// This should not happen, i.e. none of the above cases should produce null, but it case it happens
				// anyway we want to catch the error early.
				throw new RuntimeException("could not determine absolute name for item: " + item);
			}
			absoluteNames.put(item, absoluteName);
		}
		return absoluteName;
	}

	protected String getDefaultName(DesignItem item) {
		if (item instanceof ProceduralRegister) {
			return "register";
		} else if (item instanceof ProceduralMemory || item instanceof Memory) {
			return "memory";
		} else if (item instanceof Signal) {
			return "signal";
		} else if (item instanceof BlackboxInstance) {
			String moduleName = ((BlackboxInstance) item).getBlackboxTemplateName();
			return moduleName == null ? "instance" : StringUtils.uncapitalize(moduleName);
		}
		String className = item.getClass().getSimpleName();
		return StringUtils.uncapitalize(className);
	}

	public String getAbsoluteName(DesignItem item) {
		if (item.getDesign() != design) {
			throw new IllegalArgumentException("wrong design for item: " + item);
		}
		String absoluteName = absoluteNames.get(item);
		if (absoluteName == null) {
			throw new IllegalArgumentException("no name for item: " + item);
		}
		return absoluteName;
	}

}
