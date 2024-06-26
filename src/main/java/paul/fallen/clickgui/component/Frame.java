package paul.fallen.clickgui.component;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.component.components.Button;
import paul.fallen.module.Module;
import paul.fallen.utils.render.UIUtils;

/**
 *  Made by Pandus1337
 *  it's free to use,
 *  but you have to credit me
 *  @author Pandus1337
 *  <a href="https://github.com/Pandus1337/ClickGUI">...</a>
 */

public class Frame {

	public ArrayList<Component> components;
	public Module.Category category;
	private boolean open;
	private int width;
	private int y;
	private int x;
	private int barHeight;
	private boolean isDragging;
	public int dragX;
	public int dragY;
	
	public Frame(Module.Category cat) {
		this.components = new ArrayList<>();
		this.category = cat;
		this.width = 88;
		this.x = 5;
		this.y = 5;
		this.barHeight = 13;
		this.dragX = 0;
		this.open = false;
		this.isDragging = false;
		int tY = this.barHeight;
		
		for(Module mod : FALLENClient.INSTANCE.getModuleManager().getModules()) {
			if (!mod.getCategory().equals(cat))continue;
			Button modButton = new Button(mod, this, tY);
			this.components.add(modButton);
			tY += 12;
		}
	}
	
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public void setDrag(boolean drag) {
		this.isDragging = drag;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}

	public void renderFrame(FontRenderer fontRenderer) {
		UIUtils.drawRect(this.x, this.y , this.width, this.barHeight, new Color(0, 0, 0, 191).getRGB());
		//Gui.drawRect(this.x, this.y , this.x + this.width, this.y + this.barHeight, new Color(0, 0, 0, 191).getRGB());
		UIUtils.drawTextOnScreen(this.category.name(), this.x + 3, (int) ((this.y + 0.0f) * 1 + 4), new Color(255, 255, 255, 255).getRGB());
		//Fonts.ARIAL.ARIAL_18.ARIAL_18.drawCenteredString(this.category.name(), (this.x + 40) + 3, (this.y + 0.0f) * 1 + 4, new Color(255, 255, 255, 255).getRGB(), true);
		if(this.open) {
			if(!this.components.isEmpty()) {
				for(Component component : components) {
					component.renderComponent();
				}
			}
		}
	}
	
	public void refresh() {
		int off = this.barHeight;
		for(Component comp : components) {
			comp.setOff(off);
			off += comp.getHeight();
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void updatePosition(int mouseX, int mouseY) {
		if(this.isDragging) {
			this.setX(mouseX - dragX);
			this.setY(mouseY - dragY);
		}
	}
	
	public boolean isWithinHeader(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
	}
	
}
