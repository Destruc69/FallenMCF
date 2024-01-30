package paul.fallen.module.modules;

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

    private static Setting fragmentARed;
    private static Setting fragmentAGreen;
    private static Setting fragmentABlue;

    private static Setting fragmentBRed;
    private static Setting fragmentBGreen;
    private static Setting fragmentBBlue;

    private static Setting fragmentCRed;
    private static Setting fragmentCGreen;
    private static Setting fragmentCBlue;

    private static Setting fragmentDRed;
    private static Setting fragmentDGreen;
    private static Setting fragmentDBlue;

    private static Setting textRed;
    private static Setting textGreen;
    private static Setting textBlue;

    private static Setting prefix;

    public ClickGuiHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        fragmentARed = new Setting("FragmentARed", this, 100, 0, 255);
        fragmentAGreen = new Setting("FragmentAGreen", this, 10, 0, 255);
        fragmentABlue = new Setting("FragmentABlue", this, 100, 0, 255);

        fragmentBRed = new Setting("FragmentBRed", this, 45, 0, 255);
        fragmentBGreen = new Setting("FragmentBGreen", this, 45, 0, 255);
        fragmentBBlue = new Setting("FragmentBBlue", this, 45, 0, 255);

        fragmentCRed = new Setting("FragmentCRed", this, 230, 0, 255);
        fragmentCGreen = new Setting("FragmentCGreen", this, 10, 0, 255);
        fragmentCBlue = new Setting("FragmentCBlue", this, 230, 0, 255);

        fragmentDRed = new Setting("FragmentDRed", this, 28, 0, 255);
        fragmentDGreen = new Setting("FragmentDGreen", this, 28, 0, 255);
        fragmentDBlue = new Setting("FragmentDBlue", this, 28, 0, 255);

        textRed = new Setting("TextRed", this, 170, 0, 255);
        textGreen = new Setting("TextGreen", this, 170, 0, 255);
        textBlue = new Setting("TextBlue", this, 170, 0, 255);

        prefix = new Setting("Prefix", "Prefix", this, "dot", new ArrayList<>(Arrays.asList("dot", "minus")));

        addSetting(fragmentARed);
        addSetting(fragmentAGreen);
        addSetting(fragmentABlue);

        addSetting(fragmentBRed);
        addSetting(fragmentBGreen);
        addSetting(fragmentBBlue);

        addSetting(fragmentCRed);
        addSetting(fragmentCGreen);
        addSetting(fragmentCBlue);

        addSetting(fragmentDRed);
        addSetting(fragmentDGreen);
        addSetting(fragmentDBlue);

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
            FALLENClient.INSTANCE.getClickgui().fragmentA = new Vector3d(fragmentARed.dval, fragmentAGreen.dval, fragmentABlue.dval);
            FALLENClient.INSTANCE.getClickgui().fragmentB = new Vector3d(fragmentBRed.dval, fragmentBGreen.dval, fragmentBBlue.dval);
            FALLENClient.INSTANCE.getClickgui().fragmentC = new Vector3d(fragmentCRed.dval, fragmentCGreen.dval, fragmentCBlue.dval);
            FALLENClient.INSTANCE.getClickgui().fragmentD = new Vector3d(fragmentDRed.dval, fragmentDGreen.dval, fragmentDBlue.dval);
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
