

/**
 * Heuristic functions class
 * Contains different estimation functions for A* algorithm
 */
public class Heuristics {

    /**
     * Zero heuristic - for Uniform Cost Search
     * Always returns 0, which simulates UCS algorithm
     */
    public static double hZero(Types.Node from, Types.Node to) {
        return 0.0;
    }

    /**
     * Euclidean distance heuristic
     * Calculates straight-line distance between two points
     */
    public static double hEuclidean(Types.Node from, Types.Node to) {
        double deltaX = (double)(from.x - to.x);
        double deltaY = (double)(from.y - to.y);
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Manhattan distance heuristic
     * Calculates grid-based distance (only up/down, left/right movement)
     */
    public static double hManhattan(Types.Node from, Types.Node to) {
        return (double)(Math.abs(from.x - to.x) + Math.abs(from.y - to.y));
    }
}