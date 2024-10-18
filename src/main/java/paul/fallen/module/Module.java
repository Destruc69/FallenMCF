package paul.fallen.module;

import net.minecraftforge.common.MinecraftForge;
import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.utils.render.ColorUtils;

public class Module implements ClientSupport {

    public boolean toggled;
    private int bind, color = 0xffffffff, categoryColor = 0xffffffff;
    private String name = "", displayName = "", description = "";
    private boolean hidden = false;
    private Category category;

    public Module(int bind, String name, Category category, String description) {
        this(bind, name, name, category, description);
    }

    public Module(int bind, String name, String displayName, Category category, String description) {
        this.bind = bind;
        this.name = name;
        this.displayName = displayName;
        this.color = ColorUtils.generateColor();
        this.category = category;
        this.description = description;
        switch (this.category) {
            case Combat:
                this.categoryColor = 0xffe53935;
                break;
            case Player:
                this.categoryColor = 0xff64b5f6;
                break;
            case Movement:
                this.categoryColor = 0xffffb74d;
                break;
            case World:
                this.categoryColor = 0xff81c784;
                break;
            case Render:
                this.categoryColor = 0xffb39ddb;
                break;
        }
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(int categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean getState() {
        return this.toggled;
    }

    public void setState(boolean state) {
        this.toggled = state;
        onToggle();
        if (state) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setState(!getState());
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public void onToggle() {
    }

    public void addSetting(Setting setting) {
        FALLENClient.INSTANCE.getSettingManager().addSetting(setting);
    }

    public enum Category {
        Combat, Player, Movement, World, Render, Client, Pathing
    }

}