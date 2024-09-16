package paul.fallen.module.modules.world;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.world.BlockUtils;

public class FastBreak extends Module {

    private final Setting reset;
    private final Setting multiplyBy;
    private final Setting packet;

    public FastBreak(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        reset = new Setting("Reset", this, false);
        addSetting(reset);

        multiplyBy = new Setting("MultiplyBy", this, 1.2, 1.05, 5, false);
        addSetting(multiplyBy);

        packet = new Setting("Packet", this, false);
        addSetting(packet);
    }

    @SubscribeEvent
    public void onTick(PlayerEvent.BreakSpeed event) {
        try {
            if (!packet.getValBoolean()) {
                if (!reset.getValBoolean()) {
                    event.setNewSpeed((float) (event.getOriginalSpeed() * multiplyBy.getValDouble()));
                } else {
                    event.setNewSpeed(event.getOriginalSpeed());
                }
            } else {
                BlockUtils.breakBlockPacketSpam(event.getPos());
                event.setCanceled(true);
            }
        } catch (Exception ignored) {
        }
    }
}
