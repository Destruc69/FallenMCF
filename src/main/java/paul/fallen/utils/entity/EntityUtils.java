package paul.fallen.utils.entity;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class EntityUtils {

    public static int getFallDistance(Entity entity) {
        BlockPos pos = entity.getPosition();

        int c = 0;

        while (true) {
            assert Minecraft.getInstance().world != null;
            if (!Minecraft.getInstance().world.getBlockState(pos).getBlock().equals(Blocks.AIR)) break;
            pos = pos.add(0, -0.1, 0);
            c++;
        }

        return c;
    }

    public static RayTraceResult rayTraceBlocks(Vector3d start, Vector3d end) {
        RayTraceContext context = new RayTraceContext(new Vector3d(start.x, start.y, start.z), new Vector3d(end.x, end.y, end.z), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, null);
        assert Minecraft.getInstance().world != null;
        return Minecraft.getInstance().world.rayTraceBlocks(context);
    }

    public static void setMotionX(double x) {
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.setMotion(x, Minecraft.getInstance().player.getMotion().y, Minecraft.getInstance().player.getMotion().z);
    }

    public static void setMotionY(double y) {
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.setMotion(Minecraft.getInstance().player.getMotion().x, y, Minecraft.getInstance().player.getMotion().z);
    }

    public static void setMotionZ(double z) {
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.setMotion(Minecraft.getInstance().player.getMotion().x, Minecraft.getInstance().player.getMotion().y, z);
    }

    public static void setEMotionX(Entity entity, double x) {
        assert Minecraft.getInstance().player != null;
        entity.setMotion(x, entity.getMotion().y, entity.getMotion().z);
    }

    public static void setEMotionY(Entity entity, double y) {
        assert Minecraft.getInstance().player != null;
        entity.setMotion(entity.getMotion().x, y, entity.getMotion().z);
    }

    public static void setEMotionZ(Entity entity, double z) {
        assert Minecraft.getInstance().player != null;
        entity.setMotion(entity.getMotion().x, entity.getMotion().y, z);
    }

    public static boolean isEntityMoving(Entity entity) {
        double x = entity.getMotion().x;
        double y = entity.getMotion().y;
        double z = entity.getMotion().z;

        if (x < 0.1 && x > -0.1 && y < 0.1 && y > -0.1 && z < 0.1 && z > -0.1) {
            return false;
        } else {
            return false;
        }
    }
}
