package paul.fallen.module.modules.client;

import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public class ClickGuiHack extends Module {

    private Setting colorSetting;

    public ClickGuiHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        colorSetting = new Setting("ColorSetting", this, 0);
        //addSetting(colorSetting);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();
            ClientSupport.mc.displayGuiScreen(FALLENClient.INSTANCE.getClickgui());

            onDisable();
            setState(false);
        } catch (Exception ignored) {
        }
    }
}
