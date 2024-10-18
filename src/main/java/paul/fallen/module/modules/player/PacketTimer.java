package paul.fallen.module.modules.player;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

public class PacketTimer extends Module {

    private final Setting amount;

    public PacketTimer(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        amount = new Setting("Amount", this, 2, 1, 20, true);
        addSetting(amount);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        try {
            if (event.getPacket() instanceof CPlayerPacket) {
                for (int i = 0; i < amount.getValDouble(); i++) {
                    mc.player.connection.sendPacket(event.getPacket());
                }
            }
        } catch (Exception ignored) {
        }
    }
}
