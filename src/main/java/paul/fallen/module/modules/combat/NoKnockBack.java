package paul.fallen.module.modules.combat;

import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.events.PacketSendEvent;
import paul.fallen.module.Module;

public class NoKnockBack extends Module {

    public NoKnockBack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        setState(true);
    }

    @SubscribeEvent
    public void onPacketIn(PacketSendEvent event) {
        if (event.getPacket() instanceof SEntityVelocityPacket || event.getPacket() instanceof SExplosionPacket) {
            event.setCanceled(true);
        }
    }
}
