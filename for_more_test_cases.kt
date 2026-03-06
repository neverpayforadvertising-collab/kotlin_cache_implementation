class FilterTestsWithOutput {

    @Test
    fun `empty hierarchy returns empty result`() {
        // Create an empty hierarchy with no nodes
        val emptyHierarchy: Hierarchy = ArrayBasedHierarchy(IntArray(0), IntArray(0))
        
        // Apply the filter (any predicate) on the empty hierarchy
        val filtered = emptyHierarchy.filter { it > 0 }
        
        // Print the filtered hierarchy in a readable format
        println("Empty hierarchy filtered: ${filtered.formatString()}")
        
        // Assert that the result is indeed empty
        assertEquals(0, filtered.size)
    }

    @Test
    fun `all nodes pass predicate returns identical hierarchy`() {
        // Create a simple hierarchy with 3 nodes in a straight DFS order
        val hierarchy: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3),
            intArrayOf(0, 1, 2)
        )
        
        // Filter with a predicate that always returns true
        val filtered = hierarchy.filter { true }
        
        // Print the filtered hierarchy; should be identical to the original
        println("All nodes pass predicate: ${filtered.formatString()}")
        
        // Verify the filtered hierarchy matches the original
        assertEquals(hierarchy.formatString(), filtered.formatString())
    }

    @Test
    fun `no nodes pass predicate returns empty hierarchy`() {
        // Create a simple hierarchy with 3 nodes
        val hierarchy: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3),
            intArrayOf(0, 1, 2)
        )
        
        // Filter with a predicate that always returns false
        val filtered = hierarchy.filter { false }
        
        // Print the result; should be empty
        println("No nodes pass predicate: ${filtered.formatString()}")
        
        // Assert that no nodes remain after filtering
        assertEquals(0, filtered.size)
    }

    @Test
    fun `child nodes excluded if parent fails predicate`() {
        // Create a hierarchy where node 2 has a child node 3
        val hierarchy: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4),
            intArrayOf(0, 1, 2, 1)
        )
        
        // Filter out node 2; children of node 2 should also be excluded
        val filtered = hierarchy.filter { it != 2 }
        
        // Expected result contains only nodes 1 and 4
        val expected: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 4),
            intArrayOf(0, 1)
        )
        
        // Print the filtered hierarchy for clarity
        println("Child nodes excluded when parent fails: ${filtered.formatString()}")
        
        // Verify the filtered hierarchy matches expectations
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun `filter works correctly on multiple roots`() {
        // Create a forest with multiple root nodes
        val hierarchy: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5, 6),
            intArrayOf(0, 1, 0, 1, 0, 1)
        )
        
        // Keep only nodes with odd IDs
        val filtered = hierarchy.filter { it % 2 != 0 }
        
        // Expected result includes odd-numbered root nodes only
        val expected: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 3, 5),
            intArrayOf(0, 0, 0)
        )
        
        // Print filtered hierarchy to show effect of filtering across multiple roots
        println("Filter on multiple roots (odd nodes): ${filtered.formatString()}")
        
        // Assert the result matches expected hierarchy
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun `deep hierarchy excludes descendants of failing nodes`() {
        // Create a deep hierarchy where nodes 4, 5, 6 are nested under each other
        val hierarchy: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5, 6),
            intArrayOf(0, 1, 2, 3, 4, 5)
        )
        
        // Filter out node 4; its children (5 and 6) should also be removed
        val filtered = hierarchy.filter { it != 4 }
        
        // Expected result contains only nodes 1, 2, 3
        val expected: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3),
            intArrayOf(0, 1, 2)
        )
        
        // Print filtered hierarchy to verify deep exclusion works
        println("Deep hierarchy filtered (excluding node 4 and descendants): ${filtered.formatString()}")
        
        // Assert filtered hierarchy matches expected
        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun `original example test`() {
        // Recreate the original example hierarchy from the problem statement
        val unfiltered: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            intArrayOf(0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2)
        )
        
        // Filter out nodes divisible by 3
        val filteredActual: Hierarchy = unfiltered.filter { nodeId -> nodeId % 3 != 0 }
        
        // Expected filtered hierarchy based on the same rules
        val filteredExpected: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 5, 8, 10, 11),
            intArrayOf(0, 1, 1, 0, 1, 2)
        )
        
        // Print filtered hierarchy to visually confirm correctness
        println("Original example filtered: ${filteredActual.formatString()}")
        
        // Assert the filtered hierarchy matches the expected outcome
        assertEquals(filteredExpected.formatString(), filteredActual.formatString())
    }
}