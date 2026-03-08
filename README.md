# Hierarchy Filter Implementation

This repository contains the implementation of a Hierarchy Filter function designed to filter nodes in a forest-like structure based on a predicate. The solution demonstrates my expertise in Kotlin, data structures, and algorithmic problem solving. Below is a detailed explanation of the problem, the solution, and how to test it.

Problem Overview

The task revolves around implementing a function that filters nodes from a hierarchical data structure (forest of trees) based on a predicate. The hierarchy is represented by two arrays:

Node IDs: A list of unique node identifiers.

Depths: A list of corresponding depths for each node (root nodes have depth 0, children have depth 1, etc.).

The filter should only include nodes where both the node and all its ancestors pass a given predicate.

Solution

The filter() function was implemented in Kotlin to traverse the hierarchy, apply the predicate, and ensure that the children of a node are only included if their parent node passes the predicate.

Key Features:

Depth Tracking: Nodes are filtered based on both their own predicate and the predicate of all their ancestors. If a parent node fails the predicate, all of its children are excluded.

Efficient DFS Traversal: The filter traverses the hierarchy in a depth-first search (DFS) order, ensuring that all ancestors of a node are checked before the node itself is processed.

Correct Depth Adjustment: After filtering, the depth of each remaining node is recalculated based on the depth of its passing ancestors.

Data Structure

Hierarchy is represented using two parallel arrays:

nodeIds[]: Array storing the node identifiers in DFS order.

depths[]: Array storing the depths of each node.

Code Walkthrough:
fun Hierarchy.filter(nodeIdPredicate: (Int) -> Boolean): Hierarchy {
    // Create lists to hold filtered node IDs and corresponding depths
    val filteredNodeIds = mutableListOf<Int>()
    val filteredDepths = mutableListOf<Int>()

    // Stack to track ancestor predicates
    val ancestorPassedStack = mutableListOf<Boolean>()

    // Iterate through the hierarchy nodes
    for (i in 0 until size) {
        val node = nodeId(i)
        val depth = depth(i)

        // Remove ancestors that are deeper than the current node's depth
        while (ancestorPassedStack.size > depth) {
            ancestorPassedStack.removeAt(ancestorPassedStack.size - 1)
        }

        // Check if all ancestors pass the predicate
        val ancestorsPassed = ancestorPassedStack.all { it }

        // Node is included if it and all ancestors pass the predicate
        val include = nodeIdPredicate(node) && ancestorsPassed

        // Update ancestor stack for future checks
        ancestorPassedStack.add(include)

        // If the node is included, add it to the filtered lists
        if (include) {
            filteredNodeIds.add(node)
            filteredDepths.add(ancestorPassedStack.count { it } - 1)
        }
    }

    // Return a new hierarchy with the filtered nodes and their depths
    return ArrayBasedHierarchy(filteredNodeIds.toIntArray(), filteredDepths.toIntArray())
}
Testing

The solution includes comprehensive unit tests using JUnit 5 to validate the functionality of the filter() function. The tests cover a range of scenarios, including:

Empty Hierarchy: Verifying that an empty hierarchy returns an empty result.

Predicate Always Passing: Ensuring the hierarchy remains unchanged when all nodes pass the predicate.

Predicate Always Failing: Checking that no nodes remain when the predicate always fails.

Child Nodes Exclusion: Ensuring that child nodes are excluded if their parent fails the predicate.

Multiple Roots: Verifying filtering works across multiple independent trees in the forest.

Deep Hierarchy Filtering: Testing that descendants of a failing node are excluded.

Original Example: Using the example from the problem statement to ensure correct filtering behavior.

Example Test Case:
@Test
fun `child nodes excluded if parent fails predicate`() {
    val hierarchy: Hierarchy = ArrayBasedHierarchy(
        intArrayOf(1, 2, 3, 4),
        intArrayOf(0, 1, 2, 1)
    )
    val filtered = hierarchy.filter { it != 2 }

    val expected: Hierarchy = ArrayBasedHierarchy(
        intArrayOf(1, 4),
        intArrayOf(0, 1)
    )

    assertEquals(expected.formatString(), filtered.formatString())
}
Run Tests:

To run the tests, execute the following command:

./gradlew test
Dependencies

Kotlin 1.5+: The solution is implemented using Kotlin.

JUnit 5: For testing the functionality.

How to Use

    Clone this repository

    Navigate to the project directory

    Add your custom Hierarchy structure and use the filter() function to apply your predicate.

Example Usage:
val hierarchy: Hierarchy = ArrayBasedHierarchy(
    intArrayOf(1, 2, 3, 4),
    intArrayOf(0, 1, 2, 1)
)

val filtered = hierarchy.filter { it % 2 == 0 }

println(filtered.formatString())
Conclusion

This solution demonstrates my ability to work with Kotlin, data structures, and algorithm design. I implemented a recursive filtering approach to handle hierarchical data while maintaining depth integrity and ancestor relationships.
