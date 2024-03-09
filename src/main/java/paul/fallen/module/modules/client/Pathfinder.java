package paul.fallen.module.modules.client;

import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class Pathfinder extends Module {
    public Setting type;

    public Pathfinder(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        type = new Setting("Type", this, "ground", new ArrayList<>(Arrays.asList("ground", "air")));
        addSetting(type);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        setState(false);
        onDisable();
    }
}
