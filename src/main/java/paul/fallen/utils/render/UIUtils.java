package paul.fallen.utils.render;

import net.minecraft.client.gui.GuiGraphics;
import paul.fallen.ClientSupport;

public class UIUtils implements ClientSupport {

    // Method to draw a filled rectangle on the screen
    public static void drawRect(int x, int y, int width, int height, int color) {
        GuiGraphics guiGraphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
        guiGraphics.fill(x, y, width, height, color);
    }

    // Method to draw text on the screen
    public static void drawTextOnScreen(String text, int x, int y, int color) {
        GuiGraphics guiGraphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
        guiGraphics.drawString(mc.font, text, x, y, color);
    }
}
