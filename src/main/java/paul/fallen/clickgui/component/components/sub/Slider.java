package paul.fallen.clickgui.component.components.sub;

import paul.fallen.clickgui.component.Component;
import paul.fallen.clickgui.component.components.Button;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *  Made by Pandus1337
 *  it's free to use,
 *  but you have to credit me
 *  @author Pandus1337
 *  <a href="https://github.com/Pandus1337/ClickGUI">...</a>
 */

public class Slider extends Component {

	private boolean hovered;

	private final Setting set;
	private final Button parent;
	private int offset;
	private int x;
	private int y;
	private boolean dragging = false;

	private double renderWidth;

	public Slider(Setting value, Button button, int offset) {
		this.set = value;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	@Override
	public void renderComponent() {
		UIUtils.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getWidth(), 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
		//Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());
		UIUtils.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, (int) renderWidth, 12, new Color(150, 150, 150, 128).getRGB());
		//Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12, new Color(150, 150, 150, 128).getRGB());
		UIUtils.drawRect(parent.parent.getX(), parent.parent.getY() + offset, 2, 12, new Color(0, 0, 0, 191).getRGB());
		//Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(0, 0, 0, 191).getRGB());
		// Format the slider value to display up to 3 decimal places
		DecimalFormat decimalFormat = new DecimalFormat();
		String formattedValue = decimalFormat.format(this.set.getValDouble());

		// Draw the formatted value
		UIUtils.drawTextOnScreen(this.set.getName() + ": " + formattedValue, (parent.parent.getX() + 6), (parent.parent.getY() + offset) + 3, new Color(255, 255, 255, 255).getRGB());
		//Fonts.REGULAR.REGULAR_18.REGULAR_18.drawString(EnumChatFormatting.WHITE + this.set.getName() + ": " + EnumChatFormatting.RESET + this.set.getValDouble() , (parent.parent.getX() + 6), (parent.parent.getY() + offset) + 3, new Color(255, 255, 255, 255).getRGB(), true);
	}

	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();

		double diff = Math.min(88, Math.max(0, mouseX - this.x));

		double min = set.getMin();
		double max = set.getMax();

		renderWidth = (88) * (set.getValDouble() - min) / (max - min);

		if (dragging) {
			if (diff == 0) {
				set.setValDouble(set.getMin());
			}
			else {
				double newValue = roundToPlace(((diff / 88) * (max - min) + min));
				set.setValDouble((float) newValue);
			}
		}
	}

	private static double roundToPlace(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
		if(isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		dragging = false;
	}

	public boolean isMouseOnButtonD(int x, int y) {
		return x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12;
	}

	public boolean isMouseOnButtonI(int x, int y) {
		return x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12;
	}
}