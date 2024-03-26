package paul.fallen.module.modules.player;

import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public class Timer extends Module {

    public Setting timer;
    public Setting bypass;

    public Timer(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        timer = new Setting("Timer", "Timer", this, 20, 1, 100);
        bypass = new Setting("Bypass", this, false);
        addSetting(timer);
        addSetting(bypass);
    }

    // See MixinMinecraft
}