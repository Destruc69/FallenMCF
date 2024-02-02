package paul.fallen.utils.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import paul.fallen.ClientSupport;

public class UIUtils implements ClientSupport {

    // Method to draw a filled rectangle on the screen
    public static void drawRect(int x, int y, int width, int height, int color) {
        AbstractGui.fill(new MatrixStack(), x, y, x + width, y + height, color);
    }

    public static void drawRect(double x, double y, double width, double height, int color) {
        fill(x, y, x + width, y + height, color);
    }

    // Method to draw text on the screen
    public static void drawTextOnScreen(String text, int x, int y, int color) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.drawString(new MatrixStack(), text, x, y, color);
    }

    private static void fill(double minX, double minY, double maxX, double maxY, int color) {
        if (minX < maxX) {
            double i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            double j = minY;
            minY = maxY;
            maxY = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(minX, maxY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(maxX, maxY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(maxX, minY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(minX, minY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
