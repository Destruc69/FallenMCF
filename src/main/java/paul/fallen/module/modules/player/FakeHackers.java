package paul.fallen.module.modules.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class FakeHackers extends Module {

    public FakeHackers(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            for (final Entity o : mc.world.getAllEntities()) {
                if (o instanceof PlayerEntity) {
                    final PlayerEntity player = (PlayerEntity) o;
                    if (player == mc.player) {
                        continue;
                    }
                    if (mc.player.getDistanceSq(player) > 3.8) {
                        continue;
                    }
                    final double diffX = mc.player.getPosX() - player.getPosX();
                    final double diffZ = mc.player.getPosZ() - player.getPosZ();
                    final double diffY = mc.player.getPosZ() + mc.player.getEyeHeight() - (player.getPosY() + player.getEyeHeight());
                    final double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
                    final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
                    final float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
                    final PlayerEntity entityPlayer = player;
                    entityPlayer.rotationYawHead += Math.toDegrees(yaw - player.rotationYawHead);
                    final PlayerEntity entityPlayer2 = player;
                    entityPlayer2.rotationYaw += Math.toDegrees(yaw - player.rotationYaw);
                    final PlayerEntity entityPlayer3 = player;
                    entityPlayer3.rotationPitch += Math.toDegrees(pitch - player.rotationPitch);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
