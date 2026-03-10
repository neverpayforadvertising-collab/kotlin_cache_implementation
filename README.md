Hierarchy Filter Utility (Kotlin)

A high-performance Kotlin utility to manage and filter hierarchical tree structures (forests) with ancestor-aware filtering, designed for maintainability, clarity, and production-readiness.

Overview

Models a forest (multiple ordered trees) using:

nodeIds — unique identifiers for nodes

depths — depth of each node in DFS order

Relationships (parent-child) are derived from depth and sequence — no linked objects needed

Supports multiple independent root nodes in a single hierarchy

Key Features

✅ Ancestor-aware filtering

A node is retained only if it and all ancestors pass a predicate

✅ Depth recalculation after filtering

Maintains correct lineage in the resulting hierarchy

✅ Array-backed implementation

Efficient memory footprint and sequential access

✅ Comprehensive unit tests

Covers empty hierarchies, all-pass, all-fail, deep trees, multiple roots, and ancestor-descendant exclusion

Usage Example
val hierarchy: Hierarchy = ArrayBasedHierarchy(
    intArrayOf(1, 2, 3, 4, 5),
    intArrayOf(0, 1, 2, 1, 0)
)

val filtered: Hierarchy = hierarchy.filter { it % 2 != 0 }

println(filtered.formatString()) // Output: [1:0, 5:0]
Benefits

• Clean, readable code adhering to senior-level standards

• Predictable behavior for deep and complex hierarchies

• Easily extensible for future hierarchy operations

• Minimal memory overhead and efficient DFS traversal

• Ideal for production-grade Kotlin applications handling hierarchical data