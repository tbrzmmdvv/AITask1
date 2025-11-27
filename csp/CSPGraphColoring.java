import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CSPGraphColoring {

    private final int numColors;
    private final Set<Integer> variables = new HashSet<>();
    private final Map<Integer, List<Integer>> adj = new HashMap<>();
    private Map<Integer, Set<Integer>> domains = new HashMap<>();

    public CSPGraphColoring(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();

        // 1. Read number of colors
        if (line != null && line.startsWith("colors=")) {
            this.numColors = Integer.parseInt(line.substring(7).trim());
        } else {
            throw new IllegalArgumentException("Input file must start with 'colors=<k>'");
        }

        // 2. Read edges and build graph
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length != 2) continue;

            int u = Integer.parseInt(parts[0].trim());
            int v = Integer.parseInt(parts[1].trim());

            variables.add(u);
            variables.add(v);

            adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
            adj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
        }
        reader.close();

        // 3. Initialize domains for all variables
        Set<Integer> initialColors = new HashSet<>();
        for (int i = 1; i <= numColors; i++) {
            initialColors.add(i);
        }
        for (Integer var : variables) {
            domains.put(var, new HashSet<>(initialColors));
        }
    }

    /**
     * Creates a deep copy of the current domains.
     * Necessary for backtracking.
     */
    private Map<Integer, Set<Integer>> deepCopyDomains() {
        Map<Integer, Set<Integer>> newDomains = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : domains.entrySet()) {
            newDomains.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return newDomains;
    }
    
    /**
     * Revise function for AC-3.
     * Returns true if the domain of vi was changed.
     */
    private boolean revise(int vi, int vj) {
        boolean revised = false;
        Set<Integer> domainVi = domains.get(vi);
        Set<Integer> domainVj = domains.get(vj);
        
        // Using an iterator to safely remove elements while iterating
        Iterator<Integer> iterator = domainVi.iterator();
        while(iterator.hasNext()){
            Integer colorI = iterator.next();
            // If no color in domain of vj allows (colorI, colorJ) to satisfy the constraint
            if (domainVj.stream().noneMatch(colorJ -> !colorI.equals(colorJ))) {
                iterator.remove();
                revised = true;
            }
        }
        return revised;
    }

    /**
     * AC-3 Algorithm to enforce arc consistency.
     */
    private boolean ac3(Queue<int[]> queue) {
        while (!queue.isEmpty()) {
            int[] arc = queue.poll();
            int vi = arc[0];
            int vj = arc[1];

            if (revise(vi, vj)) {
                if (domains.get(vi).isEmpty()) {
                    return false; // Failure, domain wiped out
                }
                // Add all arcs (vk, vi) where vk is a neighbor of vi
                for (int vk : adj.getOrDefault(vi, Collections.emptyList())) {
                    if (vk != vj) {
                        queue.add(new int[]{vk, vi});
                    }
                }
            }
        }
        return true;
    }

    /**
     * Heuristic: Select the unassigned variable with the Minimum Remaining Values (MRV).
     */
    private int selectUnassignedVariable(Map<Integer, Integer> assignment) {
        int bestVar = -1;
        int minDomainSize = Integer.MAX_VALUE;

        for (int var : variables) {
            if (!assignment.containsKey(var)) {
                int domainSize = domains.get(var).size();
                if (domainSize < minDomainSize) {
                    minDomainSize = domainSize;
                    bestVar = var;
                }
            }
        }
        return bestVar;
    }

    /**
     * Heuristic: Order domain values by the Least Constraining Value (LCV).
     */
    private List<Integer> orderDomainValues(int var, Map<Integer, Integer> assignment) {
        if (!domains.containsKey(var)) return Collections.emptyList();

        List<Integer> currentDomain = new ArrayList<>(domains.get(var));
        
        currentDomain.sort(Comparator.comparingInt(color -> {
            int conflicts = 0;
            for (int neighbor : adj.getOrDefault(var, Collections.emptyList())) {
                if (!assignment.containsKey(neighbor) && domains.get(neighbor).contains(color)) {
                    conflicts++;
                }
            }
            return conflicts;
        }));
        
        return currentDomain;
    }

    /**
     * The core backtracking algorithm with heuristics.
     */
    private Map<Integer, Integer> backtrack(Map<Integer, Integer> assignment) {
        if (assignment.size() == variables.size()) {
            return assignment; // Success
        }

        int var = selectUnassignedVariable(assignment);
        List<Integer> orderedValues = orderDomainValues(var, assignment);

        for (Integer value : orderedValues) {
            Map<Integer, Integer> newAssignment = new HashMap<>(assignment);
            newAssignment.put(var, value);

            // Store old domains before making changes
            Map<Integer, Set<Integer>> oldDomains = deepCopyDomains();

            // Forward checking: Remove assigned value from neighbors' domains
            domains.get(var).clear();
            domains.get(var).add(value);
            
            // Maintain Arc Consistency (MAC)
            Queue<int[]> queue = new LinkedList<>();
            for (int neighbor : adj.getOrDefault(var, Collections.emptyList())) {
                if (!newAssignment.containsKey(neighbor)) {
                    queue.add(new int[]{neighbor, var});
                }
            }

            if (ac3(queue)) {
                Map<Integer, Integer> result = backtrack(newAssignment);
                if (result != null) {
                    return result;
                }
            }
            
            // Backtrack: Restore domains
            this.domains = oldDomains;
        }

        return null; // Failure
    }

    public void solve() {
        // Initial AC-3 consistency check
        Queue<int[]> initialQueue = new LinkedList<>();
        for (int u : variables) {
            for (int v : adj.getOrDefault(u, Collections.emptyList())) {
                initialQueue.add(new int[]{u, v});
            }
        }
        if (!ac3(initialQueue)) {
            System.out.println("failure");
            return;
        }

        Map<Integer, Integer> result = backtrack(new HashMap<>());

        if (result == null) {
            System.out.println("failure");
        } else {
            String solution = result.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
            System.out.println("SOLUTION: " + solution);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java CSPGraphColoring <filename>");
            return;
        }

        try {
            CSPGraphColoring csp = new CSPGraphColoring(args[0]);
            csp.solve();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
