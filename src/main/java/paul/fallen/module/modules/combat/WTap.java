package paul.fallen.module.modules.combat;

import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

public class WTap extends Module {

    private Setting strength;

    public WTap(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        strength = new Setting("Strength", this, 1, 1, 20, true);
        addSetting(strength);
    }

    @SubscribeEvent
    public void onPacketOut(PacketEvent event) {
        try {
            if (event.getPacket() instanceof CUseEntityPacket) {
                CUseEntityPacket cPacketUseEntity = (CUseEntityPacket) event.getPacket();

                if (cPacketUseEntity.getAction().equals(CUseEntityPacket.Action.ATTACK)) {
                    for (int i = 0; i < strength.getValDouble(); i++) {
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}