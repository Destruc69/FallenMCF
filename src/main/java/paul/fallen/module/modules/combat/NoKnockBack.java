package paul.fallen.module.modules.combat;

import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

public class NoKnockBack extends Module {

    public NoKnockBack(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onPacketIn(PacketEvent event) {
        if (event.getPacket() instanceof SEntityVelocityPacket || event.getPacket() instanceof SExplosionPacket) {
            event.setCanceled(true);
        }
    }
}
