package paul.fallen.module.modules.combat;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.pathfinder.AStarCustomPathFinder;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class InfiniteAura extends Module {

    private AStarCustomPathFinder aStarCustomPathFinder;
    private Entity entity;
    private long lastActionTime = 0L; // Variable to store the timestamp of the last action

    public InfiniteAura(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            Entity entity = findClosestEntity();

            // Skip if less than 1 second has passed since the last action
            if (System.currentTimeMillis() - lastActionTime < 500) {
                return;
            }

            // Set the last action time to the current time
            lastActionTime = System.currentTimeMillis();

            if (entity == null)
                return;

            // Stop all movement
            mc.player.setMotion(0, 0, 0);
            for (KeyBinding k : new ArrayList<>(Arrays.asList(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak)))
                k.setPressed(false);

            // Move to entity
            aStarCustomPathFinder = new AStarCustomPathFinder(mc.player.getPositionVec(), entity.getPositionVec());
            aStarCustomPathFinder.compute();

            this.entity = entity;

            // Move forward
            int pathSize = aStarCustomPathFinder.getPath().size(); // Store the size of the path
            for (int a = 0; a < pathSize; a++) {
                Vector3d v = aStarCustomPathFinder.getPath().get(a);
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(v.x, v.y, v.z, true));
            }

            // Look at entity
            int[] rot = RotationUtils.getYawAndPitch(aStarCustomPathFinder.getPath().get(pathSize - 1), entity.getPositionVec().add(0.5, 0.5, 0.5));
            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], true));

            // Attack entity
            mc.playerController.attackEntity(mc.player, entity);
            mc.player.swingArm(Hand.MAIN_HAND);

            // Reverse path
            ArrayList<Vector3d> vector3ds = new ArrayList<>();

            vector3ds.addAll(aStarCustomPathFinder.getPath());

            Collections.reverse(vector3ds);

            // Move back
            pathSize = vector3ds.size(); // Update the size of the path
            for (int b = 0; b < pathSize; b++) {
                Vector3d v = vector3ds.get(b);
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(v.x, v.y, v.z, true));
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (aStarCustomPathFinder.getPath().size() > 0 && aStarCustomPathFinder != null) {
                aStarCustomPathFinder.renderPath(event);
            }
            if (entity != null) {
                RenderUtils.drawOutlinedBox(entity.getPosition(), 0, 1, 0, event);
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        // Anti Flag
        if (!mc.isSingleplayer()) {
            if (event.getPacket() instanceof SPlayerPositionLookPacket) {
                SPlayerPositionLookPacket sPacketPlayerPosLook = (SPlayerPositionLookPacket) event.getPacket();
                event.setPacket(new SPlayerPositionLookPacket(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY() - Integer.MAX_VALUE, sPacketPlayerPosLook.getZ(), sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), sPacketPlayerPosLook.getFlags(), sPacketPlayerPosLook.getTeleportId()));
            }
        }
    }

    private Entity findClosestEntity() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        assert mc.world != null;
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity != null && entity != mc.player && entity instanceof LivingEntity) {
                assert mc.player != null;
                double distance = mc.player.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                if (distance < closestDistance) { // Fixed variable name
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        if (closestEntity != null && mc.player != null) { // Removed assertion for closestEntity not being null
            return closestEntity;
        }
        return null; // Moved return statement out of the if condition
    }
}