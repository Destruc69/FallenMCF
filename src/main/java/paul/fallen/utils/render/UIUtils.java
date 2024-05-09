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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector4f;
import paul.fallen.ClientSupport;

public class UIUtils implements ClientSupport {

    // Method to draw text on the screen
    public static void drawTextOnScreen(String text, int x, int y, int color) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.drawString(new MatrixStack(), text, x, y, color);
    }

    // Method to draw text on the screen with shadow
    public static void drawTextOnScreenWithShadow(String text, int x, int y, int color) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.drawStringWithShadow(new MatrixStack(), text, x, y, color);
    }

    // Method to draw a line between two points using rectangles
    public static void drawLine(int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (x1 != x2 || y1 != y2) {
            drawRect(x1, y1, 1, 1, color);
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    public static void drawCircle(int centerX, int centerY, int radius, int color) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) <= radius * radius) {
                    drawRect(x, y, 1, 1, color);
                }
            }
        }
    }

    public static void drawEllipse(int centerX, int centerY, int ellipseWidth, int ellipseHeight, int color) {
        int radiusX = ellipseWidth / 2;
        int radiusY = ellipseHeight / 2;

        for (int x = centerX - radiusX; x <= centerX + radiusX; x++) {
            for (int y = centerY - radiusY; y <= centerY + radiusY; y++) {
                if (isPointInEllipse(x, y, centerX, centerY, radiusX, radiusY)) {
                    drawRect(x, y, 1, 1, color);
                }
            }
        }
    }

    private static boolean isPointInEllipse(int x, int y, int centerX, int centerY, int radiusX, int radiusY) {
        // Formula to check if (x, y) is inside or on the boundary of the ellipse
        double normalizedX = (double) (x - centerX) / radiusX;
        double normalizedY = (double) (y - centerY) / radiusY;

        return (normalizedX * normalizedX + normalizedY * normalizedY) <= 1;
    }

    // Method to draw a filled rectangle on the screen
    public static void drawRect(int x, int y, int width, int height, int color) {
        AbstractGui.fill(new MatrixStack(), x, y, x + width, y + height, color);
    }

    public static void drawRect(double x, double y, double width, double height, int color) {
        fill(x, y, x + width, y + height, color);
    }


    // Method to draw a filled gradient rectangle on the screen
    //public static void drawGradientRect(double x, double y, double width, double height, int color1, int color2) {
    //    fillGradient(x, y, x + width, y + height, color1, color2);
    //}

    public static void drawGradientRect(double x, double y, double width, double height, int startColor, int endColor) {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        // Interpolate between startColor and endColor diagonally
        for (double yOffset = 0; yOffset < height; yOffset++) {
            float ratioY = (float) yOffset / (float) height;
            for (double xOffset = 0; xOffset < width; xOffset++) {
                float ratioX = (float) xOffset / (float) width;
                float ratio = (ratioX + ratioY) / 2.0f;
                int color = interpolateColor(startColor, endColor, ratio);
                float alpha = (float) (color >> 24 & 255) / 255.0F;
                float red = (float) (color >> 16 & 255) / 255.0F;
                float green = (float) (color >> 8 & 255) / 255.0F;
                float blue = (float) (color & 255) / 255.0F;

                bufferBuilder.pos(x + xOffset, y + yOffset + 1, 0.0F).color(red, green, blue, alpha).endVertex();
                bufferBuilder.pos(x + xOffset + 1, y + yOffset + 1, 0.0F).color(red, green, blue, alpha).endVertex();
                bufferBuilder.pos(x + xOffset + 1, y + yOffset, 0.0F).color(red, green, blue, alpha).endVertex();
                bufferBuilder.pos(x + xOffset, y + yOffset, 0.0F).color(red, green, blue, alpha).endVertex();
            }
        }

        bufferBuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferBuilder);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private static int interpolateColor(int startColor, int endColor, float ratio) {
        Vector4f startVec = unpackColor(startColor);
        Vector4f endVec = unpackColor(endColor);

        float red = lerp(startVec.getX(), endVec.getX(), ratio);
        float green = lerp(startVec.getY(), endVec.getY(), ratio);
        float blue = lerp(startVec.getZ(), endVec.getZ(), ratio);
        float alpha = lerp(startVec.getW(), endVec.getW(), ratio);

        return packColor(red, green, blue, alpha);
    }

    private static Vector4f unpackColor(int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        return new Vector4f(red, green, blue, alpha);
    }

    private static int packColor(float red, float green, float blue, float alpha) {
        int r = (int) (red * 255);
        int g = (int) (green * 255);
        int b = (int) (blue * 255);
        int a = (int) (alpha * 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static float lerp(float a, float b, float ratio) {
        return a + ratio * (b - a);
    }

    private static void fillGradient(double minX, double minY, double maxX, double maxY, int color1, int color2) {
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

        float f3 = (float) (color1 >> 24 & 255) / 255.0F;
        float f = (float) (color1 >> 16 & 255) / 255.0F;
        float f1 = (float) (color1 >> 8 & 255) / 255.0F;
        float f2 = (float) (color1 & 255) / 255.0F;

        float f4 = (float) (color2 >> 24 & 255) / 255.0F;
        float f5 = (float) (color2 >> 16 & 255) / 255.0F;
        float f6 = (float) (color2 >> 8 & 255) / 255.0F;
        float f7 = (float) (color2 & 255) / 255.0F;

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        // Draw vertices with interpolated colors
        for (double y = minY; y < maxY; y++) {
            float lerpFactor = (float) ((y - minY) / (maxY - minY));
            float r = f * (1 - lerpFactor) + f5 * lerpFactor;
            float g = f1 * (1 - lerpFactor) + f6 * lerpFactor;
            float b = f2 * (1 - lerpFactor) + f7 * lerpFactor;
            float a = f3 * (1 - lerpFactor) + f4 * lerpFactor;

            bufferbuilder.pos(minX, y, 0).color(r, g, b, a).endVertex();
            bufferbuilder.pos(maxX, y, 0).color(r, g, b, a).endVertex();
            bufferbuilder.pos(maxX, y + 1, 0).color(r, g, b, a).endVertex();
            bufferbuilder.pos(minX, y + 1, 0).color(r, g, b, a).endVertex();
        }

        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
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

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(minX, maxY, 0.0F).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(maxX, maxY, 0.0F).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(maxX, minY, 0.0F).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(minX, minY, 0.0F).color(red, green, blue, alpha).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawCustomSizedTexture(ResourceLocation resourceLocation, int x, int y, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight) {
        Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
        AbstractGui.blit(new MatrixStack(), x, y, textureX, textureY, width, height, textureWidth, textureHeight);
    }
}
