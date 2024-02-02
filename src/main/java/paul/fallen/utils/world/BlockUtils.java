package paul.fallen.utils.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import paul.fallen.ClientSupport;

import java.util.Arrays;
import java.util.List;

public class BlockUtils implements ClientSupport {

    public static List<Block> emptyBlocks;
    public static List<Block> rightclickableBlocks;

    static {
        emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.LAVA, Blocks.WATER, Blocks.VINE, Blocks.SNOW, Blocks.TALL_GRASS, Blocks.FIRE);
        rightclickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.ACACIA_BUTTON, Blocks.BIRCH_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.OAK_BUTTON, Blocks.POLISHED_BLACKSTONE_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.WARPED_BUTTON, Blocks.STONE_BUTTON, Blocks.COMPARATOR, Blocks.REPEATER, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTE_BLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.RED_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE);
    }

    public static Block getBlock(int x, int y, int z) {
        return mc.level.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static Block getBlockAbovePlayer(Player inPlayer, double blocks) {
        blocks += inPlayer.getEyeHeight();
        return getBlockAtPos(new BlockPos((int)inPlayer.getX(), (int)inPlayer.getY() + (int)blocks, (int)inPlayer.getZ()));
    }

    public static Block getBlockAtPos(BlockPos inBlockPos) {
        return mc.level.getBlockState(inBlockPos).getBlock();
    }

    public static Block getBlockAtPosC(Player inPlayer, double x, double y, double z) {
        return getBlockAtPos(new BlockPos((int) (inPlayer.getX() - x), (int) (inPlayer.getY() - y), (int) (inPlayer.getZ() - z)));
    }

    public static float getBlockDistance(float xDiff, float yDiff, float zDiff) {
        return (float) Math.sqrt(((xDiff - 0.5F) * (xDiff - 0.5F)) + ((yDiff - 0.5F) * (yDiff - 0.5F))
                + ((zDiff - 0.5F) * (zDiff - 0.5F)));
    }

    public static BlockPos getBlockPos(BlockPos inBlockPos) {
        return inBlockPos;
    }

    public static BlockPos getBlockPos(double x, double y, double z) {
        return getBlockPos(new BlockPos((int) x, (int) y, (int) z));
    }

    public static BlockPos getBlockPosUnderPlayer(Player inPlayer) {
        return new BlockPos((int) inPlayer.getX(), (int) ((inPlayer.getY() + (mc.player.getDeltaMovement().y + 0.1D)) - 1D), (int) inPlayer.getZ());
    }

    public static Block getBlockUnderPlayer(Player inPlayer) {
        return getBlockAtPos(
                new BlockPos((int) inPlayer.getX(), (int) ((inPlayer.getY() + (mc.player.getDeltaMovement().y + 0.1D)) - 1D), (int) inPlayer.getZ()));
    }

    public static float getHorizontalPlayerBlockDistance(BlockPos blockPos) {
        float xDiff = (float) (mc.player.getX() - blockPos.getX());
        float zDiff = (float) (mc.player.getZ() - blockPos.getZ());
        return (float) Math.sqrt(((xDiff - 0.5F) * (xDiff - 0.5F)) + ((zDiff - 0.5F) * (zDiff - 0.5F)));
    }
}