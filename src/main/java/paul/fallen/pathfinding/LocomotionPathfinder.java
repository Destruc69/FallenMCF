package paul.fallen.pathfinding;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class LocomotionPathfinder {
    private final BlockPos startPos;
    private final BlockPos endPos;
    private LinkedList<BlockPos> path = new LinkedList<>();
    private final Set<Hub> hubs = new HashSet<>();
    private final PriorityQueue<Hub> hubsToWork = new PriorityQueue<>(new CompareHub());
    private static final Minecraft mc = Minecraft.getInstance();

    private static final BlockPos[] flatCardinalDirections = {
            new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
            new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 1, -1),
            new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, -1, -1),
            new BlockPos(1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1),
            new BlockPos(1, 1, 1), new BlockPos(-1, 1, -1), new BlockPos(1, 1, -1), new BlockPos(-1, 1, 1),
            new BlockPos(1, -1, 1), new BlockPos(-1, -1, -1), new BlockPos(1, -1, -1), new BlockPos(-1, -1, 1)
    };

    public LocomotionPathfinder(BlockPos startPos, BlockPos endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public LinkedList<BlockPos> getPath() {
        return path;
    }

    public void compute() {
        compute(1000);
    }

    public void compute(int loops) {
        path.clear();
        hubsToWork.clear();
        Set<BlockPos> visited = new HashSet<>();

        LinkedList<BlockPos> initPath = new LinkedList<>();
        initPath.add(startPos);
        hubsToWork.add(new Hub(startPos, null, initPath, startPos.manhattanDistance(endPos), 0, 0));

        search:
        for (int i = 0; i < loops; i++) {
            if (hubsToWork.isEmpty()) break;

            Hub hub = hubsToWork.poll();
            if (hub == null) break;

            hubs.add(hub);

            for (BlockPos direction : flatCardinalDirections) {
                BlockPos loc = hub.getLoc().add(direction);
                if (checkPositionValidity(loc) && !visited.contains(loc)) {
                    visited.add(loc);
                    if (addHub(hub, loc)) {
                        break search;
                    }
                }
            }

            BlockPos loc1 = hub.getLoc().up();
            if (checkPositionValidity(loc1) && !visited.contains(loc1)) {
                visited.add(loc1);
                if (addHub(hub, loc1)) {
                    break;
                }
            }

            BlockPos loc2 = hub.getLoc().down();
            if (checkPositionValidity(loc2) && !visited.contains(loc2)) {
                visited.add(loc2);
                if (addHub(hub, loc2)) {
                    break;
                }
            }
        }

        if (!hubs.isEmpty()) {
            List<Hub> hubsList = new ArrayList<>(hubs);
            hubsList.sort(new CompareHub());
            path = hubsList.iterator().next().getPath();
        }
    }

    public static boolean checkPositionValidity(BlockPos pos) {
        return checkPositionValidity(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean checkPositionValidity(int x, int y, int z) {
        BlockPos block = new BlockPos(x, y, z);
        assert mc.world != null;
        if (!mc.world.getBlockState(block).getMaterial().isReplaceable()) {
            return false;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos neighbor = block.add(dx, dy, dz);
                    if (!mc.world.getBlockState(neighbor).getMaterial().isReplaceable()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean addHub(Hub parent, BlockPos loc) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = (double) 0 + parent.getTotalCost();
        if (existingHub == null) {
            if (loc.equals(endPos)) {
                path.clear();
                path = parent.getPath();
                path.add(loc);
                return true;
            } else {
                LinkedList<BlockPos> newPath = new LinkedList<>(parent.getPath());
                newPath.add(loc);
                hubsToWork.add(new Hub(loc, parent, newPath, loc.manhattanDistance(endPos), 0, totalCost));
            }
        } else if (existingHub.getCost() > (double) 0) {
            LinkedList<BlockPos> newPath = new LinkedList<>(parent.getPath());
            newPath.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(newPath);
            existingHub.setCost(0);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    public Hub isHubExisting(BlockPos loc) {
        for (Hub hub : hubs) {
            if (hub.getLoc().equals(loc)) {
                return hub;
            }
        }
        for (Hub hub : hubsToWork) {
            if (hub.getLoc().equals(loc)) {
                return hub;
            }
        }
        return null;
    }

    public BlockPos getNextBlockToMove() {
        if (path.isEmpty()) {
            return null;
        }

        BlockPos currentPlayerPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());

        // Find the next block that is closer to the goal
        for (BlockPos nextBlock : path) {
            if (isCloserToGoal(currentPlayerPos, nextBlock)) {
                return nextBlock;
            }
        }

        // If no valid forward block is found, return null
        return null;
    }

    // Utility method to check if the next block is closer to the goal
    private boolean isCloserToGoal(BlockPos currentPos, BlockPos nextPos) {
        // Calculate Manhattan distance to the goal (endPos) from the current position
        double currentDistance = currentPos.manhattanDistance(endPos);
        double nextDistance = nextPos.manhattanDistance(endPos);

        // If next position is closer to the goal, return true
        return nextDistance < currentDistance;
    }


    private static class Hub {
        private BlockPos loc;
        private Hub parent;
        private LinkedList<BlockPos> path;
        private final double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(BlockPos loc, Hub parent, LinkedList<BlockPos> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public BlockPos getLoc() {
            return loc;
        }

        public Hub getParent() {
            return parent;
        }

        public LinkedList<BlockPos> getPath() {
            return path;
        }

        public double getSquareDistanceToFromTarget() {
            return squareDistanceToFromTarget;
        }

        public double getCost() {
            return cost;
        }

        public void setLoc(BlockPos loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(LinkedList<BlockPos> path) {
            this.path = path;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }

    public static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return Double.compare(o1.getSquareDistanceToFromTarget() + o1.getTotalCost(),
                    o2.getSquareDistanceToFromTarget() + o2.getTotalCost());
        }
    }
}
