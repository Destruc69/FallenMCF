package paul.fallen.module.modules.client;

import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;

public class ClickGuiHack extends Module {

    public ClickGuiHack(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
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
