package paul.fallen.pathfinding;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class LocomotionPathfinder {
    private final BlockPos startBlockPos;
    private final BlockPos endBlockPos;
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
    private final ArrayList<Hub> hubs = new ArrayList<>();
    private final ArrayList<Hub> hubsToWork = new ArrayList<>();
    private final double minDistanceSquared = 9;
    private final boolean nearest = true;
    private ArrayList<BlockPos> path = new ArrayList<>();

    public LocomotionPathfinder(BlockPos startBlockPos, BlockPos endBlockPos) {
        this.startBlockPos = startBlockPos;
        this.endBlockPos = endBlockPos;
    }

    public static boolean checkPositionValidity(BlockPos loc, boolean checkGround) {
        BlockPos block1 = loc;
        BlockPos block2 = loc.up();
        BlockPos block3 = loc.down();
        return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
    }

    private static boolean isBlockSolid(BlockPos block) {
        return Minecraft.getInstance().world.getBlockState(block).isSolid() ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.SlabBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.StairsBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.CactusBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.ChestBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.EnderChestBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.SkullBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.PaneBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.FenceBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.WallBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.GlassBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.PistonBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.StainedGlassBlock;
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        return !(Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.FenceBlock) &&
                !(Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof net.minecraft.block.WallBlock);
    }

    public ArrayList<BlockPos> getPath() {
        return path;
    }

    public void compute() {
        compute(100, 2);
    }

    public void compute(int loops, int depth) {
        path.clear();
        hubsToWork.clear();
        ArrayList<BlockPos> initPath = new ArrayList<>();
        initPath.add(startBlockPos);
        hubsToWork.add(new Hub(startBlockPos, null, initPath, startBlockPos.distanceSq(endBlockPos), 0, 0));
        search:
        for (int i = 0; i < loops; i++) {
            hubsToWork.sort(new CompareHub());
            int j = 0;
            if (hubsToWork.isEmpty()) {
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
                        if (checkPositionValidity(loc, false)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    BlockPos loc1 = hub.getLoc().up();
                    if (checkPositionValidity(loc1, false)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    BlockPos loc2 = hub.getLoc().down();
                    if (checkPositionValidity(loc2, false)) {
                        if (addHub(hub, loc2, 0)) {
                            break search;
                        }
                    }
                }
            }
        }
        if (nearest) {
            hubs.sort(new CompareHub());
            path = new ArrayList<>(hubs.get(0).getPath());
        }
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
            if (loc.equals(endBlockPos) || (minDistanceSquared != 0 && loc.distanceSq(endBlockPos) <= minDistanceSquared)) {
                path.clear();
                path.addAll(parent.getPath());
                path.add(loc);
                return true;
            } else {
                ArrayList<BlockPos> path = new ArrayList<>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.distanceSq(endBlockPos), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<BlockPos> path = new ArrayList<>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.distanceSq(endBlockPos));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    public void renderPath(RenderWorldLastEvent event) {
        for (int i = 0; i < getPath().size(); i++) {
            if (i + 1 <= getPath().size()) {
                RenderUtils.drawLine(getPath().get(i), getPath().get(i + 1), 0, 1, 0, event);
            }
        }
    }

    public void dynamicRefresh() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player.getDistanceSq(getPath().get(0).getX(), getPath().get(0).getY(), getPath().get(0).getZ()) > mc.gameSettings.renderDistanceChunks * 16 - 1 ||
                mc.player.getDistanceSq(getPath().get(getPath().size() - 1).getX(), getPath().get(getPath().size() - 1).getY(), getPath().get(getPath().size() - 1).getZ()) <= 1) {
            compute();
        }

        boolean onPath = false;
        int range = 2; // Check blocks 2 blocks away from the player in all directions

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos blockPos = new BlockPos(mc.player.getPosX() + x, mc.player.getPosY() + y, mc.player.getPosZ() + z);
                    if (getPath().contains(blockPos)) {
                        onPath = true;
                        break; // Break out of the loops once a block on the path is found
                    }
                }
                if (onPath) {
                    break; // Break out of the loops once a block on the path is found
                }
            }
            if (onPath) {
                break; // Break out of the loops once a block on the path is found
            }
        }

        if (!onPath) {
            compute();
        }
    }

    public void move() {
        BlockPos nextPos = getTargetPositionInPathArray(getPath());

        // Move towards the next position
        double speed = Minecraft.getInstance().player.isSprinting() ? 0.26 : 0.2; // Adjust speed as needed
        Vector3d target = new Vector3d(nextPos.getX() + 0.5, Minecraft.getInstance().player.getPosY(), nextPos.getZ() + 0.5);
        Vector3d playerPos = Minecraft.getInstance().player.getPositionVec();
        Vector3d motion = target.subtract(playerPos).normalize().scale(speed);

        // Apply movement
        Minecraft.getInstance().player.setMotion(motion.x, Minecraft.getInstance().player.getMotion().y, motion.z);

        // Jump if necessary
        if (nextPos.getY() > Minecraft.getInstance().player.getPosY() && Minecraft.getInstance().player.isOnGround()) {
            Minecraft.getInstance().player.jump();
        }

        // Adjust for water movement
        if (Minecraft.getInstance().player.isInWater()) {
            Minecraft.getInstance().player.setMotion(Minecraft.getInstance().player.getMotion().x, 0.01, Minecraft.getInstance().player.getMotion().z);
        }
    }

    public BlockPos getTargetPositionInPathArray(ArrayList<BlockPos> path) {
        int closestBlockIndex = 0;
        double closestBlockDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < path.size(); i++) {
            double distance = Minecraft.getInstance().player.getDistanceSq(path.get(i).getX(), path.get(i).getY(), path.get(i).getZ());
            if (distance < closestBlockDistance) {
                closestBlockDistance = distance;
                closestBlockIndex = i;
            }
        }

        BlockPos closestBlock = path.get(closestBlockIndex);
        BlockPos nextBlock;
        if (closestBlockIndex == path.size() - 1) {
            nextBlock = closestBlock;
        } else {
            nextBlock = path.get(closestBlockIndex + 1);
        }

        return nextBlock;
    }

    private static class Hub {
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

        public void setLoc(BlockPos loc) {
            this.loc = loc;
        }

        public Hub getParent() {
            return parent;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public ArrayList<BlockPos> getPath() {
            return path;
        }

        public void setPath(ArrayList<BlockPos> path) {
            this.path = path;
        }

        public double getSquareDistanceToFromTarget() {
            return squareDistanceToFromTarget;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        public double getCost() {
            return cost;
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

    private static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int) (
                    (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()) - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost())
            );
        }
    }
}