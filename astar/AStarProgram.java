import java.io.*;
import java.util.*;

public class AStarProgram {

    // Node entry class for Priority Queue
    private static class PriorityNode {
        int nodeId;
        double fValue;
        double gValue;
        
        PriorityNode(int nodeId, double fValue, double gValue) { 
            this.nodeId = nodeId; 
            this.fValue = fValue; 
            this.gValue = gValue; 
        }
    }

    public interface Heuristic {
        double h(Types.Node a, Types.Node b);
    }

    // Main A* algorithm function
    public static Types.PathResult astar(Types.Graph graph, int startNode, int targetNode, Heuristic heuristic) {
        long startTime = System.nanoTime();

        // Initialize g-costs to infinity for all nodes
        Map<Integer, Double> gCosts = new HashMap<>();
        for (Integer nodeId : graph.nodes.keySet()) {
            gCosts.put(nodeId, Double.POSITIVE_INFINITY);
        }
        gCosts.put(startNode, 0.0);

        // Map for parent tracking
        Map<Integer, Integer> parentMap = new HashMap<>();

        // Priority queue - sorted by f value
        PriorityQueue<PriorityNode> openList = new PriorityQueue<>(new Comparator<PriorityNode>() {
            @Override
            public int compare(PriorityNode nodeA, PriorityNode nodeB) {
                int fComparison = Double.compare(nodeA.fValue, nodeB.fValue);
                if (fComparison != 0) return fComparison;
                return Integer.compare(nodeA.nodeId, nodeB.nodeId);
            }
        });


        // Add start node to priority queue
        double initialF = heuristic.h(graph.nodes.get(startNode), graph.nodes.get(targetNode));
        openList.add(new PriorityNode(startNode, initialF, 0.0));
        
        int totalPushes = 1;
        int nodesExpanded = 0;
        int maxQueueSize = Math.max(1, openList.size());

        // Check if start and target nodes exist
        if (!graph.nodes.containsKey(startNode) || !graph.nodes.containsKey(targetNode)) {
            double executionTime = (System.nanoTime() - startTime) / 1e9;
            return new Types.PathResult(false, 0.0, Collections.emptyList(), 
                                      nodesExpanded, totalPushes, maxQueueSize, executionTime);
        }

        // Main search loop
        while (!openList.isEmpty()) {
            PriorityNode currentNode = openList.poll();

            // Skip if this node was already expanded with better g-cost
            double currentBestG = gCosts.getOrDefault(currentNode.nodeId, Double.POSITIVE_INFINITY);
            if (currentNode.gValue > currentBestG) continue;

            nodesExpanded++;

            // Check if we reached the target
            if (currentNode.nodeId == targetNode) {
                double executionTime = (System.nanoTime() - startTime) / 1e9;
                // Reconstruct path backwards
                List<Integer> finalPath = new ArrayList<>();
                int pathNode = targetNode;
                while (pathNode != startNode) {
                    finalPath.add(pathNode);
                    pathNode = parentMap.get(pathNode);
                }
                finalPath.add(startNode);
                Collections.reverse(finalPath);
                return new Types.PathResult(true, gCosts.get(targetNode), finalPath, 
                                          nodesExpanded, totalPushes, maxQueueSize, executionTime);
            }

            // Examine neighbor nodes
            List<Types.Edge> neighbors = graph.adj.get(currentNode.nodeId);
            if (neighbors != null) {
                for (Types.Edge edge : neighbors) {
                    double newG = currentNode.gValue + edge.weight;
                    double previousG = gCosts.getOrDefault(edge.to, Double.POSITIVE_INFINITY);
                    
                    // If new path is better
                    if (newG < previousG) {
                        gCosts.put(edge.to, newG);
                        parentMap.put(edge.to, currentNode.nodeId);
                        double newF = newG + heuristic.h(graph.nodes.get(edge.to), graph.nodes.get(targetNode));
                        openList.add(new PriorityNode(edge.to, newF, newG));
                        totalPushes++;
                    }
                }
            }

            // Update maximum queue size
            if (openList.size() > maxQueueSize) maxQueueSize = openList.size();
        }

        // Path not found
        double executionTime = (System.nanoTime() - startTime) / 1e9;
        return new Types.PathResult(false, 0.0, Collections.emptyList(), 
                                  nodesExpanded, totalPushes, maxQueueSize, executionTime);
    }

    // Helper function to print results
    private static void printResult(String algorithmMode, Types.PathResult result) {
        System.out.println("ALGORITHM: " + algorithmMode);
        
        if (!result.found) {
            System.out.println("Optimal cost: NO PATH");
        } else {
            // Format cost
            String costString = (result.cost % 1.0 == 0.0) ? 
                String.format("%.0f", result.cost) : String.format("%.6f", result.cost);
            System.out.println("Optimal cost: " + costString);
            
            // Print path
            System.out.print("Path: ");
            for (int i = 0; i < result.path.size(); ++i) {
                System.out.print(result.path.get(i));
                if (i + 1 < result.path.size()) System.out.print(" -> ");
            }
            System.out.println();
        }
        
        System.out.println("Expanded: " + result.expanded);
        System.out.println("Pushes: " + result.pushes);
        System.out.println("Max frontier: " + result.maxFrontier);
        System.out.printf(Locale.ROOT, "Runtime (s): %.6f%n%n", result.runtime_s);
    }

    public static void main(String[] args) {
        // Determine file path - use command line argument if provided, otherwise use default file
        String inputFile;
        if (args.length > 0) {
            inputFile = args[0];
        } else {
            // Default file path for this specific machine
            inputFile = "C:\\Users\\ASUS\\Desktop\\Tebriz\\Duolingo\\AITASK-master\\astar\\astar_medium.txt";
        }
        
        // Read graph file
        Types.Graph graph;
        try {
            graph = Graph.parseGraph(inputFile);
        } catch (IOException e) {
            System.err.println("Failed to read graph file: " + e.getMessage());
            return;
        }

        // Define heuristic functions
        Heuristic zeroHeuristic = Heuristics::hZero;
        Heuristic euclideanHeuristic = Heuristics::hEuclidean;
        Heuristic manhattanHeuristic = Heuristics::hManhattan;

        // Perform search with three different algorithms
        System.out.println("=== A* Algorithm Comparison ===\n");
        
        Types.PathResult result1 = astar(graph, graph.source, graph.destination, zeroHeuristic);
        printResult("Uniform Cost Search (h=0)", result1);

        Types.PathResult result2 = astar(graph, graph.source, graph.destination, euclideanHeuristic);
        printResult("A* with Euclidean Distance", result2);

        Types.PathResult result3 = astar(graph, graph.source, graph.destination, manhattanHeuristic);
        printResult("A* with Manhattan Distance", result3);
    }
}