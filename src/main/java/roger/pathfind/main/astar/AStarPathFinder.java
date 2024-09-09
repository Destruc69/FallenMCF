package roger.pathfind.main.astar;

import net.minecraft.util.math.BlockPos;

import java.util.*;

public class AStarPathFinder {

    private static final int MAX_SEARCHES = 1000;

    public static List<AStarNode> compute(BlockPos start, BlockPos end, int depth) {
        PriorityQueue<AStarNode> openQueue = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getTotalCost));
        Set<AStarNode> closedSet = new HashSet<>();
        PriorityQueue<List<AStarNode>> allPaths = new PriorityQueue<>(Comparator.comparingDouble(AStarPathFinder::getPathCost));

        AStarNode endNode = new AStarNode(end);
        AStarNode startNode = new AStarNode(start, endNode);

        openQueue.add(startNode);

        for (int i = 0; i < depth; i++) {
            if (openQueue.isEmpty()) break;

            AStarNode currentNode = openQueue.poll();
            closedSet.add(currentNode);

            if (currentNode.equals(endNode)) {
                allPaths.add(getPath(currentNode));
                if (allPaths.size() > MAX_SEARCHES) {
                    allPaths.poll();
                }
                continue;
            }

            populateNeighbours(openQueue, closedSet, currentNode, startNode, endNode);
        }

        return getBestPath(allPaths);
    }

    private static void populateNeighbours(PriorityQueue<AStarNode> openQueue, Set<AStarNode> closedSet, AStarNode current, AStarNode startNode, AStarNode endNode) {
        List<AStarNode> neighbours = Arrays.asList(
                new AStarNode(-1, 0, 0, current, endNode),
                new AStarNode(0, 0, 1, current, endNode),
                new AStarNode(0, 0, -1, current, endNode),
                new AStarNode(1, 0, 0, current, endNode),
                new AStarNode(0, 1, 0, current, endNode),
                new AStarNode(0, -1, 0, current, endNode)
        );

        for (AStarNode neighbour : neighbours) {
            if (closedSet.contains(neighbour)) continue;

            if (neighbour.canBeTraversed()) {
                if (!openQueue.contains(neighbour) || neighbour.getTotalCost() < current.getTotalCost()) {
                    openQueue.add(neighbour);
                }
            }
        }
    }

    private static List<AStarNode> getPath(AStarNode currentNode) {
        List<AStarNode> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(0, currentNode);
            currentNode = currentNode.getParent();
        }
        return path;
    }

    private static double getPathCost(List<AStarNode> path) {
        if (path.isEmpty()) return Double.MAX_VALUE;
        return path.get(path.size() - 1).getTotalCost();
    }

    private static List<AStarNode> getBestPath(PriorityQueue<List<AStarNode>> allPaths) {
        return allPaths.isEmpty() ? new ArrayList<>() : allPaths.peek();
    }
}