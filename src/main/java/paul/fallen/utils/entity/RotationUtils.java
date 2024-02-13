package paul.fallen.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;

public class RotationUtils {

    public static void rotateTo(Vector3d posVec) {
        int[] look = getYawAndPitch(posVec);
        Minecraft.getInstance().player.connection.sendPacket(new CPlayerPacket.RotationPacket(look[0], look[1], Minecraft.getInstance().player.isOnGround()));
    }

    public static void rotateTo(Vector3d posVec, boolean shouldCenter) {
        int[] look = getYawAndPitch(posVec.add(0.5, 0.5, 0.5));
        Minecraft.getInstance().player.connection.sendPacket(new CPlayerPacket.RotationPacket(look[0], look[1], Minecraft.getInstance().player.isOnGround()));
    }

    public static int[] getYawAndPitch(Vector3d target) {
        double xDiff = target.x - Minecraft.getInstance().player.getPosX();
        double yDiff = target.y - (Minecraft.getInstance().player.getPosY() + Minecraft.getInstance().player.getEyeHeight());
        double zDiff = target.z - Minecraft.getInstance().player.getPosZ();

        double horizontalDistance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) Math.toDegrees(Math.atan2(-xDiff, zDiff));
        float pitch = (float) Math.toDegrees(Math.atan2(-yDiff, horizontalDistance));

        return new int[]{(int) yaw, (int) pitch};
    }

    public static int[] getYawAndPitch(Vector3d base, Vector3d target) {
        double xDiff = target.x - base.x;
        double yDiff = target.y - base.y;
        double zDiff = target.z - base.z;

        double horizontalDistance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) Math.toDegrees(Math.atan2(-xDiff, zDiff));
        float pitch = (float) Math.toDegrees(Math.atan2(-yDiff, horizontalDistance));

        return new int[]{(int) yaw, (int) pitch};
    }
}
