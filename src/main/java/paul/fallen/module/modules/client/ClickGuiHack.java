package paul.fallen.module.modules.client;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class ClickGuiHack extends Module {

    private static Setting primaryRed;
    private static Setting primaryGreen;
    private static Setting primaryBlue;

    private static Setting secondaryRed;
    private static Setting secondaryGreen;
    private static Setting secondaryBlue;

    private static Setting textRed;
    private static Setting textGreen;
    private static Setting textBlue;

    private static Setting prefix;

    public ClickGuiHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        primaryRed = new Setting("PrimaryRed", this, 100, 0, 255);
        primaryGreen = new Setting("PrimaryGreen", this, 10, 0, 255);
        primaryBlue = new Setting("PrimaryBlue", this, 100, 0, 255);

        secondaryRed = new Setting("SecondaryRed", this, 45, 0, 255);
        secondaryGreen = new Setting("SecondaryGreen", this, 45, 0, 255);
        secondaryBlue = new Setting("SecondaryBlue", this, 45, 0, 255);

        textRed = new Setting("TextRed", this, 170, 0, 255);
        textGreen = new Setting("TextGreen", this, 170, 0, 255);
        textBlue = new Setting("TextBlue", this, 170, 0, 255);

        prefix = new Setting("Prefix", "Prefix", this, "dot", new ArrayList<>(Arrays.asList("dot", "minus")));

        addSetting(primaryRed);
        addSetting(primaryGreen);
        addSetting(primaryBlue);

        addSetting(secondaryRed);
        addSetting(secondaryGreen);
        addSetting(secondaryBlue);

        addSetting(textRed);
        addSetting(textGreen);
        addSetting(textBlue);

        // I just put the setting in clickgui settings for now
        addSetting(prefix);

        setState(true);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();
            ClientSupport.mc.displayGuiScreen(FALLENClient.INSTANCE.getClickgui());
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
            FALLENClient.INSTANCE.getClickgui().primary = new Vector3d(primaryRed.dval, primaryGreen.dval, primaryBlue.dval);
            FALLENClient.INSTANCE.getClickgui().secondary = new Vector3d(secondaryRed.dval, secondaryGreen.dval, secondaryBlue.dval);
            FALLENClient.INSTANCE.getClickgui().textRGB = new Vector3d(textRed.dval, textGreen.dval, textBlue.dval);

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
