package paul.fallen.module.modules.render;

import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class HeadRoll extends Module {

    private final Setting mode;
    int i = 0;

    public HeadRoll(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", this, "roll", new ArrayList<>(Arrays.asList("roll", "upsidedown")));
        addSetting(mode);
    }

    @SubscribeEvent
    public void onCamera(EntityViewRenderEvent.CameraSetup event) {
        i++;
        if (mode.sval == "roll") {
            event.setRoll(event.getRoll() + i);
        } else {
            event.setRoll(180);
        }
    }
}
