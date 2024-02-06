package paul.fallen.pathfinder;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AStarCustomPathFinder {
    private Vector3d startVector3d;
    private Vector3d endVector3d;
    private ArrayList<Vector3d> path = new ArrayList<Vector3d>();
    private ArrayList<Hub> hubs = new ArrayList<Hub>();
    private ArrayList<Hub> hubsToWork = new ArrayList<Hub>();
    private double minDistanceSquared = 9;
    private boolean nearest = true;

    private static Vector3d[] flatCardinalDirections = {
            new Vector3d(1, 0, 0),
            new Vector3d(-1, 0, 0),
            new Vector3d(0, 0, 1),
            new Vector3d(0, 0, -1),

            new Vector3d(1, 1, 0),
            new Vector3d(-1, 1, 0),
            new Vector3d(0, 1, 1),
            new Vector3d(0, 1, -1),

            new Vector3d(1, -1, 0),
            new Vector3d(-1, -1, 0),
            new Vector3d(0, -1, 1),
            new Vector3d(0, -1, -1)
    };

    public AStarCustomPathFinder(Vector3d startVector3d, Vector3d endVector3d) {
        this.startVector3d = new Vector3d((int) startVector3d.x, (int) startVector3d.y, (int) startVector3d.z);
        this.endVector3d = new Vector3d((int) endVector3d.x, (int) endVector3d.y, (int) endVector3d.z);
    }

    public ArrayList<Vector3d> getPath() {
        return path;
    }

    public void compute() {
        compute(500, 2);
    }

    public void compute(int loops, int depth) {
        path.clear();
        hubsToWork.clear();
        ArrayList<Vector3d> initPath = new ArrayList<Vector3d>();
        initPath.add(startVector3d);
        hubsToWork.add(new Hub(startVector3d, null, initPath, startVector3d.squareDistanceTo(endVector3d), 0, 0));
        search:
        for (int i = 0; i < loops; i++) {
            Collections.sort(hubsToWork, new CompareHub());
            int j = 0;
            if (hubsToWork.isEmpty()) {
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

    public static boolean checkPositionValidity(Vector3d loc, boolean checkGround) {
        return checkPositionValidity((int) loc.x, (int) loc.y, (int) loc.z, checkGround);
    }

    public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
    }

    private static boolean isBlockSolid(BlockPos block) {
        return !Minecraft.getInstance().world.getBlockState(block).getBlock().equals(Blocks.AIR);
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        Minecraft mc = Minecraft.getInstance();
        return mc.world.getBlockState(block).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(block.add(0, -1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(block.add(0, 1, 0)).getBlock().equals(Blocks.AIR);
    }

    public Hub isHubExisting(Vector3d loc) {
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

    public boolean addHub(Hub parent, Vector3d loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if (loc.equals(endVector3d) || (minDistanceSquared != 0 && loc.squareDistanceTo(endVector3d) <= minDistanceSquared)) {
                path.clear();
                path = new ArrayList<Vector3d>(parent.getPath());
                path.add(loc);
                return true;
            } else {
                ArrayList<Vector3d> path = new ArrayList<Vector3d>(parent.getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(endVector3d), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<Vector3d> path = new ArrayList<Vector3d>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endVector3d));
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

        public Hub getParent() {
            return parent;
        }

        public ArrayList<Vector3d> getPath() {
            return path;
        }

        public double getSquareDistanceToFromTarget() {
            return squareDistanceToFromTarget;
        }

        public double getCost() {
            return cost;
        }

        public void setLoc(Vector3d loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(ArrayList<Vector3d> path) {
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
            return (int) (
                    (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()) - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost())
            );
        }
    }

    public double[] calculateMotion(ArrayList<Vector3d> path, double rotationYaw, double speed) {
        Minecraft mc = Minecraft.getInstance();
        double playerX = mc.player.getPosX();
        double playerZ = mc.player.getPosZ();
        double velocityX = mc.player.getMotion().x;
        double velocityZ = mc.player.getMotion().z;

        rotationYaw = Math.toRadians(rotationYaw);

        int closestBlockIndex = 0;
        double closestBlockDistance = Double.POSITIVE_INFINITY;

        for (int i = 0; i < path.size(); i++) {
            double distance = mc.player.getDistanceSq(new Vector3d(path.get(i).getX(), path.get(i).getY(), path.get(i).getZ()));
            if (distance < closestBlockDistance) {
                closestBlockDistance = distance;
                closestBlockIndex = i;
            }
        }

        BlockPos closestBlock = new BlockPos(path.get(closestBlockIndex));

        // Ensure we don't exceed the array size when accessing nextBlock
        BlockPos nextBlock = (closestBlockIndex == path.size() - 1) ? closestBlock : new BlockPos(path.get(closestBlockIndex + 1));

        // Adjust delta values based on player's velocity
        double deltaX = nextBlock.getX() - playerX + velocityX;
        double deltaZ = nextBlock.getZ() - playerZ + velocityZ;

        double targetAngle = Math.atan2(deltaZ, deltaX);
        double playerAngle = rotationYaw; // Use radians directly

        // Smoothly adjust the player's rotationYaw
        double angleDifference = targetAngle - playerAngle;

        // Ensure the angle difference is within the range [-π, π]
        angleDifference = (angleDifference + Math.PI) % (2 * Math.PI) - Math.PI;

        // Calculate the distance to the target position
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // Calculate the motion components (motionX and motionZ)
        double motionX = Math.cos(angleDifference) * distance;
        double motionZ = Math.sin(angleDifference) * distance;

        // Apply motion limits based on speed
        double motionMagnitude = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (motionMagnitude > speed) {
            motionX *= speed / motionMagnitude;
            motionZ *= speed / motionMagnitude;
        }

        return new double[]{motionX, motionZ};
    }
}