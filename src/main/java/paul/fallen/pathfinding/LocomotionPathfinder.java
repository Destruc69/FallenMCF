package paul.fallen.pathfinding;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import paul.fallen.utils.render.RenderUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class LocomotionPathfinder {
    private final BlockPos startBlockPos;
    private final BlockPos endBlockPos;
    private LinkedList<BlockPos> path = new LinkedList<>();
    private final PriorityQueue<Hub> hubsToWork = new PriorityQueue<>(new CompareHub());

    private static final BlockPos[] FLAT_CARDINAL_DIRECTIONS = {
            new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
            new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 1, -1),
            new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, -1, -1),
            new BlockPos(1, 0, 1), new BlockPos(-1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1),
            new BlockPos(1, 1, 1), new BlockPos(-1, 1, -1), new BlockPos(1, 1, -1), new BlockPos(-1, 1, 1),
            new BlockPos(1, -1, 1), new BlockPos(-1, -1, -1), new BlockPos(1, -1, -1), new BlockPos(-1, -1, 1)
    };

    private static final Minecraft MC = Minecraft.getInstance();

    public LocomotionPathfinder(BlockPos startBlockPos, BlockPos endBlockPos) {
        this.startBlockPos = startBlockPos;
        this.endBlockPos = endBlockPos;
    }

    public LinkedList<BlockPos> getPath() {
        return path;
    }

    public void compute() {
        compute(100);
    }

    public void compute(int loops) {
        path.clear();
        hubsToWork.clear();
        hubsToWork.add(new Hub(startBlockPos, new LinkedList<>(Collections.singletonList(startBlockPos)), startBlockPos.distanceSq(endBlockPos), 0, 0));

        for (int i = 0; i < loops && !hubsToWork.isEmpty(); i++) {
            Hub hub = hubsToWork.poll();

            for (BlockPos direction : FLAT_CARDINAL_DIRECTIONS) {
                BlockPos loc = hub.getLoc().add(direction);
                if (checkPositionValidity(loc, false) && addHub(hub, loc, 0)) return;
            }

            BlockPos up = hub.getLoc().up();
            if (checkPositionValidity(up, false) && addHub(hub, up, 0)) return;

            BlockPos down = hub.getLoc().down();
            if (checkPositionValidity(down, false) && addHub(hub, down, 0)) return;
        }

        if (!hubsToWork.isEmpty()) {
            path = new LinkedList<>(hubsToWork.peek().getPath());
        }
    }

    public static boolean checkPositionValidity(BlockPos loc, boolean checkGround) {
        return !isBlockSolid(loc) && !isBlockSolid(loc.up()) &&
                (isBlockSolid(loc.down()) || !checkGround) &&
                isSafeToWalkOn(loc.down());
    }

    private static boolean isBlockSolid(BlockPos block) {
        assert MC.world != null;
        BlockState state = MC.world.getBlockState(block);
        Block blockEntity = state.getBlock();
        return blockEntity instanceof SlabBlock || blockEntity instanceof StairsBlock ||
                blockEntity instanceof CactusBlock || blockEntity instanceof ChestBlock ||
                blockEntity instanceof EnderChestBlock || blockEntity instanceof SkullBlock ||
                blockEntity instanceof PaneBlock || blockEntity instanceof FenceBlock ||
                blockEntity instanceof WallBlock || blockEntity instanceof GlassBlock ||
                blockEntity instanceof PistonBlock || blockEntity instanceof StainedGlassBlock ||
                state.isSolid();
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        assert MC.world != null;
        Block blockEntity = MC.world.getBlockState(block).getBlock();
        return !(blockEntity instanceof FenceBlock || blockEntity instanceof WallBlock);
    }

    public boolean addHub(Hub parent, BlockPos loc, double cost) {
        Hub existingHub = hubsToWork.stream()
                .filter(hub -> hub.getLoc().equals(loc))
                .findFirst()
                .orElse(null);
        double totalCost = cost + (parent != null ? parent.getTotalCost() : 0);

        if (existingHub == null) {
            double minDistanceSquared = 9;
            if (loc.equals(endBlockPos) || loc.distanceSq(endBlockPos) <= minDistanceSquared) {
                path.clear();
                assert parent != null;
                path.addAll(parent.getPath());
                path.add(loc);
                return true;
            } else {
                assert parent != null;
                LinkedList<BlockPos> newPath = new LinkedList<>(parent.getPath());
                newPath.add(loc);
                hubsToWork.add(new Hub(loc, newPath, loc.distanceSq(endBlockPos), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            assert parent != null;
            LinkedList<BlockPos> newPath = new LinkedList<>(parent.getPath());
            newPath.add(loc);
            existingHub.update(newPath, loc.distanceSq(endBlockPos), cost, totalCost);
        }
        return false;
    }

    private static class Hub {
        private final BlockPos loc;
        private LinkedList<BlockPos> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(BlockPos loc, LinkedList<BlockPos> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public BlockPos getLoc() { return loc; }

        public LinkedList<BlockPos> getPath() { return path; }
        public double getSquareDistanceToFromTarget() { return squareDistanceToFromTarget; }
        public double getCost() { return cost; }
        public double getTotalCost() { return totalCost; }

        public void update(LinkedList<BlockPos> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }
    }

    private static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return Double.compare(o1.getSquareDistanceToFromTarget() + o1.getTotalCost(),
                    o2.getSquareDistanceToFromTarget() + o2.getTotalCost());
        }
    }

    public void renderPath(RenderWorldLastEvent event) {
        for (int i = 0; i < path.size() - 1; i++) {
            RenderUtils.drawLine(path.get(i), path.get(i + 1), 0, 1, 0, event);
        }
    }

    public void dynamicRefresh() {
        if (isPathOutOfRange() || isPathTooClose()) {
            compute();
        }

        boolean onPath = path.stream().anyMatch(blockPos ->
                {
                    assert MC.player != null;
                    return MC.player.getPositionVec().subtract(new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5)).lengthSquared() < 16.0;
                }
        );

        if (!onPath) compute();
    }

    private boolean isPathOutOfRange() {
        if (path.isEmpty()) return false;
        BlockPos startPath = path.getFirst();
        double maxDistance = MC.gameSettings.renderDistanceChunks * 16 - 1;
        assert MC.player != null;
        return MC.player.getDistanceSq(startPath.getX(), startPath.getY(), startPath.getZ()) > maxDistance;
    }

    private boolean isPathTooClose() {
        if (path.isEmpty()) return false;
        BlockPos endPath = path.getLast();
        assert MC.player != null;
        return MC.player.getDistanceSq(endPath.getX(), endPath.getY(), endPath.getZ()) <= 1;
    }

    public void move() {
        BlockPos nextPos = getTargetPositionInPathArray(path);
        assert MC.player != null;
        Vector3d target = new Vector3d(nextPos.getX() + 0.5, MC.player.getPosY(), nextPos.getZ() + 0.5);
        Vector3d playerPos = MC.player.getPositionVec();
        Vector3d motion = target.subtract(playerPos).normalize().scale(MC.player.isSprinting() ? 0.26 : 0.2);

        MC.player.setMotion(motion.x, MC.player.getMotion().y, motion.z);

        if (nextPos.getY() > MC.player.getPosY() && MC.player.isOnGround()) {
            MC.player.jump();
        }

        if (MC.player.isInWater()) {
            MC.player.setMotion(motion.x, 0.01, motion.z);
        }
    }

    private BlockPos getTargetPositionInPathArray(LinkedList<BlockPos> path) {
        return path.stream()
                .min(Comparator.comparingDouble(blockPos -> {
                    assert MC.player != null;
                    return MC.player.getDistanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                }))
                .map(closestBlock -> path.indexOf(closestBlock) + 1 < path.size() ? path.get(path.indexOf(closestBlock) + 1) : closestBlock)
                .orElse(startBlockPos);
    }
}