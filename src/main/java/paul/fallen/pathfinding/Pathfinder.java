package paul.fallen.pathfinding;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;

public class Pathfinder {
    private PriorityQueue<Node> openSet;
    private Set<Node> closedSet;
    private Map<Node, Node> cameFrom;
    private Map<Node, Double> gScore;
    private Map<Node, Double> fScore;

    public Pathfinder() {
        this.openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> fScore.getOrDefault(node, Double.POSITIVE_INFINITY)));
        this.closedSet = new HashSet<>();
        this.cameFrom = new HashMap<>();
        this.gScore = new HashMap<>();
        this.fScore = new HashMap<>();
    }

    public ArrayList<Action> findPath(Node start, Node goal) {
        openSet.clear();
        closedSet.clear();
        cameFrom.clear();
        gScore.clear();
        fScore.clear();

        openSet.add(start);
        gScore.put(start, 0.0);
        fScore.put(start, heuristicCostEstimate(start, goal));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(goal)) {
                // Reconstruct the path and execute actions
                return reconstructAndExecutePath(cameFrom, current);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor)) {
                    continue; // Ignore the neighbor which is already evaluated
                }

                double tentativeGScore = gScore.getOrDefault(current, Double.POSITIVE_INFINITY) + distanceBetween(current, neighbor);

                if (!openSet.contains(neighbor) || tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristicCostEstimate(neighbor, goal));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private ArrayList<Action> reconstructAndExecutePath(Map<Node, Node> cameFrom, Node current) {
        ArrayList<Action> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            Node previous = cameFrom.get(current);
            Action action = determineAction(previous, current);
            if (action != null) {
                path.add(action);
            }
            current = previous;
        }
        Collections.reverse(path);

        // Execute the actions in sequence
        for (Action action : path) {
            action.execute();
        }

        return path;
    }

    private Action determineAction(Node from, Node to) {
        // Determine the best action based on cost
        double bestCost = Double.POSITIVE_INFINITY;
        Action bestAction = null;

        // Check all possible actions and choose the one with the lowest cost
        for (Action action : getPossibleActions(from, to)) {
            double actionCost = calculateActionCost(action, from, to);
            if (actionCost < bestCost) {
                bestCost = actionCost;
                bestAction = action;
            }
        }

        return bestAction;
    }

    private List<Action> getPossibleActions(Node from, Node to) {
        List<Action> actions = new ArrayList<>();
        if (isAdjacent(from, to)) {
            actions.add(new MoveAction(to.pos));
        }
        if (isForBreak(from, to)) {
            actions.add(new BreakAction(to.pos));
        }
        if (isForPlace(from, to)) {
            actions.add(new PlaceAction(to.pos));
        }
        return actions;
    }

    private double calculateActionCost(Action action, Node from, Node to) {
        // Calculate the cost of executing the given action from 'from' to 'to'

        // Simulate the action to get the resulting path
        ArrayList<Action> path = simulateAction(action, from, to);

        if (path.isEmpty()) {
            // If the resulting path is empty, return a high cost to avoid selecting this action
            return Double.POSITIVE_INFINITY;
        }

        // Calculate the cost of the resulting path
        double pathCost = calculatePathCost(path);

        return pathCost;
    }

    private ArrayList<Action> simulateAction(Action action, Node from, Node to) {
        // Simulate the given action and return the resulting path
        Pathfinder pathfinder = new Pathfinder();
        return pathfinder.findPath(from, to);
    }

    private double calculatePathCost(ArrayList<Action> path) {
        // Calculate the total cost of executing the given path of actions
        double totalCost = 0.0;
        for (Action action : path) {
            totalCost += getActionCost(action);
        }
        return totalCost;
    }

    private double getActionCost(Action action) {
        if (action instanceof MoveAction) {
            return 3;
        } else if (action instanceof PlaceAction) {
            return 2;
        } else if (action instanceof BreakAction) {
            return 1;
        }
        return 0;
    }

    private boolean isForBreak(Node from, Node to) {
        return Minecraft.getInstance().world.getBlockState(new net.minecraft.util.math.BlockPos(to.pos.x, to.pos.y, to.pos.z)).isSolid();
    }


    private boolean isForPlace(Node from, Node to) {
        return Minecraft.getInstance().world.getBlockState(new net.minecraft.util.math.BlockPos(to.pos.x, to.pos.y, to.pos.z)).isAir();
    }

    private double heuristicCostEstimate(Node from, Node to) {
        // Placeholder heuristic (Euclidean distance for example)
        return distanceBetween(from, to);
    }

    private double distanceBetween(Node from, Node to) {
        // Placeholder distance calculation
        // Assuming a 3D grid, calculate Euclidean distance between positions
        int dx = Math.abs(from.pos.getX() - to.pos.getX());
        int dy = Math.abs(from.pos.getY() - to.pos.getY());
        int dz = Math.abs(from.pos.getZ() - to.pos.getZ());
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private ArrayList<Node> getNeighbors(Node node) {
        ArrayList<Node> neighbors = new ArrayList<>();

        int x = node.pos.getX();
        int y = node.pos.getY();
        int z = node.pos.getZ();

        // Generate neighboring positions in a 3D grid (e.g., 6-connected or 26-connected)

        // 26-connected neighbors (including diagonals)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue; // Skip the current node itself
                    }
                    int nx = x + dx;
                    int ny = y + dy;
                    int nz = z + dz;
                    BlockPos neighborPos = new BlockPos(nx, ny, nz);
                    Node neighborNode = new Node(neighborPos, null, 0, 0); // Parent and costs are placeholders
                    neighbors.add(neighborNode);
                }
            }
        }

        // Optionally, filter out nodes based on game-specific constraints (e.g., within bounds, obstacles)

        return neighbors;
    }

    private boolean isAdjacent(Node from, Node to) {
        int dx = Math.abs(from.pos.getX() - to.pos.getX());
        int dy = Math.abs(from.pos.getY() - to.pos.getY());
        int dz = Math.abs(from.pos.getZ() - to.pos.getZ());
        return (dx <= 1 && dy <= 1 && dz <= 1 && (dx + dy + dz) > 0);
    }

    // Define your specific action classes below
    public static abstract class Action {
        public abstract void execute();
    }

    public static class MoveAction extends Action {
        private Pathfinder.BlockPos position;

        public MoveAction(Pathfinder.BlockPos position) {
            this.position = position;
        }

        @Override
        public void execute() {
            // Implement moving logic
            System.out.println("Moving to: " + position);
        }
    }

    public static class BreakAction extends Action {
        private Pathfinder.BlockPos position;

        public BreakAction(Pathfinder.BlockPos position) {
            this.position = position;
        }

        @Override
        public void execute() {
            // Implement moving logic
            System.out.println("Breaking: " + position);
        }
    }

    public static class PlaceAction extends Action {
        private Pathfinder.BlockPos position;

        public PlaceAction(Pathfinder.BlockPos position) {
            this.position = position;
        }

        @Override
        public void execute() {
            // Implement moving logic
            System.out.println("Placing: " + position);
        }
    }

    private static class Node {
        Pathfinder.BlockPos pos;
        Node parent;
        int gCost; // Cost from start to this node
        int hCost; // Heuristic cost to target
        int fCost; // Total cost (gCost + hCost)

        Node(Pathfinder.BlockPos pos, Node parent, int gCost, int hCost) {
            this.pos = pos;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return pos.equals(node.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos);
        }
    }

    // Assuming BlockPos is a placeholder for a position in your game
    private static class BlockPos {
        private final int x, y, z;

        public BlockPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BlockPos blockPos = (BlockPos) obj;
            return x == blockPos.x && y == blockPos.y && z == blockPos.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }
}