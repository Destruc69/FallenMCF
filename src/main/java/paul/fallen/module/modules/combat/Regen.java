package paul.fallen.module.modules.combat;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

import java.util.ArrayList;
import java.util.Arrays;

public class Regen extends Module {

    private final Setting mode;
    private final Setting speed;

    public Regen(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", this, "basic", new ArrayList<>(Arrays.asList("basic", "gwen", "experimental")));
        speed = new Setting("Speed", this, 20, 5, 100, true);
        addSetting(mode);
        addSetting(speed);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (mc.player.getHealth() < mc.player.getMaxHealth() && mc.player.isOnGround()) {
                for (int i = 0; i < speed.getValDouble(); i++) {
                    if (mode.getValString() == "basic") {
                        mc.player.connection.sendPacket(new CPlayerPacket());
                    } else if (mode.getValString() == "gwen") {
                        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, true));
                    } else if (mode.getValString() == "experimental") {
                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), true));
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}