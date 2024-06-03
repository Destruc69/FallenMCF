package paul.fallen.module.modules.client;

import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;

public class ClickGuiHack extends Module {

    public ClickGuiHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
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
