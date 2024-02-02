package paul.fallen.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.RelativeMovement;

import java.util.EnumSet;
import java.util.Set;

public class PacketUtils {

    public static void sendPositionPacket(double x, double y, double z) {
        Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);
        Minecraft.getInstance().getConnection().send(new ClientboundPlayerPositionPacket(x, y, z, Minecraft.getInstance().player.yya, Minecraft.getInstance().player.yHeadRot, set, 1));
    }

    public static void sendRotationPacket(double yaw, double pitch) {
        Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);
        Minecraft.getInstance().getConnection().send(new ClientboundPlayerPositionPacket(Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ(), (float) yaw, (float) pitch, set, 1));
    }
}
