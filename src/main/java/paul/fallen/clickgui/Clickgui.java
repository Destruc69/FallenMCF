package paul.fallen.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.comp.*;
import paul.fallen.command.Command;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.stream.Collectors;

public class Clickgui extends Screen {

    public double posX, posY, width, height, dragX, dragY;
    public boolean dragging;
    public Module.Category selectedCategory;

    private boolean resizingWidth = false;
    private boolean resizingHeight = false;
    private double lastMouseX;
    private double lastMouseY;

    public int primary;
    public int secondary;

    public boolean gradient;
    public int primaryG;
    public int secondaryG;

    private final ArrayList<Ball> balls = new ArrayList<>();

    public int textRGB;
    private final Random random = new Random();
    public boolean lineNetwork;

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

        // Generate initial balls (adjust count and position for desired density)
        for (int i = 0; i < 50; i++) {
            balls.add(new Ball(random.nextInt(Minecraft.getInstance().getMainWindow().getScaledWidth()),
                    random.nextInt(Minecraft.getInstance().getMainWindow().getScaledHeight())));
        }
    }

    public static String convertTicksToHMS(int ticks) {
        long totalMilliseconds = ticks * 50L;
        long hours = totalMilliseconds / 3600000;
        long minutes = (totalMilliseconds % 3600000) / 60000;
        long seconds = ((totalMilliseconds % 3600000) % 60000) / 1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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
            if (isInside(mouseX, mouseY, posX + 65, posY + 1 + offset, posX + 125, posY + 15 + offset)) {
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

                            if (setting.isCombo()) {
                                comps.add(new Combo(columnOffset, yOffset, this, selectedModule, setting));
                                yOffset += 15;
                                count++;
                            }
                            if (setting.isCheck()) {
                                comps.add(new CheckBox(columnOffset, yOffset, this, selectedModule, setting));
                                yOffset += 15;
                                count++;
                            }
                            if (setting.isSlider()) {
                                comps.add(new Slider(columnOffset, yOffset, this, selectedModule, setting));
                                yOffset += 25;
                                count++;
                            }
                            if (setting.isColorPalette()) {
                                comps.add(new ColorPalette(columnOffset, yOffset, this, selectedModule, setting));
                                yOffset += 80;
                                count = count + 3;
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (lineNetwork) {
            renderBackground();
        }
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

        if (!gradient) {
            UIUtils.drawRect(posX, posY - 10, width, 10, new Color(primary).getRGB());
        } else {
            UIUtils.drawGradientRect(posX, posY - 10, width, 10, new Color(primary).getRGB(), new Color(primaryG).getRGB());
        }

        //Gui.drawRect(posX, posY, width, height, new Color(45,45,45).getRGB());

        if (!gradient) {
            UIUtils.drawRect(posX, posY, width, height, new Color(secondary).getRGB());
        } else {
            UIUtils.drawGradientRect(posX, posY, width, height, new Color(secondary).getRGB(), new Color(secondaryG).getRGB());
        }

        if (!gradient) {
            UIUtils.drawRect(posX + width - 2, posY, 2, height, new Color(primary).getRGB());
            UIUtils.drawRect(posX, posY + height - 2, width, 2, new Color(secondary).getRGB());
        } else {
            UIUtils.drawGradientRect(posX + width - 2, posY, 2, height, new Color(primary).getRGB(), new Color(primaryG).getRGB());
            UIUtils.drawGradientRect(posX, posY + height - 2, width, 2, new Color(secondary).getRGB(), new Color(secondaryG).getRGB());
        }

        UIUtils.drawTextOnScreen("Fallen", (int) posX + 2, (int) (posY - 8), Color.CYAN.getRGB());

        if (searchInquiry.length() > 0) {
            UIUtils.drawTextOnScreen(searchInquiry.toString(), mouseX, mouseY - 10, new Color(textRGB).getRGB());
        }

        Calendar calendar = Calendar.getInstance();
        UIUtils.drawTextOnScreen(calendar.getTime().toString(), (int) ((int) posX + width - 160), (int) (posY - 8), new Color(textRGB).getRGB());

        // Format the time as hours:minutes:seconds
        UIUtils.drawTextOnScreen(convertTicksToHMS(Minecraft.getInstance().player.ticksExisted), (int) ((int) posX + width - 280), (int) (posY - 8), new Color(textRGB).getRGB());

        int offset = 0;
        for (Module.Category category : Module.Category.values()) {
            //Gui.drawRect(posX,posY + 1 + offset,posX + 60,posY + 15 + offset,category.equals(selectedCategory) ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
            UIUtils.drawRect(posX, posY + 1 + offset, 60, 15, category.equals(selectedCategory) ? new Color(primary).getRGB() : new Color(primary).darker().getRGB());
            //fontRendererObj.drawString(category.name(),(int)posX + 2, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
            UIUtils.drawTextOnScreen(category.name(), (int) posX + 2, (int) (posY + 5) + offset, new Color(textRGB).getRGB());
            offset += 15;
        }
        offset = 0;
        ArrayList<Module> searchedModules = FALLENClient.INSTANCE.getModuleManager().getModulesInCategory(selectedCategory).stream()
                .filter(module -> module.getName().toLowerCase().contains(searchInquiry.toString().toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (searchInquiry.length() > 0) {
            for (Module m : searchedModules) {
                //Gui.drawRect(posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset,m.isToggled() ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
                UIUtils.drawRect(posX + 65, posY + 1 + offset, 125, 15, m.toggled ? new Color(primary).getRGB() : new Color(primary).darker().getRGB());
                //fontRendererObj.drawString(m.getName(),(int)posX + 67, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
                UIUtils.drawTextOnScreen(m.getName(), (int) posX + 67, (int) (posY + 5) + offset, new Color(textRGB).getRGB());
                offset += 15;
            }
        } else {
            for (Module m : FALLENClient.INSTANCE.getModuleManager().getModulesInCategory(selectedCategory)) {
                //Gui.drawRect(posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset,m.isToggled() ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
                UIUtils.drawRect(posX + 65, posY + 1 + offset, 125, 15, m.toggled ? new Color(primary).getRGB() : new Color(primary).darker().getRGB());
                //fontRendererObj.drawString(m.getName(),(int)posX + 67, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
                UIUtils.drawTextOnScreen(m.getName(), (int) posX + 67, (int) (posY + 5) + offset, new Color(textRGB).getRGB());
                offset += 15;
            }
        }

        for (Comp comp : comps) {
            comp.drawScreen(mouseX, mouseY);
        }
    }

    private void renderBackground() {
        // Draw lines and balls
        for (Ball ball1 : balls) {
            ball1.tick(); // Update ball position
            for (Ball ball2 : balls) {
                if (ball1 != ball2 && isConnected(ball1, ball2)) {
                    drawLine(ball1.getX(), ball1.getY(), ball2.getX(), ball2.getY());
                }
            }
            drawBall(ball1);
        }
    }

    private boolean isConnected(Ball ball1, Ball ball2) {
        // Define connection criteria (e.g., distance threshold)
        double distance = Math.sqrt(Math.pow(ball2.getX() - ball1.getX(), 2) + Math.pow(ball2.getY() - ball1.getY(), 2));
        return distance < 100; // Adjust threshold value for desired connection density
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        UIUtils.drawLine(x1, y1, x2, y2, Color.WHITE.getRGB());
    }

    private void drawBall(Ball ball) {
        UIUtils.drawCircle(ball.x, ball.y, (int) ball.getSize(), Color.WHITE.getRGB());
    }

    public class Ball {

        private final double size;
        private int x;
        private int y;
        private int speedX;
        private int speedY;

        public Ball(int x, int y) {
            this.x = x;
            this.y = y;
            this.speedX = random.nextInt(4) - 1; // Random number between -2 and 2
            this.speedY = random.nextInt(4) - 1; // Random number between -2 and 2
            size = Math.round(Math.random() * 4);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public double getSize() {
            return size;
        }

        public void tick() {
            // Update ball position
            x += speedX;
            y += speedY;

            // Change direction when hitting screen edges
            if (x <= 0 || x >= Minecraft.getInstance().getMainWindow().getScaledWidth()) {
                speedX = -speedX;
            }
            if (y <= 0 || y >= Minecraft.getInstance().getMainWindow().getScaledHeight()) {
                speedY = -speedY;
            }
        }
    }
}