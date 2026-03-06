fun Hierarchy.filter(nodeIdPredicate: (Int) -> Boolean): Hierarchy {
    // Create a list to hold the node IDs that pass the filter
    val filteredNodeIds = mutableListOf<Int>()
    
    // Create a list to hold the corresponding depths of the filtered nodes
    val filteredDepths = mutableListOf<Int>()
    
    // Stack to track whether each ancestor at each depth has passed the predicate
    val ancestorPassedStack = mutableListOf<Boolean>()
    
    // Iterate through all nodes in the hierarchy in DFS order
    for (i in 0 until size) {
        // Retrieve the unique ID of the current node
        val node = nodeId(i)
        
        // Retrieve the depth of the current node in its tree
        val depth = depth(i)
        
        // Remove any ancestor flags from the stack that are deeper than the current node's depth
        // This ensures the stack always reflects only the current node's ancestor lineage
        while (ancestorPassedStack.size > depth) {
            ancestorPassedStack.removeAt(ancestorPassedStack.size - 1)
        }
        
        // Determine if all ancestors of the current node have passed the predicate
        val ancestorsPassed = ancestorPassedStack.all { it }
        
        // Include this node only if it passes the predicate and all ancestors passed
        val include = nodeIdPredicate(node) && ancestorsPassed
        
        // Push the result for this node onto the stack for future children to reference
        ancestorPassedStack.add(include)
        
        // If this node should be included in the filtered hierarchy
        if (include) {
            // Add the node ID to the list of filtered nodes
            filteredNodeIds.add(node)
            
            // Add the adjusted depth for the filtered node
            // Depth is based on how many nodes in the current lineage passed the filter
            filteredDepths.add(ancestorPassedStack.count { it } - 1)
        }
    }
    
    // Return a new ArrayBasedHierarchy containing only the filtered nodes and their adjusted depths
    return ArrayBasedHierarchy(
        filteredNodeIds.toIntArray(),
        filteredDepths.toIntArray()
    )
}
