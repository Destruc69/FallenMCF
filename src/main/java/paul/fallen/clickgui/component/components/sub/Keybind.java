package paul.fallen.clickgui.component.components.sub;


import paul.fallen.clickgui.component.Component;
import paul.fallen.clickgui.component.components.Button;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;

/**
 *  Made by Pandus1337
 *  it's free to use,
 *  but you have to credit me
 *  @author Pandus1337
 *  <a href="https://github.com/Pandus1337/ClickGUI">...</a>
 */

public class Keybind extends Component {

	private boolean hovered;
	private boolean binding;
	private final Button parent;
	private int offset;
	private int x;
	private int y;
	
	public Keybind(Button button, int offset) {
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void renderComponent() {
		UIUtils.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, (parent.parent.getWidth()), 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
		//Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
		UIUtils.drawRect(parent.parent.getX(), parent.parent.getY() + offset, 2, 12, new Color(0, 0, 0, 191).getRGB());
		//Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(0, 0, 0, 191).getRGB());
		UIUtils.drawTextOnScreen(binding ? "Press a key..." : ("Key: " + this.parent.mod.getBind()), (parent.parent.getX() + 6), (parent.parent.getY() + offset + 0) * 1 + 3, new Color(255, 255, 255, 255).getRGB());
		//Fonts.REGULAR.REGULAR_18.REGULAR_18.drawString(binding ? "Press a key..." : ("Key: " + Keyboard.getKeyName(this.parent.mod.getKey())), (parent.parent.getX() + 6), (parent.parent.getY() + offset + 0) * 1 + 3, new Color(255, 255, 255, 255).getRGB(), true);
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			this.binding = !this.binding;
		}
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		super.keyPressed(keyCode, scanCode, modifiers);
		if(this.binding) {
			this.parent.mod.setBind(keyCode);
			this.binding = false;
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
	}
}