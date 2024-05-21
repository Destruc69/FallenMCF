package paul.fallen.module.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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

public class HUD extends Module {

	private final Setting watermark;
	private final Setting arrayList;
	private final Setting coords;
	private static final int RADAR_SIZE = 110; // Size of the radar square
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

					// Calculate player's rotation
					float playerYaw = mc.player.rotationYaw;

					// Draw radar texture
					int radarX = screenWidth - 80 - RADAR_SIZE / 2;
					int radarY = 2;
					//UIUtils.drawCustomSizedTexture(RADAR_TEXTURE, radarX, radarY, 0, 0, RADAR_SIZE, RADAR_SIZE, RADAR_SIZE, RADAR_SIZE);

					// Draw player arrow
					int arrowX = radarX + RADAR_SIZE / 2;
					int arrowY = radarY + RADAR_SIZE / 2;

					//mc.textureManager.bindTexture(getEntityFaceSkin(mc.player));
					//UIUtils.drawCustomSizedTexture(getEntityFaceSkin(mc.player), arrowX - 2, arrowY - 2, 0, 0, 10, 10, 10, 10);
					//UIUtils.drawRect(arrowX - 2, arrowY - 2, 6, 6, Color.WHITE.getRGB());

					// Draw other entities on radar
					for (Entity entity : mc.world.getAllEntities()) {
						if (entity != null && !(entity == mc.player)) {
							double relativeX = entity.getPosX() - mc.player.getPosX();
							double relativeZ = entity.getPosZ() - mc.player.getPosZ();
							double angle = MathHelper.atan2(relativeZ, relativeX) - Math.toRadians(playerYaw - 180);
							double distance = Math.sqrt(relativeX * relativeX + relativeZ * relativeZ);

							// Calculate position on radar
							int entityRadarX = (int) (arrowX + distance * Math.cos(angle));
							int entityRadarY = (int) (arrowY + distance * Math.sin(angle));

							//mc.textureManager.bindTexture(getEntityFaceSkin(entity));
							//UIUtils.drawCustomSizedTexture(getEntityFaceSkin(entity), entityRadarX - 2, entityRadarY - 2, 0, 0, 10, 10, 10, 10);
							if (entity instanceof MobEntity) {
								//UIUtils.drawRect(entityRadarX - 2, entityRadarY - 2, 4, 4, Color.RED.getRGB());
								UIUtils.drawCircle(entityRadarX - 2, entityRadarY - 2, 1, Color.RED.getRGB());
							} else if (entity instanceof AnimalEntity) {
								//UIUtils.drawRect(entityRadarX - 2, entityRadarY - 2, 4, 4, Color.GREEN.getRGB());
								UIUtils.drawCircle(entityRadarX - 2, entityRadarY - 2, 1, Color.GREEN.getRGB());
							} else if (entity instanceof WaterMobEntity) {
								//UIUtils.drawRect(entityRadarX - 2, entityRadarY - 2, 4, 4, Color.BLUE.getRGB());
								UIUtils.drawCircle(entityRadarX - 2, entityRadarY - 2, 1, Color.BLUE.getRGB());
							} else if (entity instanceof PlayerEntity) {
								//UIUtils.drawRect(entityRadarX - 2, entityRadarY - 2, 4, 4, Color.ORANGE.getRGB());
								UIUtils.drawCircle(entityRadarX - 2, entityRadarY - 2, 2, Color.WHITE.getRGB());
							} else {
								UIUtils.drawCircle(entityRadarX - 2, entityRadarY - 2, 1, Color.YELLOW.getRGB());
							}
						}
					}

					//UIUtils.drawRect(arrowX - 2, arrowY - 2, 6, 6, Color.WHITE.getRGB());
					UIUtils.drawCircle(arrowX - 2, arrowY - 2, 2, Color.WHITE.getRGB());



					// Draw line with marks indicating rotation yaw
					int marksCount = 8; // Number of marks
					int markLength = 5; // Length of each mark
					int markSpacing = 20; // Spacing between marks
					int lineLength = markSpacing * (marksCount - 1); // Total length of line

					// Draw the line
					int startX = arrowX - lineLength / 2;
					int endX = startX + lineLength;
					int lineY = arrowY + RADAR_SIZE / 2 + 5; // Position below the radar
					UIUtils.drawLine(startX, lineY, endX, lineY, Color.WHITE.getRGB());

					// Draw marks
					for (int i = 0; i < marksCount; i++) {
						int markX = startX + i * markSpacing;
						UIUtils.drawLine(markX, lineY - markLength / 2, markX, lineY + markLength / 2, Color.WHITE.getRGB());
					}

					// Adjust playerYaw if it exceeds 360 or goes below 0
					if (playerYaw > 360) {
						playerYaw %= 360;
					} else if (playerYaw < 0) {
						playerYaw += 360;
					}

					// Calculate the player's yaw position on the line
					int playerMarkX = startX + (int) ((playerYaw % 360) / 360.0 * lineLength);
					if (playerMarkX < startX) {
						playerMarkX = startX + lineLength - (startX - playerMarkX);
					} else if (playerMarkX > endX) {
						playerMarkX = endX - (playerMarkX - endX);
					}

					// Draw the current rotation yaw mark
					UIUtils.drawLine(playerMarkX, lineY - markLength, playerMarkX, lineY + markLength, Color.RED.getRGB());

					// Display the current yaw value
					String yawText = "Yaw: " + String.format("%.2f", playerYaw);
					int textWidth = mc.fontRenderer.getStringWidth(yawText);
					int textX = arrowX - textWidth / 2;
					int textY = lineY + markLength + 5; // Below the line marks
					UIUtils.drawTextOnScreenWithShadow(yawText, textX, textY, new Color(255, 255, 255).getRGB());
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