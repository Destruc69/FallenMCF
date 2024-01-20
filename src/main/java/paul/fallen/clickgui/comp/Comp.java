package paul.fallen.clickgui.comp;

import com.mojang.blaze3d.matrix.MatrixStack;
import paul.fallen.clickgui.Clickgui;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public class Comp {

    public double x, y, x2, y2;
    public Clickgui parent;
    public Module module;
    public Setting setting;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

}
