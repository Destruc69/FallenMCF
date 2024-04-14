package paul.fallen.clickgui.component.components.sub;

import paul.fallen.clickgui.component.Component;
import paul.fallen.clickgui.component.components.Button;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *  Made by Pandus1337
 *  it's free to use,
 *  but you have to credit me
 *  @author Pandus1337
 *  <a href="https://github.com/Pandus1337/ClickGUI">...</a>
 */

public class ColorSlider extends Component {

	private boolean hovered;
	private Setting set;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	private boolean dragging = false;
	private Color selectedColor;

	private double renderWidth;
	private double renderHeight;

	public ColorSlider(Setting value, Button button, int offset) {
		this.set = value;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;

		// Initial color (default)
		float initialHue = 0.5f; // Default hue
		float initialBrightness = 1.0f; // Default brightness
		this.selectedColor = Color.getHSBColor(initialHue, 1.0f, initialBrightness);
	}

	@Override
	public void renderComponent() {
		int sliderWidth = 88; // Width of the color slider
		int sliderHeight = 12; // Height of the color slider

		// Render color gradient based on hue and selected brightness
		for (int i = 0; i < sliderWidth; i++) {
			float hue = (float) i / sliderWidth;
			Color color = Color.getHSBColor(hue, 1.0f, (float) renderHeight);
			UIUtils.drawRect(parent.parent.getX() + 2 + i, parent.parent.getY() + offset, 1, sliderHeight, color.getRGB());
		}

		// Render selected color indicator
		UIUtils.drawRect(parent.parent.getX() + 2 + (int) renderWidth, parent.parent.getY() + offset, 2, sliderHeight, new Color(0, 0, 0, 191).getRGB());

		// Render text label with RGB values of selected color
		UIUtils.drawTextOnScreen(this.set.getName() + ":" + selectedColor.getRed() + ":" + selectedColor.getGreen() + ":" + selectedColor.getBlue(), (parent.parent.getX() + 6), (parent.parent.getY() + offset) + 3, new Color(255, 255, 255, 255).getRGB());
	}

	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnSlider(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();

		// Calculate horizontal and vertical positions within the slider
		double diffX = Math.min(88, Math.max(0, mouseX - this.x));
		double diffY = Math.min(12, Math.max(0, mouseY - this.y));

		renderWidth = diffX;
		renderHeight = 1.0 - (diffY / 12.0); // Calculate brightness based on vertical position

		if (dragging) {
			// Calculate new hue and brightness based on slider positions
			float hue = (float) (diffX / 88);
			selectedColor = Color.getHSBColor(hue, 1.0f, (float) renderHeight);
			set.setValDouble(selectedColor.getRGB());
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnSlider(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		dragging = false;
	}

	public boolean isMouseOnSlider(int x, int y) {
		int sliderX = this.x + 2;
		int sliderY = this.y;
		int sliderWidth = 88; // Width of the color slider
		int sliderHeight = 12; // Height of the color slider

		return x >= sliderX && x <= sliderX + sliderWidth &&
				y >= sliderY && y <= sliderY + sliderHeight;
	}
}