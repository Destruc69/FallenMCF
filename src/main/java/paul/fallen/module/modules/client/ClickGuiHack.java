package paul.fallen.module.modules.client;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ClickGuiHack extends Module {

    private static Setting primary;

    private static Setting secondary;

    private static Setting text;

    private static Setting prefix;

    public ClickGuiHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        primary = new Setting("Primary", this, new Color(100, 0, 255).getRGB());

        secondary = new Setting("Secondary", this, new Color(45, 0, 255).getRGB());

        text = new Setting("Text", this, new Color(170, 0, 255).getRGB());

        prefix = new Setting("Prefix", "Prefix", this, "dot", new ArrayList<>(Arrays.asList("dot", "minus")));

        addSetting(primary);

        addSetting(secondary);

        addSetting(text);

        // I just put the setting in clickgui settings for now
        addSetting(prefix);

        setState(true);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();
            ClientSupport.mc.displayGuiScreen(FALLENClient.INSTANCE.getClickgui());

            // Set to default
            if (FALLENClient.INSTANCE.getClickgui().primary == FALLENClient.INSTANCE.getClickgui().secondary) {
                //FALLENClient.INSTANCE.getSettingManager().getSettingByName("primary", FALLENClient.INSTANCE.getModuleManager().clickGuiHack).setValDouble(new Color(100, 0, 255).getRGB());
                //FALLENClient.INSTANCE.getSettingManager().getSettingByName("secondary", FALLENClient.INSTANCE.getModuleManager().clickGuiHack).setValDouble(new Color(45, 0, 255).getRGB());

                primary.setValDouble(new Color(100, 0, 255).getRGB());
                secondary.setValDouble(new Color(45, 0, 255).getRGB());
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        try {
            FALLENClient.INSTANCE.getClickgui().primary = (int) primary.dval;
            FALLENClient.INSTANCE.getClickgui().secondary = (int) secondary.dval;
            FALLENClient.INSTANCE.getClickgui().textRGB = (int) text.dval;

            FALLENClient.INSTANCE.getCommandManager().prefix = prefix.sval == "minus" ? "-" : ".";

            if (!(mc.currentScreen == FALLENClient.INSTANCE.getClickgui())) {
                setState(false);
                onDisable();
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        try {
            if (event.getKey() == GLFW.GLFW_KEY_ESCAPE) {
                ClientSupport.mc.currentScreen.closeScreen();
                setState(false);
                onDisable();
            }
        } catch (Exception ignored) {
        }
    }
}
