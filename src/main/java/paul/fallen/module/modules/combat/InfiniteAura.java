package paul.fallen.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.pathfinding.LocomotionPathfinder;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InfiniteAura extends Module {

    private final Setting antiTP;
    private LocomotionPathfinder aStarCustomPathFinder;
    private Entity targetEntity;
    private long lastActionTime = 0L;

    public InfiniteAura(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        antiTP = new Setting("AntiTP", this, false);
        addSetting(antiTP);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        aStarCustomPathFinder = null;
        targetEntity = null;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (System.currentTimeMillis() - lastActionTime < 500) return;

        lastActionTime = System.currentTimeMillis();

        Entity entity = findClosestEntity();
        if (entity == null) return;

        this.targetEntity = entity;

        // Calculate path to entity
        aStarCustomPathFinder = new LocomotionPathfinder(mc.player.getPosition(), entity.getPosition());
        aStarCustomPathFinder.compute();

        // Move to entity
        for (BlockPos pos : aStarCustomPathFinder.getPath()) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(pos.getX(), pos.getY(), pos.getZ(), true));
        }

        // Look at entity
        Vector3d targetPos = new Vector3d(
                aStarCustomPathFinder.getPath().get(aStarCustomPathFinder.getPath().size() - 1).getX() + 0.5,
                aStarCustomPathFinder.getPath().get(aStarCustomPathFinder.getPath().size() - 1).getY() + 1.5,
                aStarCustomPathFinder.getPath().get(aStarCustomPathFinder.getPath().size() - 1).getZ() + 0.5
        );
        float[] rot = RotationUtils.getYawAndPitch(targetPos, entity.getBoundingBox().getCenter());
        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], true));

        // Attack entity
        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(Hand.MAIN_HAND);

        // Move back
        List<BlockPos> reversedPath = new ArrayList<>(aStarCustomPathFinder.getPath());
        Collections.reverse(reversedPath);
        for (BlockPos pos : reversedPath) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(pos.getX(), pos.getY(), pos.getZ(), true));
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!antiTP.getValBoolean()) return;

        if (event.getPacket() instanceof SPlayerPositionLookPacket) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), mc.player.isOnGround()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (aStarCustomPathFinder != null && !aStarCustomPathFinder.getPath().isEmpty()) {
            aStarCustomPathFinder.renderPath(event);
        }
        if (targetEntity != null) {
            RenderUtils.drawOutlinedBox(targetEntity.getPosition(), 0, 1, 0, event);
        }
    }

    private Entity findClosestEntity() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        if (mc.world == null || mc.player == null) return null;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity != null && entity != mc.player && entity instanceof LivingEntity) {
                double distance = mc.player.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        return closestEntity;
    }
}