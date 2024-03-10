package paul.fallen.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.world.BlockUtils;

public class LegitFightBot extends Module {

    public LegitFightBot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (findClosestEntity() != null) {
                float[] rot = RotationUtils.getYawAndPitch(findClosestEntity().getBoundingBox().getCenter());

                mc.player.rotationYaw = rot[0];
                mc.player.rotationPitch = rot[1];

                if (mc.player.getDistanceSq(findClosestEntity().getPositionVec()) > 3.5) {
                    mc.gameSettings.keyBindForward.setPressed(true);
                    mc.gameSettings.keyBindBack.setPressed(false);
                } else {
                    mc.gameSettings.keyBindForward.setPressed(false);
                    mc.gameSettings.keyBindBack.setPressed(true);
                }

                if (mc.player.getDistanceSq(findClosestEntity().getPositionVec()) < 4) {
                    if (mc.player.ticksExisted % 10 == 0) {
                        mc.playerController.attackEntity(mc.player, findClosestEntity());
                        mc.player.swingArm(Hand.MAIN_HAND);
                    }
                }
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
        if (closestEntity != null && mc.player != null && BlockUtils.canSeeBlock(closestEntity.getPosition())) { // Removed assertion for closestEntity not being null
            return closestEntity;
        }
        return null; // Moved return statement out of the if condition
    }
}
