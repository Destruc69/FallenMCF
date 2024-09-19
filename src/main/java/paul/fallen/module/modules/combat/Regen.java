package paul.fallen.module.modules.combat;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class Regen extends Module {

    public Regen(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            for (int i = 0; i < 200; i ++) {
                mc.player.connection.sendPacket(new CPlayerPacket());
            }
        } catch (Exception ignored) {
        }
    }
}