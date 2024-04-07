package paul.fallen.module.modules.player;

import net.minecraft.network.IPacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

import java.util.ArrayList;

public final class Freeze extends Module {

    private Setting inputPackets;
    private ArrayList<IPacket> packets;

    public Freeze(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        packets = new ArrayList<>();
    }

    public void onDisable() {
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