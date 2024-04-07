package paul.fallen.clickgui;

import java.io.IOException;
import java.util.ArrayList;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;
import paul.fallen.clickgui.component.Component;
import paul.fallen.clickgui.component.Frame;
import paul.fallen.module.Module;

/**
 *  Made by Pandus1337
 *  it's free to use,
 *  but you have to credit me
 *  @author Pandus1337
 *  <a href="https://github.com/Pandus1337/ClickGUI">...</a>
 */

public class ClickGui extends Screen {

	public static ArrayList<Frame> frames;
	public static int color = -1;
	
	public ClickGui() {
		super(new StringTextComponent("clickgui"));
		frames = new ArrayList<>();
		int frameX = 5;
		for(Module.Category category : Module.Category.values()) {
			Frame frame = new Frame(category);
			frame.setX(frameX);
			frames.add(frame);
			frameX += frame.getWidth() + 1;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		for(Frame frame : frames) {
			frame.renderFrame(Minecraft.getInstance().fontRenderer);
			for(Component comp : frame.getComponents()) {
				comp.updateComponent(mouseX, mouseY);
			}
			frame.updatePosition(mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);

		for (Frame frame : frames) {
			if (isInside(mouseX, mouseY, frame.getX(), frame.getY() - 10, frame.getX() + frame.getWidth(), frame.getY())) {
				frame.setDrag(true);
				frame.dragX = (int) (mouseX - frame.getX());
				frame.dragY = (int) (mouseY - frame.getY());
			}
			if (frame.isWithinHeader((int) mouseX, (int) mouseY) && button == 0) {
				frame.setDrag(true);
				frame.dragX = (int) (mouseX - frame.getX());
				frame.dragY = (int) (mouseY - frame.getY());
			}
			if (frame.isWithinHeader((int)mouseX, (int)mouseY) && button == 1) {
				frame.setOpen(!frame.isOpen());
			}
			if (frame.isOpen()) {
				if (!frame.getComponents().isEmpty()) {
					for (Component component : frame.getComponents()) {
						component.mouseClicked((int)mouseX, (int)mouseY, button);
					}
				}
			}
		}
		return false;
	}


	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		super.keyPressed(keyCode, scanCode, modifiers);
		for(Frame frame : frames) {
			if(frame.isOpen() && keyCode != 1) {
				if(!frame.getComponents().isEmpty()) {
					for(Component component : frame.getComponents()) {
						component.keyPressed(keyCode, scanCode, modifiers);
					}
				}
			}
		}
		if (keyCode == 1) {
            Minecraft.getInstance().displayGuiScreen(null);
        }
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		for(Frame frame : frames) {
			frame.setDrag(false);
		}
		for(Frame frame : frames) {
			if(frame.isOpen()) {
				if(!frame.getComponents().isEmpty()) {
					for(Component component : frame.getComponents()) {
						component.mouseReleased(mouseX, mouseY, button);
					}
				}
			}
		}
		return false;
	}

	public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
		return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
	}

	public boolean isInside(double mouseX, double mouseY, double x, double y, double x2, double y2) {
		return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
	}
}
