package paul.fallen.pathfinding;

import net.minecraft.util.math.BlockPos;

import java.util.*;

public class Pathfinder {
    private final BlockPos start;
    private final BlockPos end;
    private final ArrayList<BlockPos> path;

    public Pathfinder(BlockPos start, BlockPos end) {
        this.start = start;
        this.end = end;
        this.path = new ArrayList<>();
    }

    public void think(int depth) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
        Map<BlockPos, Integer> gScore = new HashMap<>();
        Map<BlockPos, Integer> fScore = new HashMap<>();
        Set<BlockPos> closedSet = new HashSet<>();

        openSet.add(new Node(start, heuristic(start, end)));
        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));

        while (!openSet.isEmpty()) {
            BlockPos current = openSet.poll().pos;

            if (current.equals(end)) {
                reconstructPath(cameFrom, current);
                return;
            }

            closedSet.add(current);

            for (BlockPos neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor)) continue;

                int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;

                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, end));

                    if (!openSet.contains(new Node(neighbor, fScore.get(neighbor)))) {
                        openSet.add(new Node(neighbor, fScore.get(neighbor)));
                    }
                }
            }
        }

        path.clear();
    }

    private void reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos current) {
        path.clear();
        while (cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(start);
        Collections.reverse(path);
    }

    private int heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    private ArrayList<BlockPos> getNeighbors(BlockPos pos) {
        ArrayList<BlockPos> neighbors = new ArrayList<>();
        neighbors.add(pos.add(1, 0, 0));
        neighbors.add(pos.add(-1, 0, 0));
        neighbors.add(pos.add(0, 1, 0));
        neighbors.add(pos.add(0, -1, 0));
        neighbors.add(pos.add(0, 0, 1));
        neighbors.add(pos.add(0, 0, -1));
        return neighbors;
    }

    public ArrayList<BlockPos> getPath() {
        return path;
    }

    private static class Node implements Comparable<Node> {
        BlockPos pos;
        int priority;

        Node(BlockPos pos, int priority) {
            this.pos = pos;
            this.priority = priority;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.priority, other.priority);
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
            return pos.hashCode();
        }
    }
}