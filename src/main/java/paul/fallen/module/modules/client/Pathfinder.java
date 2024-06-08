package paul.fallen.module.modules.client;

import paul.fallen.module.Module;

public class Pathfinder extends Module {

    public Pathfinder(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        setState(false);
        onDisable();
    }
}
