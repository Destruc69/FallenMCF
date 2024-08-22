package paul.fallen.pathfinding;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class Pathfinder {
    private final CustomBlockPos start;
    private final CustomBlockPos end;
    private final ArrayList<CustomBlockPos> path;

    public static final int TRAVERSE_COST = 1;
    public static final int BREAK_COST = 5;

    public Pathfinder(BlockPos start, BlockPos end) {
        this.start = new CustomBlockPos(start, 0);
        this.end = new CustomBlockPos(end, 0);
        this.path = new ArrayList<>();
    }

    public void think() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.world == null) {
            throw new IllegalStateException("Minecraft world is not initialized.");
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<CustomBlockPos, CustomBlockPos> cameFrom = new HashMap<>();
        Map<CustomBlockPos, Integer> gScore = new HashMap<>();
        Map<CustomBlockPos, Integer> fScore = new HashMap<>();
        Set<CustomBlockPos> closedSet = new HashSet<>();

        openSet.add(new Node(start, heuristic(start.getBlockPos(), end.getBlockPos())));
        gScore.put(start, 0);
        fScore.put(start, heuristic(start.getBlockPos(), end.getBlockPos()));

        while (!openSet.isEmpty()) {
            CustomBlockPos current = openSet.poll().pos;

            if (current.equals(end)) {
                reconstructPath(cameFrom, current);
                return;
            }

            closedSet.add(current);

            for (Neighbor neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor.pos)) continue;

                int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + neighbor.actionCost;

                if (!gScore.containsKey(neighbor.pos) || tentativeGScore < gScore.get(neighbor.pos)) {
                    cameFrom.put(neighbor.pos, current);
                    gScore.put(neighbor.pos, tentativeGScore);
                    fScore.put(neighbor.pos, tentativeGScore + heuristic(neighbor.pos.getBlockPos(), end.getBlockPos()));

                    if (!openSet.contains(new Node(neighbor.pos, fScore.get(neighbor.pos)))) {
                        openSet.add(new Node(neighbor.pos, fScore.get(neighbor.pos)));
                    }
                }
            }
        }

        path.clear();
    }

    private void reconstructPath(Map<CustomBlockPos, CustomBlockPos> cameFrom, CustomBlockPos current) {
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

    private List<Neighbor> getNeighbors(CustomBlockPos pos) {
        List<Neighbor> neighbors = new ArrayList<>();
        int[][] directions = {
                {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
        };
        for (int[] direction : directions) {
            BlockPos neighborPos = pos.getBlockPos().add(direction[0], direction[1], direction[2]);
            int actionCost = calculateActionCost(pos.getBlockPos(), neighborPos);
            if (actionCost != Integer.MAX_VALUE) {
                neighbors.add(new Neighbor(new CustomBlockPos(neighborPos, actionCost), actionCost));
            }
        }
        return neighbors;
    }

    private int calculateActionCost(BlockPos from, BlockPos to) {
        if (canTraverse(from, to)) {
            return TRAVERSE_COST;
        } else if (canBreak(from, to)) {
            return BREAK_COST;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private boolean canTraverse(BlockPos from, BlockPos to) {
        BlockState a = Minecraft.getInstance().world.getBlockState(from.down());
        BlockState b = Minecraft.getInstance().world.getBlockState(from);
        BlockState c = Minecraft.getInstance().world.getBlockState(from.up());

        BlockState d = Minecraft.getInstance().world.getBlockState(to.down());
        BlockState e = Minecraft.getInstance().world.getBlockState(to);
        BlockState f = Minecraft.getInstance().world.getBlockState(to.up());

        return a.isSolid() && b.isAir() && c.isAir() && d.isSolid() && e.isAir() && f.isAir();
    }

    private boolean canBreak(BlockPos from, BlockPos to) {
        BlockState d = Minecraft.getInstance().world.getBlockState(to.down());
        BlockState e = Minecraft.getInstance().world.getBlockState(to);
        BlockState f = Minecraft.getInstance().world.getBlockState(to.up());

        return d.isSolid() && (e.isSolid() || f.isSolid());
    }

    public ArrayList<CustomBlockPos> getPath() {
        return path;
    }

    private static class Node implements Comparable<Node> {
        CustomBlockPos pos;
        int priority;

        Node(CustomBlockPos pos, int priority) {
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

    private static class Neighbor {
        CustomBlockPos pos;
        int actionCost;

        Neighbor(CustomBlockPos pos, int actionCost) {
            this.pos = pos;
            this.actionCost = actionCost;
        }
    }

    public class CustomBlockPos {
        private final BlockPos blockPos;
        private final int actionCost;

        public CustomBlockPos(BlockPos blockPos, int actionCost) {
            this.blockPos = blockPos;
            this.actionCost = actionCost;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public int getActionCost() {
            return actionCost;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CustomBlockPos that = (CustomBlockPos) obj;
            return blockPos.equals(that.blockPos);
        }

        @Override
        public int hashCode() {
            return blockPos.hashCode();
        }
    }
}