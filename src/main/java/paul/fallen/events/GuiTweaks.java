package paul.fallen.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.utils.render.UIUtils;

public class GuiTweaks {

    @SubscribeEvent
    public void onGUI(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof MainMenuScreen) {
            Minecraft mc = Minecraft.getInstance();

            int width = mc.currentScreen.width;
            int height = mc.currentScreen.height;

            // Draw Fallen logo at top right
            ResourceLocation resourceLocation2 = new ResourceLocation("fallen", "fallen-logo.png");
            mc.getTextureManager().bindTexture(resourceLocation2);

            int logoWidth2 = (int) (64 * Minecraft.getInstance().getMainWindow().getGuiScaleFactor());
            int logoHeight2 = (int) (32 * Minecraft.getInstance().getMainWindow().getGuiScaleFactor());

            int logo2X = width - logoWidth2 - 10;
            int logo2Y = 10;

            //Gui.drawModalRectWithCustomSizedTexture(logo2X, logo2Y, 0, 0, logoWidth2, logoHeight2, logoWidth2, logoHeight2);
            UIUtils.drawCustomSizedTexture(resourceLocation2, logo2X, logo2Y, 0, 0, logoWidth2, logoHeight2, logoWidth2, logoHeight2);

            // Draw "Australian Made" text under Fallen logo
            String fText = "Australian Made";
            int fTextWidth = mc.fontRenderer.getStringWidth(fText);
            int fTextX = logo2X + (logoWidth2 - fTextWidth) / 2 + 5;
            int fTextY = logo2Y + logoHeight2 + 5;

            //mc.fontRenderer.drawStringWithShadow(fText, fTextX + 5, fTextY - 15, 0xFFFFFF);
            UIUtils.drawTextOnScreen(fText, fTextX + 5, fTextY - 15, 0xFFFFFF);
        }
    }
}
