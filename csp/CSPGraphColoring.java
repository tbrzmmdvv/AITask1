import java.io.*;
import java.util.*;

/**
 * CSP Graph Coloring Solver
 * Simple backtracking implementation for graph coloring
 */
public class CSPGraphColoring {
    
    private Map<Integer, List<Integer>> graph;
    private Map<Integer, Integer> colors;
    private int numColors;
    
    public void parseProblem(String filename) throws IOException {
        graph = new HashMap<>();
        colors = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                if (line.startsWith("colors=")) {
                    numColors = Integer.parseInt(line.substring(7).trim());
                } else if (line.contains(",")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        int u = Integer.parseInt(parts[0].trim());
                        int v = Integer.parseInt(parts[1].trim());
                        
                        if (u == v) {
                            System.out.println("failure");
                            System.exit(0);
                        }
                        
                        graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
                        graph.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
                    }
                }
            }
        }
    }
    
    public boolean solve() {
        List<Integer> nodes = new ArrayList<>(graph.keySet());
        Collections.sort(nodes);
        return backtrack(nodes, 0);
    }
    
    private boolean backtrack(List<Integer> nodes, int index) {
        if (index == nodes.size()) {
            return true;
        }
        
        int node = nodes.get(index);
        
        for (int color = 1; color <= numColors; color++) {
            if (isValidColor(node, color)) {
                colors.put(node, color);
                
                if (backtrack(nodes, index + 1)) {
                    return true;
                }
                
                colors.remove(node);
            }
        }
        
        return false;
    }
    
    private boolean isValidColor(int node, int color) {
        if (graph.containsKey(node)) {
            for (int neighbor : graph.get(node)) {
                if (colors.containsKey(neighbor) && colors.get(neighbor) == color) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void printSolution() {
        if (colors.isEmpty()) {
            System.out.println("failure");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("SOLUTION: {");
        
        List<Integer> sortedNodes = new ArrayList<>(colors.keySet());
        Collections.sort(sortedNodes);
        
        for (int i = 0; i < sortedNodes.size(); i++) {
            int node = sortedNodes.get(i);
            int color = colors.get(node);
            sb.append(node).append(": ").append(color);
            if (i < sortedNodes.size() - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        System.out.println(sb.toString());
    }
    
    public static void main(String[] args) {
        String filename;
        if (args.length > 0) {
            filename = args[0];
        } else {
            // Default file path for this specific machine
            filename = "C:\\Users\\ASUS\\Desktop\\Tebriz\\Duolingo\\AITASK-master\\csp\\csp_small.txt";
        }
        
        CSPGraphColoring solver = new CSPGraphColoring();
        
        try {
            solver.parseProblem(filename);
            boolean solved = solver.solve();
            
            if (solved) {
                solver.printSolution();
            } else {
                System.out.println("failure");
            }
            
        } catch (IOException e) {
            System.err.println("Failed to read CSP file: " + e.getMessage());
        }
    }
}