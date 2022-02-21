package name.martingeisse.esdk.core.tools.structure;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.procedural.statement.Statement;
import name.martingeisse.esdk.core.library.signal.Signal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Based on the "sub item" relation from the {@link DesignStructureAnalyzer}, this class detects root nodes for a
 * design in a way to make sure that (1) all items are reached, (2) non-roots have exactly one predecessor, and
 * (3) each cycle is broken by a root.
 * <p>
 * Algorithm: In three phases, we find roots in a loop and create an otherwise root-less and acyclic "patch" for each
 * root. Phase 1 picks nodes without predecessors or with multiple predecessors, phase 2 picks "best" nodes out of
 * cycles and phase 3 picks any nodes as a fallback. For each root, we find nodes reached from that root.
 * <p>
 * We avoid root-less cycles by the following logic: If a patch contains nodes that are part of a cycle, then either
 * the root itself is part of the cycle, breaking it, or it leads into a cycle -- but then the node that "merges"
 * our initial path with the cycle has multiple predecessors and becomes a root anyway.
 *
 * TODO consider using the design hierarchy instead of this class
 */
public final class DesignRootItemFinder {

	private final Map<DesignItem, Map<String, DesignItem>> subItemRelation;
	private final Map<DesignItem, Set<DesignItem>> inverseSubItemRelation;
	private final Set<DesignItem> unreachedItems;
	private final Set<DesignItem> rootItems;

	public DesignRootItemFinder(Map<DesignItem, Map<String, DesignItem>> subItemRelation) {
		this.subItemRelation = subItemRelation;
		this.inverseSubItemRelation = invertEdges(subItemRelation);
		this.unreachedItems = new HashSet<>(subItemRelation.keySet());
		this.rootItems = new HashSet<>();
	}

	private static Map<DesignItem, Set<DesignItem>> invertEdges(Map<DesignItem, Map<String, DesignItem>> graph) {
		Map<DesignItem, Set<DesignItem>> inverse = new HashMap<>();
		for (DesignItem item : graph.keySet()) {
			inverse.put(item, new HashSet<>());
		}
		for (Map.Entry<DesignItem, Map<String, DesignItem>> predecessorEntry : graph.entrySet()) {
			DesignItem predecessor = predecessorEntry.getKey();
			for (DesignItem successor : predecessorEntry.getValue().values()) {
				inverse.get(successor).add(predecessor);
			}
		}
		return inverse;
	}

	public Set<DesignItem> getRootItems() {
		return rootItems;
	}

	public void findRootItems() {

		// any item with 0 or 2+ predecessors must be a root
		for (Map.Entry<DesignItem, Set<DesignItem>> inverseGraphEntry : inverseSubItemRelation.entrySet()) {
			if (inverseGraphEntry.getValue().size() != 1) {
				makeRoot(inverseGraphEntry.getKey());
			}
		}

		// find best unreached items to break cycles
		while (true) {
			DesignItem item = findBestUnreachedItem();
			if (item == null) {
				break;
			}
			makeRoot(item);
		}

		// fallback: all items should be reached now, but in case one isn't, we make it a root to avoid errors
		while (!unreachedItems.isEmpty()) {
			makeRoot(unreachedItems.iterator().next());
		}

	}

	private void makeRoot(DesignItem item) {
		rootItems.add(item);
		reach(item);
	}

	private void reach(DesignItem item) {
		if (unreachedItems.remove(item)) {
			for (DesignItem subItem : subItemRelation.get(item).values()) {
				if (inverseSubItemRelation.get(subItem).size() == 1) {
					reach(subItem);
				}
			}
		}
	}

	private DesignItem findBestUnreachedItem() {
		DesignItem bestItem = null;
		int bestScore = -1;
		for (DesignItem item : unreachedItems) {
			int score = calculateScore(item);
			if (score > bestScore) {
				bestItem = item;
				bestScore = score;
			}
		}
		return bestItem;
	}

	private int calculateScore(DesignItem item) {

		// signals and statements are useless if not used anywhere, so they make very bad roots
		if (item instanceof Signal || item instanceof Statement) {
			return 0;
		}

		// components make very good roots
		if (item instanceof Component) {
			return 1000;
		}

		// standard score
		return 1;

	}

}
