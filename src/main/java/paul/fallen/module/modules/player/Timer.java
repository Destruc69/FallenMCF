package paul.fallen.module.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.setting.Setting;
import paul.fallen.utils.client.ClientUtils;
import paul.fallen.utils.entity.PlayerUtils;

public class Timer extends Module {

    public Setting timer;
    public Setting bypass;

    public Timer(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        timer = new Setting("Timer", "Timer", this, 20, 1, 100);
        bypass = new Setting("Bypass", this, false);
        addSetting(timer);
        addSetting(bypass);
    }

    // See MixinMinecraft
}