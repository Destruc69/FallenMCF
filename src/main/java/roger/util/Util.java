package roger.util;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import paul.fallen.utils.client.ClientUtils;

public class Util {
    public static void msg(String msg) {
        ClientUtils.addChatMessage(msg);
    }

    public static BlockPos getPlayerBlockPos() {
        PlayerEntity player = Minecraft.getInstance().player;
        BlockPos pos = new BlockPos(Math.floor(player.getPosX()), Math.floor(player.getPosY()), Math.floor(player.getPosZ()));

        if (isBlockSolid(pos)) {
            if (isBlockSolid(pos = pos.add(0, 1, 0)))
                ClientUtils.addChatMessage("player block pos was solid! (cannot continue)");
        }

        return pos;
    }

    public static int getFallDistance(BlockPos blockPos) {
        int fallDistance = 0;
        int y = blockPos.getY();

        for (int currentY = y - 1; currentY >= 0; currentY--) {
            BlockPos currentPos = new BlockPos(blockPos.getX(), currentY, blockPos.getZ());
            if (!Minecraft.getInstance().world.getBlockState(currentPos).isAir()) {
                return fallDistance;
            }
            fallDistance++;
        }

        return fallDistance;
    }

    public static BlockPos getNextBlockUnder(BlockPos blockPos) {
        for (int y = blockPos.getY() - 1; y >= 0; y--) {
            BlockPos currentPos = new BlockPos(blockPos.getX(), y, blockPos.getZ());
            if (!Minecraft.getInstance().world.getBlockState(currentPos).isAir()) {
                return currentPos;
            }
        }

        return null;
    }

    public static BlockPos toBlockPos(Vector3d vec) {
        return new BlockPos(Math.floor(vec.x), Math.floor(vec.y), Math.floor(vec.z));
    }

    public static Vector3d vecMultiply(Vector3d vec, double scale) {
        return new Vector3d(vec.x * scale, vec.y * scale, vec.z * scale);
    }

    public static Vector3d toVec(BlockPos pos) {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isBlockSolid(BlockPos block) {
        return Minecraft.getInstance().world.getBlockState(block)
                .getBlock().getDefaultState().isSolid() ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof SlabBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof StainedGlassBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof PaneBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof FenceBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof PistonBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof EnderChestBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof TrapDoorBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof MovingPistonBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof ChestBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof StairsBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof CactusBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof WallBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof GlassBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof SkullBlock ||
                Minecraft.getInstance().world.getBlockState(block).getBlock() instanceof SandBlock;
    }


    public static double calculateAngleVec2D(Vector3d one, Vector3d two) {
        one = new Vector3d(one.x, 0, one.z);
        two = new Vector3d(two.x, 0, two.z);

        double oneMagnitude = one.distanceTo(new Vector3d(0, 0, 0));
        double twoMagnitude = two.distanceTo(new Vector3d(0, 0, 0));

        double deg = Math.toDegrees(Math.acos(one.dotProduct(two) / (oneMagnitude * twoMagnitude)));
        if (Double.isNaN(deg)) {

            //vector should be going in the opposite direction
            return 180;
        }
        return deg;
    }


}
