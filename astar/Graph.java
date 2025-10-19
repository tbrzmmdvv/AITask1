import java.io.*;
import java.util.*;

/**
 * Graph file reading and parsing class
 * Reads graph data from input file and creates Graph object
 */
public class Graph {

    /**
     * Reads and parses graph data from file
     * @param filename Path to the graph file
     * @return Parsed Graph object
     * @throws IOException File reading error
     */
    public static Types.Graph parseGraph(String filename) throws IOException {
        Types.Graph graph = new Types.Graph();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String raw;
            while ((raw = br.readLine()) != null) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String low = line.toLowerCase();

                // Handle lines that explicitly start with labels like "Vertex:", "Edge:", "Source:", "Destination:"
                if (low.startsWith("vertex")) {
                    int colon = line.indexOf(':');
                    String content = colon >= 0 ? line.substring(colon + 1).trim() : line.substring(6).trim();
                    String[] parts = content.split(",");
                    if (parts.length >= 2) {
                        try {
                            int id = Integer.parseInt(parts[0].trim());
                            int cell = Integer.parseInt(parts[1].trim());
                            Types.Node n = new Types.Node(id, cell / 10, cell % 10);
                            graph.nodes.put(id, n);
                        } catch (NumberFormatException ignored) {}
                    }
                    continue;
                }

                if (low.startsWith("edge")) {
                    int colon = line.indexOf(':');
                    String content = colon >= 0 ? line.substring(colon + 1).trim() : line.substring(4).trim();
                    String[] parts = content.split(",");
                    if (parts.length >= 3) {
                        try {
                            int u = Integer.parseInt(parts[0].trim());
                            int v = Integer.parseInt(parts[1].trim());
                            double w = Double.parseDouble(parts[2].trim());
                            graph.adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Types.Edge(v, w));
                            graph.adj.computeIfAbsent(v, k -> new ArrayList<>()).add(new Types.Edge(u, w));
                        } catch (NumberFormatException ignored) {}
                    }
                    continue;
                }

                if (low.startsWith("source")) {
                    int colon = line.indexOf(':');
                    String content = colon >= 0 ? line.substring(colon + 1).trim() : line.substring(6).trim();
                    String[] tok = content.split(",");
                    // find first integer token
                    for (String t : tok) {
                        try {
                            graph.source = Integer.parseInt(t.trim().replaceAll("[^0-9\\-]", ""));
                            break;
                        } catch (NumberFormatException ignored) {}
                    }
                    continue;
                }

                if (low.startsWith("destination")) {
                    int colon = line.indexOf(':');
                    String content = colon >= 0 ? line.substring(colon + 1).trim() : line.substring(11).trim();
                    String[] tok = content.split(",");
                    for (String t : tok) {
                        try {
                            graph.destination = Integer.parseInt(t.trim().replaceAll("[^0-9\\-]", ""));
                            break;
                        } catch (NumberFormatException ignored) {}
                    }
                    continue;
                }

                // Also support short forms like "S,1" or "D,2"
                if (line.startsWith("S," ) || line.startsWith("s,")) {
                    String[] tok = line.split(",");
                    if (tok.length >= 2) {
                        try {
                            graph.source = Integer.parseInt(tok[1].trim());
                        } catch (NumberFormatException ignored) {}
                    }
                    continue;
                }
                if (line.startsWith("D," ) || line.startsWith("d,")) {
                    String[] tok = line.split(",");
                    if (tok.length >= 2) {
                        try {
                            graph.destination = Integer.parseInt(tok[1].trim());
                        } catch (NumberFormatException ignored) {}
                    }
                    continue;
                }

                // Fallback: support plain "id,cell" for vertex and "u,v,w" for edge
                long commas = line.chars().filter(ch -> ch == ',').count();
                String[] parts = line.split(",");
                if (commas == 1 && parts.length >= 2) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        int cell = Integer.parseInt(parts[1].trim());
                        Types.Node n = new Types.Node(id, cell / 10, cell % 10);
                        graph.nodes.put(id, n);
                    } catch (NumberFormatException ignored) {}
                } else if (commas >= 2 && parts.length >= 3) {
                    try {
                        int u = Integer.parseInt(parts[0].trim());
                        int v = Integer.parseInt(parts[1].trim());
                        double w = Double.parseDouble(parts[2].trim());
                        graph.adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Types.Edge(v, w));
                        graph.adj.computeIfAbsent(v, k -> new ArrayList<>()).add(new Types.Edge(u, w));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        return graph;
    }
}