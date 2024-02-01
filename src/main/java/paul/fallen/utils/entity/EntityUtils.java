package paul.fallen.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3d;

public class EntityUtils {

    public static int getFallDistance(Entity entity) {
        BlockPos pos = entity.getOnPos();

        int c = 0;

        while (true) {
            assert Minecraft.getInstance().level != null;
            if (!Minecraft.getInstance().level.getBlockState(pos).getBlock().equals(Blocks.AIR)) break;
            pos = pos.below();
            c++;
        }

        return c;
    }

    public static BlockPos rayTraceBlocks(Vector3d start, Vector3d end) {
        double startX = start.x();
        double startY = start.y();
        double startZ = start.z();

        double endX = end.x();
        double endY = end.y();
        double endZ = end.z();

        double stepX = (endX - startX) / 100.0; // Adjust the number of steps as needed
        double stepY = (endY - startY) / 100.0;
        double stepZ = (endZ - startZ) / 100.0;

        for (int i = 0; i < 100; i++) {
            int x = (int) (startX + i * stepX);
            int y = (int) (startY + i * stepY);
            int z = (int) (startZ + i * stepZ);

            // Replace the following condition with your block checking logic
            if (Minecraft.getInstance().level.getBlockState(new BlockPos(x, y, z)).isSolid()) {
                return new BlockPos(x, y, z);
            }
        }

        return null; // No block hit
    }

    public static void setMotionX(double x) {
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.setDeltaMovement(x, Minecraft.getInstance().player.getDeltaMovement().y, Minecraft.getInstance().player.getDeltaMovement().z);
    }

    public static void setMotionY(double y) {
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.setDeltaMovement(Minecraft.getInstance().player.getDeltaMovement().x, y, Minecraft.getInstance().player.getDeltaMovement().z);
    }

    public static void setMotionZ(double z) {
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.setDeltaMovement(Minecraft.getInstance().player.getDeltaMovement().x, Minecraft.getInstance().player.getDeltaMovement().y, z);
    }

    public static void setEMotionX(Entity entity, double x) {
        assert Minecraft.getInstance().player != null;
        entity.setDeltaMovement(x, entity.getDeltaMovement().y, entity.getDeltaMovement().z);
    }

    public static void setEMotionY(Entity entity, double y) {
        assert Minecraft.getInstance().player != null;
        entity.setDeltaMovement(entity.getDeltaMovement().x, y, entity.getDeltaMovement().z);
    }

    public static void setEMotionZ(Entity entity, double z) {
        assert Minecraft.getInstance().player != null;
        entity.setDeltaMovement(entity.getDeltaMovement().x, entity.getDeltaMovement().y, z);
    }

    public static boolean isEntityMoving(Entity entity) {
        double x = entity.getDeltaMovement().x;
        double y = entity.getDeltaMovement().y;
        double z = entity.getDeltaMovement().z;

        if (x < 0.1 && x > -0.1 && y < 0.1 && y > -0.1 && z < 0.1 && z > -0.1) {
            return false;
        } else {
            return false;
        }
    }
}
