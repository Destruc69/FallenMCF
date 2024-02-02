package paul.fallen.pathfinder;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AStarCustomPathFinder {
    private static final Vector3d[] flatCardinalDirections = {
            new Vector3d(1, 0, 0),
            new Vector3d(-1, 0, 0),
            new Vector3d(0, 0, 1),
            new Vector3d(0, 0, -1)
    };
    private final Vector3d startVec3;
    private final Vector3d endVec3;
    private final ArrayList<Hub> hubs = new ArrayList<Hub>();
    private final ArrayList<Hub> hubsToWork = new ArrayList<Hub>();
    private final double minDistanceSquared = 9;
    private final boolean nearest = true;
    private ArrayList<Vector3d> path = new ArrayList<Vector3d>();

    public AStarCustomPathFinder(Vector3d startVec3, Vector3d endVec3) {
        this.startVec3 = startVec3;
        this.endVec3 = endVec3;
    }

    public static boolean checkPositionValidity(Vector3d loc, boolean checkGround) {
        return checkPositionValidity((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), checkGround);
    }

    public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
    }

    private static boolean isBlockSolid(BlockPos block) {
        return Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).isSolid() ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof SlabBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof StairsBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof CactusBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof ChestBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof EnderChestBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof SkullBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof PaneBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof FenceBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof WallBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof GlassBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof PistonBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof StainedGlassBlock) ||
                (Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof TrapDoorBlock);
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        return !(Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof FenceBlock) &&
                !(Minecraft.getInstance().world.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof WallBlock);
    }

    public ArrayList<Vector3d> getPath() {
        return path;
    }

    public void compute() {
        compute(1000, 4);
    }

    public void compute(int loops, int depth) {
        path.clear();
        hubsToWork.clear();
        ArrayList<Vector3d> initPath = new ArrayList<Vector3d>();
        initPath.add(startVec3);
        hubsToWork.add(new Hub(startVec3, null, initPath, startVec3.squareDistanceTo(endVec3), 0, 0));
        search:
        for (int i = 0; i < loops; i++) {
            Collections.sort(hubsToWork, new CompareHub());
            int j = 0;
            if (hubsToWork.size() == 0) {
                break;
            }
            for (Hub hub : new ArrayList<Hub>(hubsToWork)) {
                j++;
                if (j > depth) {
                    break;
                } else {
                    hubsToWork.remove(hub);
                    hubs.add(hub);

                    for (Vector3d direction : flatCardinalDirections) {
                        Vector3d loc = hub.getLoc().add(direction);
                        if (checkPositionValidity(loc, false)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    Vector3d loc1 = hub.getLoc().add(0, 1, 0);
                    if (checkPositionValidity(loc1, false)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    Vector3d loc2 = hub.getLoc().add(0, -1, 0);
                    if (checkPositionValidity(loc2, false)) {
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

    public Hub isHubExisting(Vector3d loc) {
        for (Hub hub : hubs) {
            if (hub.getLoc().getX() == loc.getX() && hub.getLoc().getY() == loc.getY() && hub.getLoc().getZ() == loc.getZ()) {
                return hub;
            }
        }
        for (Hub hub : hubsToWork) {
            if (hub.getLoc().getX() == loc.getX() && hub.getLoc().getY() == loc.getY() && hub.getLoc().getZ() == loc.getZ()) {
                return hub;
            }
        }
        return null;
    }

    public boolean addHub(Hub parent, Vector3d loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if ((loc.getX() == endVec3.getX() && loc.getY() == endVec3.getY() && loc.getZ() == endVec3.getZ()) || (minDistanceSquared != 0 && loc.squareDistanceTo(endVec3) <= minDistanceSquared)) {
                path.clear();
                path = parent.getPath();
                path.add(loc);
                return true;
            } else {
                ArrayList<Vector3d> path = new ArrayList<Vector3d>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(endVec3), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<Vector3d> path = new ArrayList<Vector3d>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    private class Hub {
        private Vector3d loc = null;
        private Hub parent = null;
        private ArrayList<Vector3d> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(Vector3d loc, Hub parent, ArrayList<Vector3d> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public Vector3d getLoc() {
            return loc;
        }

        public void setLoc(Vector3d loc) {
            this.loc = loc;
        }

        public Hub getParent() {
            return parent;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public ArrayList<Vector3d> getPath() {
            return path;
        }

        public void setPath(ArrayList<Vector3d> path) {
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

    public class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int) (
                    (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()) - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost())
            );
        }
    }
}