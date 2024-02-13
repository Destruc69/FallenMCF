package paul.fallen.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.pathfinder.AStarCustomPathFinder;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;

public class InfiniteAura extends Module {

    private AStarCustomPathFinder aStarCustomPathFinder;
    private Entity entity;

    public InfiniteAura(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Entity entity = findClosestEntity();
        if (entity != null) {
            if (mc.player.ticksExisted % 10 == 0) {

                //Attack entity
                mc.playerController.attackEntity(mc.player, entity);
                mc.player.swingArm(Hand.MAIN_HAND);

                // Move to entity
                aStarCustomPathFinder = new AStarCustomPathFinder(mc.player.getPositionVec(), entity.getPositionVec());
                aStarCustomPathFinder.compute();

                this.entity = entity;

                // Move forward
                for (int a = 0; a < aStarCustomPathFinder.getPath().size() - 1; a++) {
                    Vector3d v = aStarCustomPathFinder.getPath().get(a);
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(v.x, v.y, v.z, true));
                }

                // Look at entity
                int[] rot = RotationUtils.getYawAndPitch(aStarCustomPathFinder.getPath().get(aStarCustomPathFinder.getPath().size()), entity.getPositionVec().add(0.5, 0.5, 0.5));
                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], true));

                // Move back from entity
                aStarCustomPathFinder = new AStarCustomPathFinder(entity.getPositionVec(), mc.player.getPositionVec());
                aStarCustomPathFinder.compute();

                //Move back
                for (int b = 0; b < aStarCustomPathFinder.getPath().size() - 1; b++) {
                    Vector3d v = aStarCustomPathFinder.getPath().get(b);
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(v.x, v.y, v.z, true));
                }
            }
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