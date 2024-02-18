package paul.fallen.module.modules.movement;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.EntityUtils;

public class Jesus extends Module {

    public Jesus(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    private boolean riseUp = false;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.player.isInWater()) {
            if ((mc.player.fallDistance >= 0.0000000000000000000000000001 && !mc.player.isInWater()) || !riseUp) {
                riseUp = true;

                double y, y1;
                EntityUtils.setMotionY(0);

                if (mc.player.ticksExisted % 3 == 0) {

                    y = mc.player.getPosY() - 1.0E-10D;
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), y, mc.player.getPosZ(), true));

                }

                y1 = mc.player.getPosY() + 1.0E-10D;
                mc.player.setPosition(mc.player.getPosX(), y1, mc.player.getPosZ());
                mc.player.connection.sendPacket(new CPlayerPacket(true));

            } else {
                EntityUtils.setMotionY(0);
            }
        } else {
            riseUp = false;
        }
    }
}