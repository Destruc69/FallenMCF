package paul.fallen.module.modules;

import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;

public class ClickGuiHack extends Module {

    public ClickGuiHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ClientSupport.mc.displayGuiScreen(FALLENClient.INSTANCE.getClickgui());
        setState(false);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
