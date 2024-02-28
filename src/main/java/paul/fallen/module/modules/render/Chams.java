package paul.fallen.module.modules.render;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public class Chams extends Module {

    private final Setting colorTest;

    public Chams(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        colorTest = new Setting("ColorSettingTest", "ColorSettingTest", this, 0);
        addSetting(colorTest);
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent event) {
        //GL11.glEnable(32823);
        //GL11.glPolygonOffset(1.0f, -1100000.0f);
    }
}
