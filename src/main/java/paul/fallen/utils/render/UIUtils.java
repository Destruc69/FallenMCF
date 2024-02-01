package paul.fallen.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import javafx.scene.shape.Mesh;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
