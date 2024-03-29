package paul.fallen.module.modules.client;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public class AutoJoin extends Module {

    public Setting hour;

    public AutoJoin(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        hour = new Setting("Hour", this, 1, 1, 24);
        addSetting(hour);
    }
}
