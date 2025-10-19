import java.util.*;

/**
 * Data structures used by the program
 * Contains Node, Edge, Graph and PathResult classes
 */
public class Types {
    
    /**
     * Graph node - each node has an ID and coordinates
     */
    public static class Node {
        public int id;
        public int x;
        public int y;
        
        public Node(int id, int x, int y) { 
            this.id = id; 
            this.x = x; 
            this.y = y; 
        }

        @Override
        public String toString() {
            return String.format("Node{id=%d, x=%d, y=%d}", id, x, y);
        }
    }

    /**
     * Graph edge - represents connection between two nodes
     */
    public static class Edge {
        public int to;
        public double weight;
        
        public Edge(int to, double weight) { 
            this.to = to; 
            this.weight = weight; 
        }

        @Override
        public String toString() {
            String weightStr = (weight % 1.0 == 0.0) ? 
                String.format("%.0f", weight) : String.format("%.6f", weight);
            return String.format("Edge{to=%d, weight=%s}", to, weightStr);
        }
    }

    /**
     * Graph data structure - holds nodes, edges and start/destination information
     */
    public static class Graph {
        public Map<Integer, Node> nodes = new HashMap<>();
        public Map<Integer, List<Edge>> adj = new HashMap<>();
        public int source = -1;
        public int destination = -1;

        @Override
        public String toString() {
            int totalEdges = adj.values().stream().mapToInt(List::size).sum() / 2; // undirected edges counted twice
            return String.format("Graph{nodes=%d, edges=%d, source=%d, destination=%d}",
                    nodes.size(), totalEdges, source, destination);
        }
    }

    /**
     * A* algorithm result data structure
     * Contains algorithm performance metrics and found path
     */
    public static class PathResult {
        public boolean found;
        public double cost;
        public List<Integer> path;
        public int expanded;
        public int pushes;
        public int maxFrontier;
        public double runtime_s;

        public PathResult(boolean found, double cost, List<Integer> path,
                          int expanded, int pushes, int maxFrontier, double runtime_s) {
            this.found = found;
            this.cost = cost;
            this.path = path;
            this.expanded = expanded;
            this.pushes = pushes;
            this.maxFrontier = maxFrontier;
            this.runtime_s = runtime_s;
        }

        @Override
        public String toString() {
            if (!found) {
                return String.format("PathResult{found=false, expanded=%d, pushes=%d, maxFrontier=%d, runtime_s=%.6f}",
                        expanded, pushes, maxFrontier, runtime_s);
            } else {
                String costStr = (cost % 1.0 == 0.0) ? 
                    String.format("%.0f", cost) : String.format("%.6f", cost);
                return String.format("PathResult{found=true, cost=%s, path=%s, expanded=%d, pushes=%d, maxFrontier=%d, runtime_s=%.6f}",
                        costStr, path, expanded, pushes, maxFrontier, runtime_s);
            }
        }
    }
}