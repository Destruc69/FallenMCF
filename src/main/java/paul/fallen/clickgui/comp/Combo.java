package paul.fallen.clickgui.comp;

import com.mojang.blaze3d.matrix.MatrixStack;
import paul.fallen.clickgui.Clickgui;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;

public class Combo extends Comp {

    public Combo(double x, double y, Clickgui parent, Module module, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.module = module;
        this.setting = setting;
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInside(mouseX, mouseY, parent.posX + x - 70, parent.posY + y, parent.posX + x, parent.posY + y + 10) && mouseButton == 0) {
            int max = setting.getOptions().size();
            if (parent.modeIndex + 1 >= max) {
                parent.modeIndex = 0;
            } else {
                parent.modeIndex++;
            }
            setting.setValString(setting.getOptions().get(parent.modeIndex));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        //Gui.drawRect(parent.posX + x - 70, parent.posY + y, parent.posX + x, parent.posY + y + 10,setting.getValBoolean() ? new Color(230,10,230).getRGB() : new Color(30,30,30).getRGB());

        // Remove background
        //UIUtils.drawRect(parent.posX + x - 70, parent.posY + y, 25, 35,setting.getValBoolean() ? new Color(230,10,230).getRGB() : new Color(30,30,30).getRGB());

        //Minecraft.getMinecraft().fontRendererObj.drawString(setting.getName() + ": " + setting.getValString(), (int)(parent.posX + x - 69), (int)(parent.posY + y + 1), new Color(200,200,200).getRGB());
        UIUtils.drawTextOnScreen(setting.getName() + ": " + setting.getValString(), (int) (parent.posX + x - 69), (int) (parent.posY + y + 1), new Color(200, 200, 200).getRGB());
    }
}
