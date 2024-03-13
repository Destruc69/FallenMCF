package paul.fallen.clickgui.comp;

import paul.fallen.FALLENClient;
import paul.fallen.clickgui.Clickgui;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;

public class ColorPalette extends Comp {

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
        int radius = 30; // radius of the circular palette
        int centerX = (int) (parent.posX + x) + radius;
        int centerY = (int) (parent.posY + y) + radius;

        for (int i = centerX - radius; i < centerX + radius; i++) {
            for (int j = centerY - radius; j < centerY + radius; j++) {
                double dist = Math.sqrt(Math.pow(i - centerX, 2) + Math.pow(j - centerY, 2));
                if (dist <= radius) {
                    double angle = Math.atan2(j - centerY, i - centerX);
                    float h = (float) Math.toDegrees(angle);
                    if (h < 0) h += 360;
                    float s = 1; // set saturation to max for a fully saturated color wheel
                    float v = 1F;
                    Color c = Color.getHSBColor(h / 360F, s, v);
                    int rgb = c.getRGB();
                    if (isInside(mouseX, mouseY, i, j, i + 1, j + 1)) {
                        UIUtils.drawRect(i, j, 1, 1, new Color(pickedColor.getRed(), pickedColor.getGreen(), pickedColor.getBlue()).getRGB());
                    } else {
                        UIUtils.drawRect(i, j, 1, 1, rgb);
                    }
                }
            }
        }

        UIUtils.drawTextOnScreen(String.valueOf(setting.dval), (int) (parent.posX + x - 55), (int) (parent.posY + y + 1), new Color(FALLENClient.INSTANCE.getClickgui().textRGB).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int radius = 30; // radius of the circular palette
        int centerX = (int) (parent.posX + x) + radius;
        int centerY = (int) (parent.posY + y) + radius;

        if (Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2)) <= radius && mouseButton == 0) {
            double angle = Math.atan2(mouseY - centerY, mouseX - centerX);
            float h = (float) Math.toDegrees(angle);
            if (h < 0) h += 360;
            float s = 1; // set saturation to max for a fully saturated color wheel
            float v = 1F;
            Color c = Color.getHSBColor(h / 360F, s, v);
            setting.setValDouble(new Color(c.getRed(), c.getGreen(), c.getBlue()).getRGB());
        }
    }

    public boolean isInside(int mouseX, int mouseY, int x, int y, int x2, int y2) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }
}
