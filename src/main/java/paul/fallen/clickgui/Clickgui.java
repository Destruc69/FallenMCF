package paul.fallen.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.comp.CheckBox;
import paul.fallen.clickgui.comp.Combo;
import paul.fallen.clickgui.comp.Comp;
import paul.fallen.clickgui.comp.Slider;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;

public class Clickgui extends Screen {

    public double posX, posY, width, height, dragX, dragY;
    public boolean dragging;
    public Module.Category selectedCategory;

    private boolean resizingWidth = false;
    private boolean resizingHeight = false;
    private double lastMouseX;
    private double lastMouseY;

    public Vector3d fragmentA;
    public Vector3d fragmentB;
    public Vector3d fragmentC;
    public Vector3d fragmentD;

    public Vector3d textRGB;

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
    }

    public static String getNearestEvent(int currentMonth, int currentDay) {
        LocalDate currentDate = LocalDate.of(LocalDate.now().getYear(), currentMonth, currentDay);

        LocalDate terryDavisBirth = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 15);
        LocalDate terryDavisDeath = LocalDate.of(LocalDate.now().getYear(), Month.AUGUST, 11);
        LocalDate fallenEstablished = LocalDate.of(LocalDate.now().getYear(), Month.OCTOBER, 8);
        LocalDate paulBirthday = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 5);
        LocalDate rickMondyBirthday = LocalDate.of(LocalDate.now().getYear(), Month.OCTOBER, 12);
        LocalDate funnyBirthday = LocalDate.of(LocalDate.now().getYear(), Month.MAY, 16);
        LocalDate zamplexBirthday = LocalDate.of(LocalDate.now().getYear(), Month.JULY, 11);
        LocalDate newYearsDay = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1);
        LocalDate christmasDay = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 25);
        LocalDate internationalMensDay = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 19);
        LocalDate australiaDay = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 26);
        LocalDate alexandersBirthday = LocalDate.of(LocalDate.now().getYear(), Month.FEBRUARY, 9);

        LocalDate[] eventDates = {terryDavisBirth, terryDavisDeath, fallenEstablished, paulBirthday, rickMondyBirthday,
                funnyBirthday, zamplexBirthday, newYearsDay, christmasDay, internationalMensDay, australiaDay, alexandersBirthday};

        LocalDate nearestEventDate = null;
        long nearestEventDays = Long.MAX_VALUE;

        for (LocalDate eventDate : eventDates) {
            if (eventDate.isAfter(currentDate) && eventDate.toEpochDay() - currentDate.toEpochDay() < nearestEventDays) {
                nearestEventDate = eventDate;
                nearestEventDays = eventDate.toEpochDay() - currentDate.toEpochDay();
            }
        }

        if (nearestEventDate != null) {
            if (nearestEventDate.equals(terryDavisBirth)) {
                return "Terry Davis's birth" + " | " + terryDavisBirth;
            } else if (nearestEventDate.equals(terryDavisDeath)) {
                return "Terry Davis's death." + " | " + terryDavisDeath;
            } else if (nearestEventDate.equals(fallenEstablished)) {
                return "Fallen established" + " | " + fallenEstablished;
            } else if (nearestEventDate.equals(paulBirthday)) {
                return "Paul's birth." + " | " + paulBirthday;
            } else if (nearestEventDate.equals(rickMondyBirthday)) {
                return "RickMondy's birth." + " | " + rickMondyBirthday;
            } else if (nearestEventDate.equals(funnyBirthday)) {
                return "Funny's birth." + " | " + funnyBirthday;
            } else if (nearestEventDate.equals(zamplexBirthday)) {
                return "Zamplex's birth." + " | " + zamplexBirthday;
            } else if (nearestEventDate.equals(newYearsDay)) {
                return "New years" + " | " + newYearsDay;
            } else if (nearestEventDate.equals(christmasDay)) {
                return "Christmas" + " | " + christmasDay;
            } else if (nearestEventDate.equals(internationalMensDay)) {
                return "International Men's Day" + " | " + internationalMensDay;
            } else if (nearestEventDate.equals(australiaDay)) {
                return "Australia Day" + " | " + australiaDay;
            } else if (nearestEventDate.equals(alexandersBirthday)) {
                return "Alexanders Birthday" + " | " + alexandersBirthday;
            }
        }

        return "Upcoming events: ";
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        for (Comp comp : comps) {
            comp.keyPressed(keyCode, scanCode, modifiers);
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
                                yOffset += 35;
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
        UIUtils.drawRect(posX, posY - 10, width, 10, new Color((int) fragmentA.x, (int) fragmentA.y, (int) fragmentA.z).getRGB());
        //Gui.drawRect(posX, posY, width, height, new Color(45,45,45).getRGB());
        UIUtils.drawRect(posX, posY, width, height, new Color((int) fragmentB.x, (int) fragmentB.y, (int) fragmentB.z).getRGB());

        UIUtils.drawRect(posX + width - 2, posY, 2, height, new Color((int) fragmentA.x, (int) fragmentA.y, (int) fragmentA.z).getRGB());
        UIUtils.drawRect(posX, posY + height - 2, width, 2, new Color((int) fragmentA.x, (int) fragmentA.y, (int) fragmentA.z).getRGB());

        UIUtils.drawTextOnScreen("Fallen", (int) posX + 2, (int) (posY - 8), Color.CYAN.getRGB());

        Calendar calendar = Calendar.getInstance();
        UIUtils.drawTextOnScreen(calendar.getTime().toString(), (int) ((int) posX + width - 160), (int) (posY - 8), new Color((int) textRGB.x, (int) textRGB.y, (int) textRGB.z).getRGB());

        int offset = 0;
        for (Module.Category category : Module.Category.values()) {
            //Gui.drawRect(posX,posY + 1 + offset,posX + 60,posY + 15 + offset,category.equals(selectedCategory) ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
            UIUtils.drawRect(posX, posY + 1 + offset, 60, 15, category.equals(selectedCategory) ? new Color((int) fragmentC.x, (int) fragmentC.y, (int) fragmentC.z).getRGB() : new Color((int) fragmentD.x, (int) fragmentD.y, (int) fragmentD.z).getRGB());
            //fontRendererObj.drawString(category.name(),(int)posX + 2, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
            UIUtils.drawTextOnScreen(category.name(), (int) posX + 2, (int) (posY + 5) + offset, new Color((int) textRGB.x, (int) textRGB.y, (int) textRGB.z).getRGB());
            offset += 15;
        }
        offset = 0;
        for (Module m : FALLENClient.INSTANCE.getModuleManager().getModulesInCategory(selectedCategory)) {
            //Gui.drawRect(posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset,m.isToggled() ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
            UIUtils.drawRect(posX + 65, posY + 1 + offset, 125, 15, m.toggled ? new Color((int) fragmentC.x, (int) fragmentC.y, (int) fragmentC.z).getRGB() : new Color((int) fragmentD.x, (int) fragmentD.y, (int) fragmentD.z).getRGB());
            //fontRendererObj.drawString(m.getName(),(int)posX + 67, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
            UIUtils.drawTextOnScreen(m.getName(), (int) posX + 67, (int) (posY + 5) + offset, new Color((int) textRGB.x, (int) textRGB.y, (int) textRGB.z).getRGB());
            offset += 15;
        }

        for (Comp comp : comps) {
            comp.drawScreen(mouseX, mouseY);
        }
    }

    private String whatsTheBuzzTellMeWhatsAHappening() {
        // Max: 80 chars
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so adding 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int screenWidth = Minecraft.getInstance().getMainWindow().getScaledWidth();
        int screenHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();
        int bottomHalfY = screenHeight / 2 + screenHeight / 4;

        int x = screenWidth / 2 - (500 / 2);
        int y = bottomHalfY - (25 / 2);

        if (month == 12 && day == 15) {
            return "Today, we celebrate Terry Davis's birth!.";
        } else if (month == 8 && day == 11) {
            return "Today, we honor Terry Davis. May his soul rest in peace.";
        } else if (month == 10 && day == 8) {
            return "On this day in 2021, Fallen was established!.";
        } else if (month == 12 && day == 5) {
            return "[Paul - Founder] I was born today!";
        } else if (month == 10 && day == 12) {
            return "[RickMondy - Trusted Confidant] I was born today!";
        } else if (month == 5 && day == 16) {
            return "[Funny - Resilient Patron] I was born today!";
        } else if (month == 7 && day == 11) {
            return "[Zamplex - Early Adopter] I was born today!";
        } else if (month == 1 && day == 1) {
            return "Happy new years!";
        } else if (month == 12 && day == 25) {
            return "Merry Christmas!";
        } else if (month == 11 && day == 19) {
            return "Its International Men's Day!";
        } else if (month == 1 && day == 26) {
            return "Happy Australia Day! Fallen is Australian Made.";
        } else if (month == 2 && day == 9) {
            return "Happy Birthday Alexander! Thank you for wurst.";
        } else {
            return "Upcoming events: " + getNearestEvent(month, day);
        }
    }
}