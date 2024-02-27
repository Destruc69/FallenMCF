package paul.fallen.module.modules.render;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class HUD extends Module {

	private final Setting watermark;
	private final Setting arrayList;
	private final Setting coords;

	public HUD(int bind, String name, Category category) {
		super(bind, name, category);
		this.setState(true);
		this.setHidden(true);
		watermark = new Setting("Watermark", this, true);
		arrayList = new Setting("ArrayList", this, true);
		coords = new Setting("Coords", this, true);
		FALLENClient.INSTANCE.getSettingManager().addSetting(watermark);
		FALLENClient.INSTANCE.getSettingManager().addSetting(arrayList);
		FALLENClient.INSTANCE.getSettingManager().addSetting(coords);
	}

	static class NameLengthComparator implements Comparator<Module> {
		@Override
		public int compare(Module hack1, Module hack2) {
			return Integer.compare(hack1.getName().length(), hack2.getName().length());
		}
	}

	@SubscribeEvent
	public void onRenderHUD(RenderGameOverlayEvent.Post event) {
		try {
			if (watermark.bval) {
				drawText("Fallen", 2, 2, new Color((int) FALLENClient.INSTANCE.getClickgui().textRGB.x, (int) FALLENClient.INSTANCE.getClickgui().textRGB.y, (int) FALLENClient.INSTANCE.getClickgui().textRGB.z), 2);
			}
			if (arrayList.bval) {
				ArrayList<Module> moduleArrayList = FALLENClient.INSTANCE.getModuleManager().getModulesForArrayList();
				moduleArrayList.sort(new NameLengthComparator().reversed());

				int y = 22;
				for (Module module : moduleArrayList) {
					if (module.getCategory() == Category.Combat) {
						drawText(module.getDisplayName(), 2, y, Color.RED);
					} else if (module.getCategory() == Category.Render) {
						drawText(module.getDisplayName(), 2, y, Color.GREEN);
					} else if (module.getCategory() == Category.Movement) {
						drawText(module.getDisplayName(), 2, y, Color.BLUE);
					} else if (module.getCategory() == Category.Player) {
						drawText(module.getDisplayName(), 2, y, Color.ORANGE);
					} else if (module.getCategory() == Category.World) {
						drawText(module.getDisplayName(), 2, y, Color.YELLOW);
					} else if (module.getCategory() == Category.Pathing) {
						drawText(module.getDisplayName(), 2, y, Color.PINK);
					}
					y += 12;
				}
			}

			if (coords.bval) {
				String coordString = Math.round(mc.player.lastTickPosX) + " " + Math.round(mc.player.lastTickPosY) + " " + Math.round(mc.player.lastTickPosZ);
				drawText(coordString, 25 + mc.fontRenderer.getStringWidth(coordString), 10, Color.WHITE);

				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < coordString.length(); i++) {
					stringBuilder.append("_");
				}
				drawText(stringBuilder.toString(), 25 + mc.fontRenderer.getStringWidth(coordString), 11, Color.WHITE);
				drawText(stringBuilder.toString(), 26 + mc.fontRenderer.getStringWidth(coordString), 11, Color.WHITE);
				drawText(stringBuilder.toString(), 24 + mc.fontRenderer.getStringWidth(coordString), 11, Color.WHITE);
			}
		} catch (Exception ignored) {
		}
	}

	public static void glColor(final int red, final int green, final int blue, final int alpha) {
		GL11.glColor4f(red / 255F, green / 255F, blue / 255F, alpha / 255F);
	}

	public static void glColor(final Color color) {
		glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	private static void glColor(final int hex) {
		glColor(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, hex >> 24 & 0xFF);
	}

	private void drawText(String text, int x, int y, Color color) {
		GL11.glPushMatrix();
		GL11.glScaled(1, 1, 1);
		UIUtils.drawTextOnScreen(text, x, y, color.getRGB());
		GL11.glPopMatrix();
	}

	private void drawText(String text, int x, int y, Color color, int scale) {
		GL11.glPushMatrix();
		GL11.glScaled(scale, scale, 1);
		UIUtils.drawTextOnScreen(text, x, y, color.getRGB());
		GL11.glPopMatrix();
	}
}