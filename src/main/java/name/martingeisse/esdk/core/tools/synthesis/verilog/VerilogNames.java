package name.martingeisse.esdk.core.tools.synthesis.verilog;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.util.AbsoluteNames;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores names for things in Verilog and performs assignment of generated names.
 *
 * This class is not used for suggested names that might be used or not. Rather, it is
 * used for allocated names that must be used if present, and if something does not
 * have a name here, it is not declared in Verilog.
 */
public class VerilogNames {

	private final AbsoluteNames absoluteNames;
	private final Set<String> fixedNames = new HashSet<>();
	private final Map<String, MutableInt> prefixNameCounters = new HashMap<>();
	private final Map<String, DesignItem> nameToItem = new HashMap<>();
	private final Map<DesignItem, String> itemToName = new HashMap<>();

	/**
	 * The argument is used for suggestions for the Verilog names to assign.
	 */
	VerilogNames(AbsoluteNames absoluteNames) {
		this.absoluteNames = absoluteNames;
	}

	/**
	 * Uses the specified name for an object, ignoring the object's own name.
	 */
	public void assignFixedName(String name, DesignItem item) {
		if (nameToItem.putIfAbsent(name, item) != null) {
			throw new IllegalStateException("fixed name is already used: " + name);
		}
		itemToName.put(item, name);
		fixedNames.add(name);
	}

	/**
	 * Generates a name based on the object's own name, possibly adding a number for disambiguation.
	 */
	public String assignGeneratedName(DesignItem item) {
		String prefix = absoluteNames.getAbsoluteName(item);
		MutableInt counter = prefixNameCounters.computeIfAbsent(prefix, p -> new MutableInt());
		while (true) {
			String name = (counter.intValue() == 0) ? prefix : (prefix + "__" + counter.intValue());
			counter.increment();

			// If the counter collides with a fixed name, we could just increment again, but we shouldn't: The order
			// in which the fixed and assigned names get reserved is undefined and therefore should not be relevant,
			// so we must do the same as if the generated name came first -- and that throws an exception.
			if (fixedNames.contains(name)) {
				throw new IllegalStateException("assigned name collides with fixed name: " + name);
			}

			// There may still be a collision in the odd case of counter prefixes like "foo" and "foo__1", so to
			// avoid edge cases, we have to check that too, hence the while loop.
			if (nameToItem.putIfAbsent(name, item) == null) {
				itemToName.put(item, name);
				return name;
			}

		}
	}

	public String getName(DesignItem item) {
		return itemToName.get(item);
	}

}
