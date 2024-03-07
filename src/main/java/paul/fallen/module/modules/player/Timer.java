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

    private Setting timer;
    private Setting bypass;

    private float i = 1;
    private boolean increasing = true;

    public Timer(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        timer = new Setting("Timer", "Timer", this, 1, 0.1f, 5);
        bypass = new Setting("Bypass", this, false);
        addSetting(timer);
        addSetting(bypass);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        PlayerUtils.setTickSpeed(1);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        i = 1;
        increasing = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (!bypass.bval) {
            PlayerUtils.setTickSpeed(timer.dval);
        } else {
            if (timer.dval > 1) {
                if (increasing) {
                    if (i < timer.dval) {
                        i += 0.01f;
                    } else {
                        increasing = false;
                    }
                } else {
                    if (i > 1) {
                        i -= 0.01f;
                    } else {
                        increasing = true;
                    }
                }
            } else {
                if (increasing) {
                    if (i < 1) {
                        i += 0.01f;
                    } else {
                        increasing = false;
                    }
                } else {
                    if (i > timer.dval) {
                        i -= 0.01f;
                    } else {
                        increasing = true;
                    }
                }

            }

            PlayerUtils.setTickSpeed(i);
        }
    }
}