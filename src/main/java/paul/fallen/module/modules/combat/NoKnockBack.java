package paul.fallen.module.modules.combat;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.events.packetevent.PacketEvent;
import paul.fallen.module.Module;

public class NoKnockBack extends Module {

    public NoKnockBack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onPacketIn(PacketEvent event) {
        if (event.getPacket() instanceof ServerboundMovePlayerPacket) {
            event.setCanceled(true);
        }
    }
}
