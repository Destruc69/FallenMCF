package paul.fallen.module.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class HUD extends Module {

	private final Setting watermark;
	private final Setting arrayList;
	private final Setting coords;
	private static final int RADAR_SIZE = 110;
	private final Setting radar;

	public HUD(int bind, String name, Category category) {
		super(bind, name, category);
		this.setState(true);
		this.setHidden(true);
		watermark = new Setting("Watermark", this, true);
		arrayList = new Setting("ArrayList", this, true);
		coords = new Setting("Coords", this, true);
		radar = new Setting("Radar", this, false);

		FALLENClient.INSTANCE.getSettingManager().addSetting(watermark);
		FALLENClient.INSTANCE.getSettingManager().addSetting(arrayList);
		FALLENClient.INSTANCE.getSettingManager().addSetting(coords);
		FALLENClient.INSTANCE.getSettingManager().addSetting(radar);
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
			if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
				if (watermark.getValBoolean()) {
					drawText("Fallen", 2, 2, new Color(255, 255, 255), 2);
				}

				if (arrayList.getValBoolean()) {
					ArrayList<Module> moduleArrayList = FALLENClient.INSTANCE.getModuleManager().getModulesForArrayList();
					moduleArrayList.sort(new NameLengthComparator().reversed());

					moduleArrayList.removeIf(Module::isHidden);

					int y = 22;
					for (Module module : moduleArrayList) {
						UIUtils.drawTextOnScreenWithShadow(module.getDisplayName(), 2, y, new Color(255, 255, 255).getRGB());
						y += 12;
					}
				}

				if (coords.getValBoolean()) {
					int screenWidth = mc.getMainWindow().getScaledWidth();
					int screenHeight = mc.getMainWindow().getScaledHeight();
					if (mc.world.getDimensionKey() == World.OVERWORLD || mc.world.getDimensionKey() == World.THE_END) {
						double netherX = Math.round(mc.player.getPosX() / 8);
						double netherZ = Math.round(mc.player.getPosZ() / 8);
						drawText(Math.round(mc.player.getPosX()) + " " + Math.round(mc.player.getPosY()) + " " + Math.round(mc.player.getPosZ()) + " [" + netherX + "] " + " [" + netherZ + "]", screenWidth - 5 - mc.fontRenderer.getStringWidth(Math.round(mc.player.getPosX()) + " " + Math.round(mc.player.getPosY()) + " " + Math.round(mc.player.getPosZ()) + " [" + netherX + "] " + " [" + netherZ + "]"), screenHeight - 10, new Color(255, 255, 255));
					} else if (mc.world.getDimensionKey() == World.THE_NETHER) {
						double overworldX = Math.round(mc.player.getPosX() * 8);
						double overworldZ = Math.round(mc.player.getPosZ() * 8);
						drawText(Math.round(mc.player.getPosX()) + " " + Math.round(mc.player.getPosY()) + " " + Math.round(mc.player.getPosZ()) + " [" + overworldX + "] " + " [" + overworldZ + "]", screenWidth - 5 - mc.fontRenderer.getStringWidth(Math.round(mc.player.getPosX()) + " " + Math.round(mc.player.getPosY()) + " " + Math.round(mc.player.getPosZ()) + " [" + overworldX + "] " + " [" + overworldZ + "]"), screenHeight - 10, new Color(255, 255, 255));
					}
				}

				if (radar.getValBoolean()) {
					Minecraft mc = Minecraft.getInstance();
					int screenWidth = mc.getMainWindow().getScaledWidth();

					float playerYaw = mc.player.rotationYaw;

					int radarX = screenWidth - 80 - RADAR_SIZE / 2;
					int radarY = 2;

					int arrowX = radarX + RADAR_SIZE / 2;
					int arrowY = radarY + RADAR_SIZE / 2;

					StreamSupport.stream(Spliterators.spliteratorUnknownSize(mc.world.getAllEntities().iterator(), 0), false)
							.filter(entity -> entity != null && entity != mc.player)
							.forEach(entity -> {
								double relativeX = entity.getPosX() - mc.player.getPosX();
								double relativeZ = entity.getPosZ() - mc.player.getPosZ();
								double angle = MathHelper.atan2(relativeZ, relativeX) - Math.toRadians(playerYaw - 180);
								double distance = Math.sqrt(relativeX * relativeX + relativeZ * relativeZ);

								int entityRadarX = (int) (arrowX + distance * Math.cos(angle));
								int entityRadarY = (int) (arrowY + distance * Math.sin(angle));

								int color = Color.YELLOW.getRGB();
								if (entity instanceof MobEntity) {
									color = Color.RED.getRGB();
								} else if (entity instanceof AnimalEntity) {
									color = Color.GREEN.getRGB();
								} else if (entity instanceof WaterMobEntity) {
									color = Color.BLUE.getRGB();
								} else if (entity instanceof PlayerEntity) {
									color = Color.WHITE.getRGB();
								}

								int radius = (entity instanceof PlayerEntity) ? 2 : 1;
								UIUtils.drawCircle(entityRadarX - 2, entityRadarY - 2, radius, color);
							});
				}
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
		//UIUtils.drawTextOnScreen(text, x, y, color.getRGB());
		UIUtils.drawTextOnScreenWithShadow(text, x, y, color.getRGB());
		GL11.glPopMatrix();
	}

	private void drawText(String text, int x, int y, Color color, int scale) {
		GL11.glPushMatrix();
		GL11.glScaled(scale, scale, 1);
		//UIUtils.drawTextOnScreen(text, x, y, color.getRGB());
		UIUtils.drawTextOnScreenWithShadow(text, x, y, color.getRGB());
		GL11.glPopMatrix();
	}
}