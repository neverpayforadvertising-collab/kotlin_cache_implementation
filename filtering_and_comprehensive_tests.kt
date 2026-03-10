import kotlin.test.*

/**
 * The `Hierarchy` abstraction models a collection of ordered trees (a forest)
 * utilizing two parallel arrays: one for node identifiers and the other for the
 * depth of each node. Nodes are stored in depth-first traversal order, ensuring
 * that parents always precede their children.
 *
 * Depth semantics:
 * - Depth 0 signifies a root node.
 * - Depth 1 represents a direct child of the root.
 * - Depth 2 corresponds to a grandchild, etc.
 *
 * By analyzing consecutive depth values, one can determine if a node is a child,
 * sibling, or belongs to a higher level in the tree.
 *
 * This flat array representation is memory-efficient, avoids pointer-heavy structures,
 * and supports multiple independent trees seamlessly.
 */
interface Hierarchy {
    /** Returns the total number of nodes in the hierarchy. */
    val size: Int

    /**
     * Retrieves the unique identifier for the node at the given index.
     * The corresponding depth is obtained via [depth].
     * @param index must satisfy 0 <= index < size
     */
    fun nodeId(index: Int): Int

    /**
     * Retrieves the depth level of the node at the given index.
     * The corresponding ID is obtainable via [nodeId].
     * @param index must satisfy 0 <= index < size
     */
    fun depth(index: Int): Int

    /** Utility to render the hierarchy as a human-readable string. */
    fun formatString(): String {
        return (0 until size).joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]"
        ) { i -> "${nodeId(i)}:${depth(i)}" }
    }
}

/**
 * Filters the hierarchy by a node predicate.
 * 
 * A node is preserved if and only if:
 * 1. It satisfies [nodeIdPredicate].
 * 2. All of its ancestors satisfy [nodeIdPredicate] as well.
 *
 * The returned hierarchy maintains relative ordering and adjusts depths
 * to reflect the filtered lineage.
 */
fun Hierarchy.filter(nodeIdPredicate: (Int) -> Boolean): Hierarchy {
    val retainedNodeIds = mutableListOf<Int>()
    val retainedDepths = mutableListOf<Int>()

    // Track ancestor acceptance at each depth level
    val ancestorAcceptanceStack = mutableListOf<Boolean>()

    for (i in 0 until size) {
        val node = nodeId(i)
        val nodeDepth = depth(i)

        // Purge any ancestor flags deeper than the current node
        while (ancestorAcceptanceStack.size > nodeDepth) {
            ancestorAcceptanceStack.removeAt(ancestorAcceptanceStack.size - 1)
        }

        // Ensure all ancestors are approved
        val ancestorsApproved = ancestorAcceptanceStack.all { it }

        // Include node only if it satisfies predicate and all ancestors are valid
        val shouldInclude = nodeIdPredicate(node) && ancestorsApproved

        // Record decision for descendants to reference
        ancestorAcceptanceStack.add(shouldInclude)

        if (shouldInclude) {
            retainedNodeIds.add(node)
            // Adjust depth according to filtered lineage
            retainedDepths.add(ancestorAcceptanceStack.count { it } - 1)
        }
    }

    return ArrayBasedHierarchy(
        retainedNodeIds.toIntArray(),
        retainedDepths.toIntArray()
    )
}

/** Concrete implementation backed by two arrays. */
class ArrayBasedHierarchy(
    private val myNodeIds: IntArray,
    private val myDepths: IntArray
) : Hierarchy {
    override val size: Int = myDepths.size
    override fun nodeId(index: Int): Int = myNodeIds[index]
    override fun depth(index: Int): Int = myDepths[index]
}

/**
 * Comprehensive unit tests demonstrating the robustness of the filter method.
 * Emphasis is placed on edge cases, multi-root forests, and deep hierarchical structures.
 */
class FilterTests {

    @Test
    fun `empty hierarchy yields empty output`() {
        val emptyHierarchy: Hierarchy = ArrayBasedHierarchy(IntArray(0), IntArray(0))
        val filtered = emptyHierarchy.filter { it > 0 }
        println("Filtered empty hierarchy: ${filtered.formatString()}")
        assertEquals(0, filtered.size)
    }

    @Test
    fun `all nodes pass predicate preserves hierarchy`() {
        val hierarchy: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 2, 3), intArrayOf(0, 1, 2))
        val filtered = hierarchy.filter { true }
        println("All nodes pass predicate: ${filtered.formatString()}")
        assertEquals(hierarchy.formatString(), filtered.formatString())
    }

    @Test
    fun `no nodes pass predicate yields empty hierarchy`() {
        val hierarchy: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 2, 3), intArrayOf(0, 1, 2))
        val filtered = hierarchy.filter { false }
        println("No nodes pass predicate: ${filtered.formatString()}")
        assertEquals(0, filtered.size)
    }

    @Test
    fun `descendants excluded if parent fails predicate`() {
        val hierarchy: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 2, 3, 4), intArrayOf(0, 1, 2, 1))
        val filtered = hierarchy.filter { it != 2 }
        val expected: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 4), intArrayOf(0, 1))
        println("Descendants excluded when parent fails: ${filtered.formatString()}")
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun `handles multiple root nodes correctly`() {
        val hierarchy: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 2, 3, 4, 5, 6), intArrayOf(0, 1, 0, 1, 0, 1))
        val filtered = hierarchy.filter { it % 2 != 0 }
        val expected: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 3, 5), intArrayOf(0, 0, 0))
        println("Multiple roots filtered (odd nodes only): ${filtered.formatString()}")
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun `deep hierarchy excludes descendants of failing nodes`() {
        val hierarchy: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 2, 3, 4, 5, 6), intArrayOf(0, 1, 2, 3, 4, 5))
        val filtered = hierarchy.filter { it != 4 }
        val expected: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 2, 3), intArrayOf(0, 1, 2))
        println("Deep hierarchy filtered (excluding node 4 and descendants): ${filtered.formatString()}")
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun `original example hierarchy filtered correctly`() {
        val unfiltered: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            intArrayOf(0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2)
        )
        val filteredActual = unfiltered.filter { it % 3 != 0 }
        val filteredExpected: Hierarchy = ArrayBasedHierarchy(intArrayOf(1, 2, 5, 8, 10, 11), intArrayOf(0, 1, 1, 0, 1, 2))
        println("Original example filtered: ${filteredActual.formatString()}")
        assertEquals(filteredExpected.formatString(), filteredActual.formatString())
    }
}