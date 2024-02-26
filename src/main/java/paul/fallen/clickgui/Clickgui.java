package paul.fallen.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.comp.CheckBox;
import paul.fallen.clickgui.comp.Combo;
import paul.fallen.clickgui.comp.Comp;
import paul.fallen.clickgui.comp.Slider;
import paul.fallen.command.Command;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

public class Clickgui extends Screen {

    public double posX, posY, width, height, dragX, dragY;
    public boolean dragging;
    public Module.Category selectedCategory;

    private boolean resizingWidth = false;
    private boolean resizingHeight = false;
    private double lastMouseX;
    private double lastMouseY;

    public Vector3d primary;
    public Vector3d secondary;

    public Vector3d textRGB;

    public StringBuilder searchInquiry;

    private Module selectedModule;
    public int modeIndex;

    public ArrayList<Comp> comps = new ArrayList<>();

    public Clickgui() {
        super(new StringTextComponent("clickgui"));
        dragging = false;
        int scaledWidth = Minecraft.getInstance().getMainWindow().getScaledWidth();
        int scaledHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();
        posX = scaledWidth / 2 - 150;
        posY = scaledHeight / 2 - 100;
        width = posX + 150 * 2 * 2;
        height = height + 200 * 2;
        selectedCategory = Module.Category.Combat;
        searchInquiry = new StringBuilder();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        for (Comp comp : comps) {
            comp.keyPressed(keyCode, scanCode, modifiers);
        }

        String cha = InputMappings.getInputByCode(keyCode, scanCode).func_237520_d_().getString();
        if (cha.length() <= 1) {
            searchInquiry.append(cha);
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (searchInquiry.length() - 1 >= 0) {
                searchInquiry.deleteCharAt(searchInquiry.length() - 1);
            }
        }

        if (searchInquiry.length() > 0) {
            if (searchInquiry.toString().startsWith(FALLENClient.INSTANCE.getCommandManager().prefix)) {
                if (keyCode == GLFW.GLFW_KEY_ENTER) {
                    String message = searchInquiry.substring(1);
                    String[] cmd = message.split(" ");
                    Command command = FALLENClient.INSTANCE.getCommandManager().getCommandFromMessage(message);
                    command.runCommand(cmd);
                    searchInquiry = new StringBuilder();
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (isInside(mouseX, mouseY, posX, posY - 10, width, posY) && button == 0) {
            dragging = true;
            dragX = mouseX - posX;
            dragY = mouseY - posY;
        }

        if (isInside(mouseX, mouseY, posX + width - 2, posY, posX + width, posY + height) && button == 0) {
            resizingWidth = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        } else if (isInside(mouseX, mouseY, posX, posY + height - 2, posX + width, posY + height) && button == 0) {
            resizingHeight = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        // Check if the mouse click is on the right border of the ClickGUI
        else if (isInside(mouseX, mouseY, posX + width - 2, posY, posX + width, posY + height) && button == 0) {
            scaleWidth(mouseX);
        }
        // Check if the mouse click is on the bottom border of the ClickGUI
        else if (isInside(mouseX, mouseY, posX, posY + height - 2, posX + width, posY + height) && button == 0) {
            scaleHeight(mouseY);
        }

        int offset = 0;
        for (Module.Category category : Module.Category.values()) {
            if (isInside(mouseX, mouseY, posX, posY + 1 + offset, posX + 60, posY + 15 + offset) && button == 0) {
                selectedCategory = category;
            }
            offset += 15;
        }
        offset = 0;
        for (Module m : FALLENClient.INSTANCE.getModuleManager().getModulesInCategory(selectedCategory)) {
            if (isInside(mouseX, mouseY,posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset)) {
                if (button == 0) {
                    m.toggle();
                }
                if (button == 1) {
                    comps.clear();
                    int column = 0;
                    int count = 0;
                    int maxSettingsPerColumn = 10;
                    int columnOffsetBase = 275;
                    int columnOffset = columnOffsetBase;
                    int yOffset = 3;

                    if (FALLENClient.INSTANCE.getSettingManager().getSettingsByMod(m) != null) {
                        for (Setting setting : FALLENClient.INSTANCE.getSettingManager().getSettingsByMod(m)) {
                            selectedModule = m;

                            if (count >= maxSettingsPerColumn) {
                                count = 0;
                                column++;
                                columnOffset = columnOffsetBase + column * 150; // Adjust the column spacing as needed
                                yOffset = 3;
                            }

                            count++;

                            if (setting.isCombo()) {
                                comps.add(new Combo(columnOffset, yOffset, this, selectedModule, setting));
                                yOffset += 15;
                            }
                            if (setting.isCheck()) {
                                comps.add(new CheckBox(columnOffset, yOffset, this, selectedModule, setting));
                                yOffset += 15;
                            }
                            if (setting.isSlider()) {
                                comps.add(new Slider(columnOffset, yOffset, this, selectedModule, setting));
                                yOffset += 25;
                            }
                        }
                    }
                }
            }
            offset += 15;
        }
        for (Comp comp : comps) {
            comp.mouseClicked((int) mouseX, (int) mouseY, button);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        dragging = false;
        for (Comp comp : comps) {
            comp.mouseReleased((int) mouseX, (int) mouseY, button);
        }

        if (resizingWidth || resizingHeight) {
            resizingWidth = false;
            resizingHeight = false;
            return true;
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (resizingWidth) {
            scaleWidth(mouseX - lastMouseX);
        } else if (resizingHeight) {
            scaleHeight(mouseY - lastMouseY);
        } else {
            super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        return true;
    }

    @Override
    public void init() {
        super.init();
        dragging = false;

        for (Comp comp : comps) {
            comp.init();
        }
    }

    public void scaleWidth(double diffX) {
        double newWidth = width + diffX;
        if (newWidth > 50) { // Ensure the width doesn't become too narrow
            width = newWidth;
        }
    }

    public void scaleHeight(double diffY) {
        double newHeight = height + diffY;
        if (newHeight > 50) { // Ensure the height doesn't become too short
            height = newHeight;
        }
    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }

    public boolean isInside(double mouseX, double mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (dragging) {
            posX = mouseX - dragX;
            posY = mouseY - dragY;
        }

        // 100 10 100 = A
        // 45 45 45 = B
        // 230 10 230 = C
        // 28 28 28 = D
        // 1701 170 170 = Text RGB

        //Gui.drawRect(posX, posY - 10, width, posY, new Color(100,10,100).getRGB());
        UIUtils.drawRect(posX, posY - 10, width, 10, new Color((int) primary.x, (int) primary.y, (int) primary.z).getRGB());
        //Gui.drawRect(posX, posY, width, height, new Color(45,45,45).getRGB());
        UIUtils.drawRect(posX, posY, width, height, new Color((int) secondary.x, (int) secondary.y, (int) secondary.z).getRGB());

        UIUtils.drawRect(posX + width - 2, posY, 2, height, new Color((int) primary.x, (int) primary.y, (int) primary.z).getRGB());
        UIUtils.drawRect(posX, posY + height - 2, width, 2, new Color((int) primary.x, (int) primary.y, (int) primary.z).getRGB());

        UIUtils.drawTextOnScreen("Fallen", (int) posX + 2, (int) (posY - 8), Color.CYAN.getRGB());

        if (searchInquiry.length() > 0) {
            UIUtils.drawTextOnScreen(searchInquiry.toString(), mouseX, mouseY - 10, new Color((int) textRGB.x, (int) textRGB.y, (int) textRGB.z).getRGB());
        }

        Calendar calendar = Calendar.getInstance();
        UIUtils.drawTextOnScreen(calendar.getTime().toString(), (int) ((int) posX + width - 160), (int) (posY - 8), new Color((int) textRGB.x, (int) textRGB.y, (int) textRGB.z).getRGB());

        int offset = 0;
        for (Module.Category category : Module.Category.values()) {
            //Gui.drawRect(posX,posY + 1 + offset,posX + 60,posY + 15 + offset,category.equals(selectedCategory) ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
            UIUtils.drawRect(posX, posY + 1 + offset, 60, 15, category.equals(selectedCategory) ? new Color((int) primary.x, (int) primary.y, (int) primary.z).getRGB() : new Color((int) ((int) primary.x > 20 ? primary.x - 20 : primary.x + 20), (int) ((int) primary.y > 20 ? primary.y - 20 : primary.y + 20), (int) ((int) primary.z > 20 ? primary.z - 20 : primary.z + 20)).getRGB());
            //fontRendererObj.drawString(category.name(),(int)posX + 2, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
            UIUtils.drawTextOnScreen(category.name(), (int) posX + 2, (int) (posY + 5) + offset, new Color((int) textRGB.x, (int) textRGB.y, (int) textRGB.z).getRGB());
            offset += 15;
        }
        offset = 0;
        ArrayList<Module> searchedModules = FALLENClient.INSTANCE.getModuleManager().getModulesInCategory(selectedCategory).stream()
                .filter(module -> module.getName().toLowerCase().contains(searchInquiry.toString().toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (searchInquiry.length() > 0) {
            for (Module m : searchedModules) {
                //Gui.drawRect(posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset,m.isToggled() ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
                UIUtils.drawRect(posX + 65, posY + 1 + offset, 125, 15, m.toggled ? new Color((int) primary.x, (int) primary.y, (int) primary.z).getRGB() : new Color((int) ((int) primary.x > 20 ? primary.x - 20 : primary.x + 20), (int) ((int) primary.y > 20 ? primary.y - 20 : primary.y + 20), (int) ((int) primary.z > 20 ? primary.z - 20 : primary.z + 20)).getRGB());
                //fontRendererObj.drawString(m.getName(),(int)posX + 67, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
                UIUtils.drawTextOnScreen(m.getName(), (int) posX + 67, (int) (posY + 5) + offset, new Color((int) textRGB.x, (int) textRGB.y, (int) textRGB.z).getRGB());
                offset += 15;
            }
        } else {
            for (Module m : FALLENClient.INSTANCE.getModuleManager().getModulesInCategory(selectedCategory)) {
                //Gui.drawRect(posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset,m.isToggled() ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
                UIUtils.drawRect(posX + 65, posY + 1 + offset, 125, 15, m.toggled ? new Color((int) primary.x, (int) primary.y, (int) primary.z).getRGB() : new Color((int) ((int) primary.x > 20 ? primary.x - 20 : primary.x + 20), (int) ((int) primary.y > 20 ? primary.y - 20 : primary.y + 20), (int) ((int) primary.z > 20 ? primary.z - 20 : primary.z + 20)).getRGB());
                //fontRendererObj.drawString(m.getName(),(int)posX + 67, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
                UIUtils.drawTextOnScreen(m.getName(), (int) posX + 67, (int) (posY + 5) + offset, new Color((int) textRGB.x, (int) textRGB.y, (int) textRGB.z).getRGB());
                offset += 15;
            }
        }

        for (Comp comp : comps) {
            comp.drawScreen(mouseX, mouseY);
        }
    }
}