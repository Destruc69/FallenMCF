package roger.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class LookUtil {

    public static Tuple<Double, Double> getAngles(Entity origin, Entity target) {
        return getAngles(origin.getPositionVec().add(0, origin.getEyeHeight(), 0), target.getPositionVec().add(0, target.getEyeHeight(), 0), origin.rotationYaw);
    }

    public static Tuple<Double, Double> getAngles(BlockPos pos) {
        Entity player = Minecraft.getInstance().player;
        return getAngles(player.getPositionVec().add(0, player.getEyeHeight(), 0), new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5f, 0.5f, 0.5f), player.rotationYaw);
    }

    public static Tuple<Double, Double> getAngles(Entity origin, Vector3d target) {
        return getAngles(origin.getPositionVec().add(0, origin.getEyeHeight(), 0), target, origin.rotationYaw);
    }

    public static Tuple<Double, Double> getAngles(Vector3d origin, Vector3d point, double currentYaw) {
        double dx = origin.x - point.x;
        double dy = origin.y - point.y;
        double dz = origin.z - point.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist == 0)
            return new Tuple<>(0.0, 0.0);

        double pitch = 90 - Math.toDegrees(Math.atan(dist / Math.abs(dy)));
        if (dy < 0)
            pitch = -pitch;

        double angle = Math.toDegrees(Math.atan(Math.abs(dx / dz)));
        double yaw;
        if(dx > 0 && dz < 0)
            yaw = angle;
        else if(dx > 0 && dz > 0)
            yaw = 180 - angle;
        else if (dx < 0 && dz > 0)
            yaw = 180 + angle;
        else
            yaw = 360 - angle;

        double diff = yaw - currentYaw;

        while (diff > 180) {
            yaw -= 360;
            diff = yaw - currentYaw;
        }

        while (diff < -180) {
            yaw += 360;
            diff = yaw - currentYaw;
        }

        return new Tuple<>(yaw, pitch);
    }
}
