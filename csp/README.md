# CSP Graph Coloring Solver

This project implements a Constraint Satisfaction Problem (CSP) solver for graph coloring using Backtracking with MRV, LCV, and AC-3 heuristics.

## Files

- `CSPGraphColoring.java` — Main CSP solver implementation
- `csp_small.txt` — Small test case (3-colorable graph)
- `csp_tight.txt` — Tight test case (requires all k colors)

## Algorithm Features

### Core Algorithm: Backtracking
- Systematic search through the solution space
- Backtracks when constraints are violated

### Heuristics:
1. **MRV (Minimum Remaining Values)**: Selects the variable with the smallest current domain size
2. **LCV (Least Constraining Value)**: Orders values by how few values they eliminate from neighbors
3. **AC-3 (Arc Consistency)**: Maintains arc consistency after each assignment

### Constraint Handling:
- Variables: Vertices present in any edge
- Domains: {1, 2, ..., k} where k is the number of colors
- Constraint: If (u,v) is an edge, then color[u] ≠ color[v]

## Input Format

```
colors=<k>
<vertex1>,<vertex2>
<vertex1>,<vertex3>
...
```

Example:
```
colors=3
1,2
2,3
3,1
3,4
```

## Output Format

- If solved: `SOLUTION: {1: 1, 2: 2, 3: 3, 4: 1}`
- If unsolvable: `failure`

## How to Compile and Run

1. Compile:
   ```
   javac CSPGraphColoring.java
   ```

2. Run:
   ```
   java CSPGraphColoring csp_small.txt
   java CSPGraphColoring csp_tight.txt
   ```

## Edge Cases Handled

- Isolated vertices: Still get assigned a color
- Duplicate edges: Normalized and stored once
- Self-loops: Immediate failure (impossible constraint)
- k=1 on graphs needing ≥2 colors: Returns failure

## Test Cases

### csp_small.txt
- 4 vertices, 4 edges
- 3-colorable graph (simple cycle with tail)
- Expected: Valid solution with 3 colors

### csp_tight.txt
- 4 vertices, 6 edges (complete graph K4)
- Requires all 4 colors
- Expected: Valid solution using all 4 colors

## Implementation Details

- Uses trail-based backtracking for efficient domain restoration
- AC-3 maintains arc consistency after each assignment
- MRV and LCV heuristics optimize variable and value selection
- Handles all edge cases specified in the requirements
