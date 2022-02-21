package name.martingeisse.esdk.core.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Detects infinite recursion over a set of items of type T by checking, for each new item, whether that item is
 * already being processed. The caller must use begin() and end() to mark processing an item. Items are compared
 * by equals() / hashCode().
 *
 * This detector does not guarantee that it detects infinite recursion at the smallest possible depth, only that it
 * does detect it, and tries to do so at a depth small enough to avoid finite-but-too-long processing time. In turn,
 * finite processing should take less time than with smallest-depth detection.
 *
 * (TODO currently does simple smallest-depth detection)
 */
public final class InfiniteRecursionDetector<T> {

	private final Set<T> activeItems = new HashSet<>();

	/**
	 * Begins the specified item. If the item is already being processed, nothing happens (i.e. the call does NOT begin
	 * that item), and this method returns false. Otherwise, this method begins the item and returns true.
	 */
	public boolean begin(T item) {
		return activeItems.add(item);
	}

	/**
	 * Ends an item. This method may only be called for items that have been begun successfully, i.e. with begin()
	 * returning true.
	 */
	public void end(T item) {
		activeItems.remove(item);
	}

}
