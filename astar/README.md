```markdown
# A* Algorithm Implementation

This project demonstrates the A* search algorithm. Goal: Find the shortest path in a graph from start node to goal node and see how different heuristic functions change the search.

This guide is written for beginners - short and easy to understand.

Files (what they do)
- AStarProgram.java — main program. A* algorithm is here.
- Graph.java — reads input file and builds the graph.
- Heuristics.java — three heuristics: zero (h=0), Euclidean (straight-line), Manhattan.
- Types.java — definitions for Node, Edge, Graph, and results.
- astar_small.txt / astar_medium.txt — example graph files.

How to compile and run
1) Compile in terminal:
   javac AStarProgram.java Graph.java Heuristics.java Types.java

2) Run:
   java AStarProgram astar_small.txt
   or
   java AStarProgram astar_medium.txt

Do the compile step first, then the run step.

Input file format — very simple
- Skip blank lines and lines starting with #
- Each line is one of:
  - Vertex: <id>,<cell_id>
  - Edge: <u>,<v>,<w>
  - Source: S,<id>
  - Destination: D,<id>

Example:
Vertex: 1,34
Edge: 1,2,5
Source: S,1
Destination: D,5

cell_id gives grid coordinates:
- x = cell_id / 10 (integer division)
- y = cell_id % 10
Example: cell_id = 34 => x = 3, y = 4.

What is a heuristic? (short)
- A heuristic estimates how far a node is from the goal.
- This program has three choices:
  - h = 0 (no guess) — same as Uniform Cost Search (UCS).
  - Euclidean — straight-line distance.
  - Manhattan — grid distance (only up/down/left/right).

If a heuristic never overestimates the true cost, A* will find the optimal path.

What the program prints (output)
For each run you will see:
- Expanded — number of nodes actually expanded (popped and expanded).
- Pushes — how many times something was pushed into the priority queue.
- Max frontier — the maximum size of the queue while running.
- Runtime — how long it took (seconds).

The algorithm uses f = g + h:
- g is the cost so far from the start.
- h is the heuristic estimate to the goal.

Why results should match
- If heuristics are admissible (do not overestimate), all modes should return the same optimal cost.
- Usually, work done follows: UCS (h=0) >= A* (Euclidean) >= A* (Manhattan) — if Manhattan gives stronger (larger but admissible) estimates than Euclidean.

Simple test example
Contents of a tiny input:
Vertex: 1,11
Vertex: 2,12
Vertex: 3,13
Edge: 1,2,1
Edge: 2,3,1
Source: S,1
Destination: D,3

Shortest path cost here is 2.

Quick troubleshooting
- If results differ:
  - Check cell_id -> (x,y) calculation.
  - Check edge weights are correct.
  - Check heuristic formulas (they should not overestimate).
- The program allows duplicate entries in the frontier, but it only expands an entry if its g value equals the best known g for that node. This prevents expanding old/stale entries.

Next steps (optional help I can give)
- Add a command-line option to choose the heuristic (e.g., --heuristic=manhattan).
- Output results as JSON for automatic analysis.
- Create a small graph visualizer.