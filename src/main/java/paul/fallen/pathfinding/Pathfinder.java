package paul.fallen.pathfinding;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import paul.fallen.ClientSupport;
import paul.fallen.utils.entity.RotationUtils;

import java.util.*;

public class Pathfinder implements ClientSupport {

    public Pathfinder() {
    }

    public List<BlockPos> findPath(BlockPos from, BlockPos to) {
        Set<BlockPos> positions = getAllPositions(from, to);
        Set<BlockPos> walkablePositions = filterWalkablePositions(positions);
        Set<JumpNode> parkourPositions = filterParkourPositions(walkablePositions);

        return aStarPathfinding(parkourPositions, from, to);
    }

    private Set<BlockPos> getAllPositions(BlockPos from, BlockPos to) {
        Set<BlockPos> positions = new HashSet<>();
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

    private Set<BlockPos> filterWalkablePositions(Set<BlockPos> positions) {
        Set<BlockPos> walkablePositions = new HashSet<>();
        for (BlockPos pos : positions) {
            if (isWalkable(pos)) {
                walkablePositions.add(pos);
            }
        }
        return walkablePositions;
    }

    private boolean isWalkable(BlockPos pos) {
        return mc.world.getBlockState(pos).isAir() && mc.world.getBlockState(pos.down()).isSolid();
    }

    private Set<JumpNode> filterParkourPositions(Set<BlockPos> walkablePositions) {
        Set<JumpNode> parkourPositions = new HashSet<>();
        for (BlockPos pos : walkablePositions) {
            List<BlockPos> potentialJumps = getNearbyJumpablePositions(pos);
            for (BlockPos jumpPos : potentialJumps) {
                if (canJump(pos, jumpPos)) {
                    parkourPositions.add(new JumpNode(pos, jumpPos));
                }
            }
        }
        return parkourPositions;
    }

    private List<BlockPos> getNearbyJumpablePositions(BlockPos pos) {
        List<BlockPos> nearbyPositions = new ArrayList<>();
        int radius = 4;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos nearbyPos = pos.add(dx, dy, dz);
                    if (!nearbyPos.equals(pos)) {
                        nearbyPositions.add(nearbyPos);
                    }
                }
            }
        }
        return nearbyPositions;
    }

    private boolean canJump(BlockPos from, BlockPos to) {
        int gapHeight = Math.abs(from.getY() - to.getY());
        if (gapHeight > 2) return false;

        int midX = (from.getX() + to.getX()) / 2;
        int midY = (from.getY() + to.getY()) / 2;
        int midZ = (from.getZ() + to.getZ()) / 2;
        BlockPos midPoint = new BlockPos(midX, midY, midZ);

        return mc.world.isAirBlock(midPoint) && mc.world.isAirBlock(midPoint.up());
    }

    private List<BlockPos> aStarPathfinding(Set<JumpNode> parkourPositions, BlockPos from, BlockPos to) {
        PriorityQueue<JumpNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.cost));
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
        Map<BlockPos, Double> gScore = new HashMap<>();
        Map<BlockPos, Double> fScore = new HashMap<>();

        JumpNode startNode = new JumpNode(from, from);
        openSet.add(startNode);
        gScore.put(from, 0.0);
        fScore.put(from, heuristicCostEstimate(from, to));

        while (!openSet.isEmpty()) {
            JumpNode currentNode = openSet.poll();
            BlockPos current = currentNode.pos;

            if (current.equals(to)) {
                return reconstructPath(cameFrom, current);
            }

            for (JumpNode neighbor : getNeighbors(currentNode, parkourPositions)) {
                BlockPos neighborPos = neighbor.pos;
                double tentativeGScore = gScore.get(current) + distance(current, neighborPos);

                if (tentativeGScore < gScore.getOrDefault(neighborPos, Double.MAX_VALUE)) {
                    cameFrom.put(neighborPos, current);
                    gScore.put(neighborPos, tentativeGScore);
                    fScore.put(neighborPos, tentativeGScore + heuristicCostEstimate(neighborPos, to));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private double heuristicCostEstimate(BlockPos from, BlockPos to) {
        return from.distanceSq(to);
    }

    private List<JumpNode> getNeighbors(JumpNode currentNode, Set<JumpNode> parkourPositions) {
        List<JumpNode> neighbors = new ArrayList<>();
        for (JumpNode node : parkourPositions) {
            if (currentNode.canReach(node)) {
                neighbors.add(node);
            }
        }
        return neighbors;
    }

    private List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos current) {
        List<BlockPos> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        Collections.reverse(totalPath);
        return totalPath;
    }

    private double distance(BlockPos a, BlockPos b) {
        return a.distanceSq(b);
    }

    private static class JumpNode {
        BlockPos pos;
        BlockPos jumpTarget;
        double cost;

        public JumpNode(BlockPos pos, BlockPos jumpTarget) {
            this.pos = pos;
            this.jumpTarget = jumpTarget;
            this.cost = pos.distanceSq(jumpTarget);
        }

        public boolean canReach(JumpNode other) {
            return this.pos.distanceSq(other.pos) <= 16.0;
        }
    }

    private int currentPathIndex = 0;
    private BlockPos currentPathPos;
    private BlockPos nextTargetPos;
    private boolean a = false;

    public void updatePathPosition(List<BlockPos> path) {
        if (currentPathPos == null) {
            currentPathPos = path.get(currentPathIndex);
            nextTargetPos = (currentPathIndex + 1 < path.size()) ? path.get(currentPathIndex + 1) : null;
        }

        assert nextTargetPos != null;
        if (isAtTarget(nextTargetPos)) {
            currentPathIndex++;

            currentPathPos = (currentPathIndex < path.size()) ? path.get(currentPathIndex) : null;
            nextTargetPos = (currentPathIndex + 1 < path.size()) ? path.get(currentPathIndex + 1) : null;
        }

        float[] r = RotationUtils.getYawAndPitch(new Vector3d(nextTargetPos.getX() + 0.5, nextTargetPos.getY(), nextTargetPos.getZ() + 0.5));
        mc.player.rotationYaw = r[0];
        mc.player.rotationPitch = r[1];

        mc.gameSettings.keyBindSprint.setPressed(true);
        mc.gameSettings.keyBindForward.setPressed(true);
        mc.gameSettings.keyBindSneak.setPressed(false);

        if (mc.player.isOnGround() && mc.player.world.getBlockState(mc.player.getPosition().down()).isAir() && !a && currentPathIndex < path.size() - 1) {
            mc.player.jump();
            a = true;
        } else {
            a = false;
        }
    }

    private boolean isAtTarget(BlockPos targetPos) {
        return mc.player.getDistanceSq(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < 1;
    }
}