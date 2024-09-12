package roger.pathfind.main.astar;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import roger.util.Util;

public class AStarNode {
    private double hCost;
    private int gCost;

    private final int x;
    private final int y;
    private final int z;

    private AStarNode parent;
    private BlockPos blockPos;

    private boolean isJumpNode;

    private boolean isFallNode;

    public AStarNode(BlockPos pos, AStarNode parentNode, AStarNode endNode) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.blockPos = pos;

        calculateHeuristic(endNode);
        setParent(parentNode);
    }

    public AStarNode(int xRel, int yRel, int zRel, AStarNode parentNode, AStarNode endNode) {
        this.x = xRel + parentNode.getX();
        this.y = yRel + parentNode.getY();
        this.z = zRel + parentNode.getZ();
        this.blockPos = new BlockPos(x, y, z);

        calculateHeuristic(endNode);
        setParent(parentNode);
    }

    public AStarNode(BlockPos pos, AStarNode endNode) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.blockPos = pos;

        calculateHeuristic(endNode);
    }

    public AStarNode(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public boolean canBeTraversed() {
        BlockState currentState = Minecraft.getInstance().world.getBlockState(blockPos);
        Block currentBlock = currentState.getBlock();

        if (parent == null) {
            return false;
        }

        // Check for fall node conditions
        if (parent.isFallNode() && parent.getY() == y) {
            return false;
        }

        if (parent.isFallNode() && Util.getFallDistance(blockPos) > 4
                && Minecraft.getInstance().world.getBlockState(Util.getNextBlockUnder(blockPos)).getBlock() != Blocks.WATER) {
            return false;
        }

        // Check water conditions
        if (currentBlock == Blocks.WATER) {
            BlockState aboveState = Minecraft.getInstance().world.getBlockState(blockPos.up());
            Block aboveBlock = aboveState.getBlock();
            if (aboveBlock == Blocks.WATER) return false;
            if (aboveBlock == Blocks.AIR) return true;
        }

        // Check if the current or next block is solid
        if (isBlockSolid(blockPos) || isBlockSolid(blockPos.up())) {
            return false;
        }

        // Ensure enough space to move
        if (isBlockSolid(blockPos.down()) && !isBlockSolid(blockPos) && !isBlockSolid(blockPos.up())) {
            return true;
        }

        // Check if we need to jump
        if (parent.blockPos.getY() - 1 == y - 2 && isBlockSolid(blockPos.down(2))) {
            setJumpNode(true);
            return true;
        }

        // Handle falling
        if (parent.isFallNode() && y == parent.getY() - 1) {
            setFallNode(true);
            return true;
        }

        // Handle fall origin
        if (parent.blockPos.getY() == y && isBlockSolid(parent.blockPos.down())) {
            setFallNode(true);
            return true;
        }

        return false;
    }

    private boolean isBlockSolid(BlockPos block) {
        return Minecraft.getInstance().world.getBlockState(block)
                .getBlock().getDefaultState().isSolid() ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof SlabBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof StainedGlassBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof PaneBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof FenceBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof PistonBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof EnderChestBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof TrapDoorBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof PistonBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof ChestBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof StairsBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof CactusBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof WallBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof GlassBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof SkullBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof SandBlock;
    }

    public void calculateHeuristic(AStarNode endNode) {
        this.hCost = (Math.abs(endNode.getX() - x) + Math.abs(endNode.getY() - y) + Math.abs(endNode.getZ() - z)) * 10;
    }

    public double calculateHeuristicDouble(AStarNode endNode) {
        return (Math.abs(endNode.getX() - x) + Math.abs(endNode.getY() - y) + Math.abs(endNode.getZ() - z)) * 10;
    }

    public void setParent(AStarNode parent) {
        this.parent = parent;

        int xDiff = Math.abs(x - parent.getX());
        int yDiff = Math.abs(y - parent.getY());
        int zDiff = Math.abs(z - parent.getZ());


        this.gCost = parent.getGCost() + (xDiff + yDiff + zDiff) * 10;
    }

    public double getTotalCost() {
        return hCost + gCost;
    }

    public int getX() {
        return x;
    }

    public int getGCost() {
        return gCost;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public AStarNode getParent() {
        return parent;
    }

    public Vector3d asVec3(double xAdd, double yAdd, double zAdd) {
        return new Vector3d(x + xAdd, y + yAdd, z + zAdd);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AStarNode))
            return false;

        AStarNode other = (AStarNode) o;

        return x == other.getX() && y == other.getY() && z == other.getZ();
    }

    public void setJumpNode(boolean jumpNode) {
        isJumpNode = jumpNode;
    }

    public void setFallNode(boolean fallNode) {
        isFallNode = fallNode;
    }

    public boolean isFallNode() {
        return isFallNode;
    }

    public boolean isJumpNode() {
        return isJumpNode;
    }
}


