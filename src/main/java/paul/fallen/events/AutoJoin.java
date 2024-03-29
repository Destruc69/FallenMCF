package paul.fallen.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.FALLENClient;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.Calendar;

public class AutoJoin {

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (FALLENClient.INSTANCE.getModuleManager().autoJoin.getState()) {
            if (Minecraft.getInstance().currentScreen instanceof MultiplayerScreen) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                double hourValue = FALLENClient.INSTANCE.getModuleManager().autoJoin.hour.dval;
                int parsedHour = (int) Math.round(hourValue); // Round the double value to the nearest integer

                if (Math.round(hour) == Math.round(parsedHour)) {
                    ((MultiplayerScreen) Minecraft.getInstance().currentScreen).connectToSelected();
                }

                int x = Minecraft.getInstance().getMainWindow().getScaledWidth() + 2 + Minecraft.getInstance().fontRenderer.getStringWidth("Join selected server at hour: " + parsedHour);
                int y = 10;

                UIUtils.drawTextOnScreen("Join selected server at hour: " + parsedHour, x, y, new Color(FALLENClient.INSTANCE.getClickgui().textRGB).getRGB());
            }
        }
    }
}