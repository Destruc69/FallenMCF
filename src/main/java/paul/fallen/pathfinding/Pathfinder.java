package paul.fallen.pathfinding;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import paul.fallen.utils.world.BlockUtils;

import java.util.*;

public class Pathfinder {
    private final CustomBlockPos start;
    private final CustomBlockPos end;
    private final ArrayList<CustomBlockPos> path;

    public static final int TRAVERSE_COST = 1;
    public static final int BREAK_COST = 5;
    public static final int PLACE_COST = 10;

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
        } else if (canPlace(from, to)) {
            return PLACE_COST;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private boolean canTraverse(BlockPos from, BlockPos to) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockState below = minecraft.world.getBlockState(to.down());
        BlockState current = minecraft.world.getBlockState(to);
        BlockState above = minecraft.world.getBlockState(to.up());

        // Ensure the block below is solid enough to walk on
        boolean belowSolid = !below.isAir() && below.getMaterial().isSolid();

        // Ensure the current block is either air or a walkable surface
        boolean currentWalkable = current.isAir() || current.getMaterial().isReplaceable();

        // Ensure the block above is clear enough to not obstruct movement
        boolean aboveClear = above.isAir() || above.getMaterial().isReplaceable();

        return belowSolid && currentWalkable && aboveClear;
    }

    private boolean canBreak(BlockPos from, BlockPos to) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockState state = minecraft.world.getBlockState(to);
        boolean isNotAir = !state.isAir();
        boolean isNotLiquid = !state.getMaterial().isLiquid();

        // Ensure breaking is valid (not air or liquid) and only in horizontal or downward directions
        return isNotAir && isNotLiquid && isHorizontalOrDownward(from, to);
    }

    private boolean isHorizontalOrDownward(BlockPos from, BlockPos to) {
        // Check if the target is either at the same Y level or below the player's position
        return to.getY() <= from.getY();
    }

    private boolean canPlace(BlockPos from, BlockPos to) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockState state = minecraft.world.getBlockState(to);
        BlockState below = minecraft.world.getBlockState(to.down());

        boolean isAir = state.isAir();
        boolean isReplaceable = state.getMaterial().isReplaceable();

        // Check if the position to place a block is valid: air or replaceable
        boolean validPlacement = isAir || isReplaceable;

        // For horizontal or upward bridging, ensure the block below is solid or non-air
        boolean belowSolid = !below.isAir() && !below.getMaterial().isLiquid();

        // Allow placing if below is solid, or if the player is bridging upwards
        return validPlacement && (belowSolid || from.getY() < to.getY());
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

    public void act() {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;

        if (player == null || path.isEmpty()) {
            return; // No path or player is not available
        }

        // Get the player's current position
        BlockPos playerPos = player.getPosition();

        // Check if player has reached the end of the path
        CustomBlockPos currentTarget = path.get(0);
        BlockPos targetPos = currentTarget.getBlockPos();

        // Check if we are close enough to the target
        if (playerPos.distanceSq(targetPos) < 1.0) {
            path.remove(0); // Remove the current target as we reached it
            if (path.isEmpty()) {
                return; // No more path to follow
            }
            currentTarget = path.get(0);
            targetPos = currentTarget.getBlockPos();
        }

        // Check if the player needs to bridge upwards
        boolean needsBridgingUpwards = targetPos.getY() > playerPos.getY() && needsUpwardBridging(playerPos);

        // Handle setting up on a block before bridging upwards
        if (needsBridgingUpwards) {
            BlockPos blockBelowPlayer = playerPos.down();
            BlockState belowState = minecraft.world.getBlockState(blockBelowPlayer);

            // Ensure the player is standing on a solid block before bridging upwards
            if (belowState.isAir()) {
                BlockUtils.placeBlock(blockBelowPlayer, player.inventory.currentItem, true, true);
                return; // Return after placing the block to stabilize the player
            } else if (player.isOnGround()) {
                // Jump and place block upwards during bridging
                player.jump();
                BlockPos blockBelow = playerPos.down();
                BlockUtils.placeBlock(blockBelow, player.inventory.currentItem, true, true);
                return; // Return after jumping and placing to continue the process
            }
        }

        // Handle horizontal and downward directions
        if (!needsBridgingUpwards) {
            // Get the direction to the target
            double dx = targetPos.getX() - playerPos.getX();
            double dy = targetPos.getY() - playerPos.getY();
            double dz = targetPos.getZ() - playerPos.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Normalize direction
            dx /= distance;
            dy /= distance;
            dz /= distance;

            // Set player yaw and pitch towards the target
            player.rotationYaw = (float) Math.atan2(dz, dx) * (180.0F / (float) Math.PI) - 90.0F;
            player.rotationPitch = (float) -Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * (180.0F / (float) Math.PI);

            // Execute actions for horizontal and downward directions
            if (canBreak(playerPos, targetPos) && playerPos.getY() >= targetPos.getY()) {
                BlockUtils.breakBlock(targetPos, player.inventory.currentItem, true, true);
            } else if (canPlace(playerPos, targetPos)) {
                BlockUtils.placeBlock(targetPos, player.inventory.currentItem, true, true);
            }
        }
    }

    /**
     * Checks if the next positions in the path require upward bridging.
     * If there are blocks that need to be placed upwards from the ground, return true.
     */
    private boolean needsUpwardBridging(BlockPos playerPos) {
        for (CustomBlockPos pos : path) {
            BlockPos blockPos = pos.getBlockPos();
            if (blockPos.getY() > playerPos.getY()) {
                // Check if the action assigned to this block is a "place" action
                if (pos.actionCost == PLACE_COST) {
                    return true;
                }
            }
        }
        return false;
    }
}