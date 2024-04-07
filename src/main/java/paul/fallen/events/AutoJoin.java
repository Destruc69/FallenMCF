package paul.fallen.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import paul.fallen.FALLENClient;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.Calendar;

public class AutoJoin {

    private Button enabledButton;
    private Slider hourSlider;

    private boolean active = false;

    @SubscribeEvent
    public void onGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof MultiplayerScreen) {
            enabledButton = new Button(Minecraft.getInstance().getMainWindow().getScaledWidth() - 220, 8, 200, 20, new StringTextComponent("AutoJoin"), button -> {
                active = !active;
            });
            event.addWidget(enabledButton);

            hourSlider = new Slider(Minecraft.getInstance().getMainWindow().getScaledWidth() - 420, 8, 200, 20, new StringTextComponent("Hour "), new StringTextComponent(""), 1, 24, 1, true, true, new Button.IPressable() {
                @Override
                public void onPress(Button p_onPress_1_) {
                }
            });
            event.addWidget(hourSlider);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (enabledButton != null && hourSlider != null && active) {
            if (Minecraft.getInstance().currentScreen != null && Minecraft.getInstance().currentScreen instanceof MultiplayerScreen) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                double hourValue = hourSlider.getValue();
                int parsedHour = (int) Math.round(hourValue); // Round the double value to the nearest integer

                if (hour == parsedHour) {
                    ((MultiplayerScreen) Minecraft.getInstance().currentScreen).connectToSelected();
                }

                int x = 2 + Minecraft.getInstance().fontRenderer.getStringWidth("Joining selected server at hour: " + parsedHour);
                int y = 10;

                // Assuming UIUtils.drawTextOnScreen is defined elsewhere
                UIUtils.drawTextOnScreen("Join selected server at hour: " + parsedHour, x, y, new Color(255, 255, 255).getRGB());
            }
        }
    }
}