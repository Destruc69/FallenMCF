package paul.fallen.module.modules.client;

import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ClickGuiHack extends Module {

    public Setting primary;
    public Setting primaryG;

    public Setting gradient;

    public Setting secondary;
    public Setting secondaryG;

    public Setting text;

    public Setting networkLines;

    public Setting prefix;

    public ClickGuiHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        primary = new Setting("Primary", this, new Color(100, 0, 255).getRGB());

        secondary = new Setting("Secondary", this, new Color(45, 0, 255).getRGB());

        primaryG = new Setting("PrimaryG", this, new Color(0, 0, 0).getRGB());

        secondaryG = new Setting("SecondaryG", this, new Color(0, 0, 0).getRGB());

        text = new Setting("Text", this, new Color(170, 0, 255).getRGB());

        networkLines = new Setting("NetworkLines", this, false);

        prefix = new Setting("Prefix", "Prefix", this, "dot", new ArrayList<>(Arrays.asList("dot", "minus")));

        addSetting(primary);

        addSetting(secondary);

        gradient = new Setting("Gradient", this, false);

        addSetting(gradient);

        addSetting(primaryG);

        addSetting(secondaryG);

        addSetting(text);

        addSetting(networkLines);

        addSetting(prefix);
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
