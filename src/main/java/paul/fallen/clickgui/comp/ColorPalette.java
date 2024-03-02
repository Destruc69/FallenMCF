package paul.fallen.clickgui.comp;

import paul.fallen.FALLENClient;
import paul.fallen.clickgui.Clickgui;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;

public class ColorPalette extends Comp {

    private final double x;
    private final double y;
    private final Clickgui parent;
    private final Module module;
    private final Setting setting;

    public ColorPalette(double x, double y, Clickgui parent, Module module, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        Color pickedColor = new Color((int) setting.dval);
        int pickerSize = 10; // smaller palette size
        for (int i = 0; i < 180; i++) { // reduced loop range for smaller palette
            for (int j = 0; j < 50; j++) { // reduced loop range for smaller palette
                float h = (float) (i * 2) / 360F; // adjust hue range
                float s = j / 50F; // adjust saturation range
                float v = 1F;
                Color c = Color.getHSBColor(h, s, v);
                int rgb = c.getRGB();
                if (isInside(mouseX, mouseY, (int) (parent.posX + x) + i, (int) (parent.posY + y) + j, (int) (parent.posX + x) + i + pickerSize, (int) (parent.posY + y) + j + pickerSize)) {
                    UIUtils.drawRect((int) (parent.posX + x) + i, (int) (parent.posY + y) + j, pickerSize, pickerSize, new Color(pickedColor.getRed(), pickedColor.getGreen(), pickedColor.getBlue()).getRGB());
                } else {
                    UIUtils.drawRect((int) (parent.posX + x) + i, (int) (parent.posY + y) + j, pickerSize, pickerSize, rgb);
                }
            }
        }

        UIUtils.drawTextOnScreen(String.valueOf(setting.dval), (int) (parent.posX + x - 55), (int) (parent.posY + y + 1), new Color(FALLENClient.INSTANCE.getClickgui().textRGB).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInside(mouseX, mouseY, (int) (parent.posX + x), (int) (parent.posY + y), (int) (parent.posX + x) + 180, (int) (parent.posY + y) + 50) && mouseButton == 0) {
            float h = (float) ((mouseX - (parent.posX + x)) * 2 / 360F); // adjust hue range
            float s = (float) ((mouseY - (parent.posY + y)) / 50F); // adjust saturation range
            float v = 1F;
            Color c = Color.getHSBColor(h, s, v);
            setting.setValDouble(new Color(c.getRed(), c.getGreen(), c.getBlue()).getRGB());
        }
    }

    public boolean isInside(int mouseX, int mouseY, int x, int y, int x2, int y2) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }
}