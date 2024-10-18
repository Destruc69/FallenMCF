package paul.fallen.module.modules.player;

import net.minecraft.network.IPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

import java.util.ArrayList;

public final class Freeze extends Module {

    private Setting inputPackets;
    private ArrayList<IPacket> packets;

    public Freeze(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    public void onEnable() {
        super.onEnable();
        packets = new ArrayList<>();
    }

    public void onDisable() {
        super.onDisable();
        try {
            if (!inputPackets.getValBoolean()) {
                for (IPacket packet : packets) {
                    assert mc.player != null;
                    mc.player.connection.sendPacket(packet);
                }
                packets.clear();
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacketOut(PacketEvent event) {
        if (!inputPackets.getValBoolean()) {
            packets.add(event.getPacket());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketIn(PacketEvent event) {
        if (inputPackets.getValBoolean()) {
            event.setCanceled(true);
        }
    }
}