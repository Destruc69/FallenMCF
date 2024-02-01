package paul.fallen.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.RelativeMovement;
import org.joml.Vector3d;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class RotationUtils {

    public static void rotateTo(Vector3d posVec) {
        Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);
        int[] look = getYawAndPitch(posVec);
        Minecraft.getInstance().player.connection.send(new ClientboundPlayerPositionPacket(Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ(), look[0], look[1], set, 1));
    }

    public static void rotateTo(Vector3d posVec, boolean shouldCenter) {
        Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);
        int[] look = getYawAndPitch(posVec.add(0.5, 0.5, 0.5));
        Minecraft.getInstance().player.connection.send(new ClientboundPlayerPositionPacket(Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ(), look[0], look[1], set, 1));
    }

    public static int[] getYawAndPitch(Vector3d target) {
        double xDiff = target.x - Minecraft.getInstance().player.getX();
        double yDiff = target.y - (Minecraft.getInstance().player.getY() + Minecraft.getInstance().player.getEyeHeight());
        double zDiff = target.z - Minecraft.getInstance().player.getZ();

        double horizontalDistance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) Math.toDegrees(Math.atan2(-xDiff, zDiff));
        float pitch = (float) Math.toDegrees(Math.atan2(-yDiff, horizontalDistance));

        return new int[]{(int) yaw, (int) pitch};
    }
}
