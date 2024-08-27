package paul.fallen.pathfinding;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.*;

public class Pathfinder {

    private static final World world = Minecraft.getInstance().world;

    public Pathfinder() {
    }

    private boolean isWalkable(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() != Blocks.AIR && !state.getMaterial().isLiquid();
    }

    private List<BlockPos> getPositionsBetween(BlockPos from, BlockPos to) {
        List<BlockPos> positions = new ArrayList<>();
        int minX = Math.min(from.getX(), to.getX());
        int maxX = Math.max(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int maxY = Math.max(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxZ = Math.max(from.getZ(), to.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }

    private List<BlockPos> getNeighborPositions(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        neighbors.add(pos.add(x, y, z));
                    }
                }
            }
        }
        return neighbors;
    }

    private List<BlockPos> aStarPathfinding(BlockPos start, BlockPos goal, Set<BlockPos> walkablePositions) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<BlockPos> closedSet = new HashSet<>();
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();

        Node startNode = new Node(start, 0, heuristic(start, goal));
        openSet.add(startNode);

        Map<BlockPos, Integer> gScores = new HashMap<>();
        gScores.put(start, 0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.pos.equals(goal)) {
                return reconstructPath(cameFrom, current.pos);
            }

            closedSet.add(current.pos);

            for (BlockPos neighbor : getNeighborPositions(current.pos)) {
                if (!walkablePositions.contains(neighbor) || closedSet.contains(neighbor)) {
                    continue;
                }

                int tentativeGScore = gScores.getOrDefault(current.pos, Integer.MAX_VALUE) + 1;

                if (tentativeGScore < gScores.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current.pos);
                    gScores.put(neighbor, tentativeGScore);
                    Node neighborNode = new Node(neighbor, tentativeGScore, heuristic(neighbor, goal));

                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }

        return Collections.emptyList();  // No path found
    }

    private int heuristic(BlockPos a, BlockPos b) {
        return MathHelper.abs(a.getX() - b.getX()) + MathHelper.abs(a.getY() - b.getY()) + MathHelper.abs(a.getZ() - b.getZ());
    }

    private List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos current) {
        List<BlockPos> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    public List<BlockPos> findPath(BlockPos start, BlockPos goal) {
        List<BlockPos> allPositions = getPositionsBetween(start, goal);
        Set<BlockPos> walkablePositions = new HashSet<>();
        for (BlockPos pos : allPositions) {
            if (isWalkable(pos)) {
                walkablePositions.add(pos);
            }
        }
        return aStarPathfinding(start, goal, walkablePositions);
    }

    private static class Node {
        BlockPos pos;
        int gCost;
        int hCost;
        int fCost;

        Node(BlockPos pos, int gCost, int hCost) {
            this.pos = pos;
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
            return pos.hashCode();
        }
    }
}