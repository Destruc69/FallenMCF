package paul.fallen.pathfinding;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LocomotionPathfinder {
    private BlockPos startPos;
    private BlockPos endPos;
    private ArrayList<BlockPos> path = new ArrayList<>();
    private ArrayList<Hub> hubs = new ArrayList<>();
    private ArrayList<Hub> hubsToWork = new ArrayList<>();
    private double minDistanceSquared = 9;
    private boolean nearest = true;

    private static final Minecraft mc = Minecraft.getInstance();

    private static final BlockPos[] flatCardinalDirections = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),

            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1),

            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1),


            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(1, 0, -1),

            new BlockPos(1, 1, 1),
            new BlockPos(-1, 1, -1),
            new BlockPos(1, 1, -1),
            new BlockPos(-1, 1, 1),

            new BlockPos(1, -1, 1),
            new BlockPos(-1, -1, -1),
            new BlockPos(1, -1, -1),
            new BlockPos(-1, -1, 1)
    };

    public LocomotionPathfinder(BlockPos startPos, BlockPos endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public ArrayList<BlockPos> getPath() {
        return path;
    }

    public void compute() {
        compute(1000, 4);
    }

    public void compute(int loops, int depth) {
        path.clear();
        hubsToWork.clear();
        ArrayList<BlockPos> initPath = new ArrayList<>();
        initPath.add(startPos);
        hubsToWork.add(new Hub(startPos, null, initPath, startPos.manhattanDistance(endPos), 0, 0));
        search:
        for (int i = 0; i < loops; i++) {
            Collections.sort(hubsToWork, new CompareHub());
            int j = 0;
            if (hubsToWork.size() == 0) {
                break;
            }
            for (Hub hub : new ArrayList<>(hubsToWork)) {
                j++;
                if (j > depth) {
                    break;
                } else {
                    hubsToWork.remove(hub);
                    hubs.add(hub);

                    for (BlockPos direction : flatCardinalDirections) {
                        BlockPos loc = hub.getLoc().add(direction);
                        if (checkPositionValidity(loc)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    BlockPos loc1 = hub.getLoc().up();
                    if (checkPositionValidity(loc1)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    BlockPos loc2 = hub.getLoc().down();
                    if (checkPositionValidity(loc2)) {
                        if (addHub(hub, loc2, 0)) {
                            break search;
                        }
                    }
                }
            }
        }
        if (nearest) {
            Collections.sort(hubs, new CompareHub());
            path = hubs.get(0).getPath();
        }
    }

    public static boolean checkPositionValidity(BlockPos pos) {
        return checkPositionValidity(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean checkPositionValidity(int x, int y, int z) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);

        return !isBlockSolid(block1) &&
                !isBlockSolid(block2) &&
                isBlockSolid(block3) &&
                isSafeToWalkOn(block3);
    }

    private static boolean isBlockSolid(BlockPos block) {
        World world = Minecraft.getInstance().world;
        Block blockType = world.getBlockState(block).getBlock();
        return world.getBlockState(block).isSolid() ||
                blockType instanceof SlabBlock ||
                blockType instanceof StairsBlock ||
                blockType instanceof CactusBlock ||
                blockType instanceof ChestBlock ||
                blockType instanceof EnderChestBlock ||
                blockType instanceof SkullBlock ||
                blockType instanceof PaneBlock ||
                blockType instanceof FenceBlock ||
                blockType instanceof WallBlock ||
                blockType instanceof GlassBlock ||
                blockType instanceof PistonBlock ||
                blockType instanceof PistonHeadBlock ||
                blockType instanceof StainedGlassBlock ||
                blockType instanceof TrapDoorBlock;
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        World world = Minecraft.getInstance().world;
        Block blockType = world.getBlockState(block).getBlock();
        return !(blockType instanceof FenceBlock) && !(blockType instanceof WallBlock);
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

    public boolean addHub(Hub parent, BlockPos loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if (loc.equals(endPos) || (minDistanceSquared != 0 && loc.manhattanDistance(endPos) <= minDistanceSquared)) {
                path.clear();
                path = parent.getPath();
                path.add(loc);
                return true;
            } else {
                ArrayList<BlockPos> path = new ArrayList<>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.manhattanDistance(endPos), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<BlockPos> path = new ArrayList<>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.manhattanDistance(endPos));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    public void move() {
        BlockPos nextPos = getTargetPositionInPathArray(path);
        Vector3d target = new Vector3d(nextPos.getX() + 0.5, mc.player.getPosY(), nextPos.getZ() + 0.5);
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d motion = target.subtract(playerPos).normalize().scale(mc.player.isSprinting() ? 0.26 : 0.2);

        mc.player.setMotion(motion.x, mc.player.getMotion().y, motion.z);

        if (nextPos.getY() > mc.player.getPosY() && mc.player.isOnGround()) {
            mc.player.jump();
        }

        if (mc.player.isInWater()) {
            mc.player.setMotion(motion.x, 0.01, motion.z);
        }
    }

    public void renderPath(RenderWorldLastEvent event) {
        for (int i = 0; i < path.size() - 1; i++) {
            RenderUtils.drawLine(path.get(i), path.get(i + 1), 0, 1, 0, event);
        }
    }

    public void dynamicRefresh() {
        if (isPathOutOfRange() || isPathTooClose() || !isOnPath()) {
            compute();
        }
    }

    private boolean isOnPath() {
        Vector3d playerPos = mc.player.getPositionVec();
        return path.stream().anyMatch(blockPos ->
                playerPos.subtract(new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5)).lengthSquared() < 16.0
        );
    }

    private boolean isPathOutOfRange() {
        if (path.isEmpty()) return true;  // If there's no path, it's considered out of range

        BlockPos lastPos = path.get(path.size() - 1);  // Get the last position in the path
        double distance = mc.player.getDistanceSq(lastPos.getX(), lastPos.getY(), lastPos.getZ());

        // Define a max range (e.g., 100 blocks). You can adjust this threshold as needed.
        double maxRange = 10 * 10;  // Squared distance for efficiency
        return distance > maxRange;
    }

    private boolean isPathTooClose() {
        if (path.isEmpty()) return true;  // If there's no path, it's considered too close

        BlockPos lastPos = path.get(path.size() - 1);  // Get the last position in the path
        double distance = mc.player.getDistanceSq(lastPos.getX(), lastPos.getY(), lastPos.getZ());

        // Define a minimum range (e.g., 1 block). You can adjust this threshold as needed.
        double minRange = 1 * 1;  // Squared distance for efficiency
        return distance < minRange;
    }

    public BlockPos getTargetPositionInPathArray(ArrayList<BlockPos> path) {
        return path.stream()
                .min(Comparator.comparingDouble(blockPos -> {
                    assert mc.player != null;
                    return mc.player.getDistanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                }))
                .map(closestBlock -> path.indexOf(closestBlock) + 1 < path.size() ? path.get(path.indexOf(closestBlock) + 1) : closestBlock)
                .orElse(startPos);
    }

    private class Hub {
        private BlockPos loc;
        private Hub parent;
        private ArrayList<BlockPos> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(BlockPos loc, Hub parent, ArrayList<BlockPos> path, double squareDistanceToFromTarget, double cost, double totalCost) {
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

        public ArrayList<BlockPos> getPath() {
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

        public void setPath(ArrayList<BlockPos> path) {
            this.path = path;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
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

    public class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return Double.compare(
                    o1.getSquareDistanceToFromTarget() + o1.getTotalCost(),
                    o2.getSquareDistanceToFromTarget() + o2.getTotalCost()
            );
        }
    }
}