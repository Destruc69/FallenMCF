package paul.fallen.module.modules.world;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public class FastBreak extends Module {

    private final Setting multiplyBy;

    public FastBreak(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        multiplyBy = new Setting("MultiplyBy", this, 1.2, 1.1, 5, false);
        addSetting(multiplyBy);
    }

    @SubscribeEvent
    public void onTick(PlayerEvent.BreakSpeed event) {
        try {
            event.setNewSpeed((float) (event.getOriginalSpeed() * multiplyBy.getValDouble()));
        } catch (Exception ignored) {
        }
    }
}
