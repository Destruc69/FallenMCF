package paul.fallen.module.modules.render;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.ColorUtils;
import paul.fallen.utils.render.UIUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HUD extends Module {

	private final Setting watermark;
	private final Setting watermarkMode;
	private final Setting arrayList;
	private final Setting radar;
	private final Setting coords;
	private final Setting direction;
	private final Setting fps;
	private final Setting colorMode;

	public HUD(int bind, String name, Category category) {
		super(bind, name, category);
		this.setState(true);
		this.setHidden(true);
		watermark = new Setting("Watermark", this, true);

		ArrayList<String> watermarkModes = new ArrayList<String>();
		watermarkModes.add("Text");
		watermarkModes.add("Logo");

		watermarkMode = new Setting("WatermarkMode", "Watermark Mode", this, "Text", watermarkModes);

		ArrayList<String> colorModes = new ArrayList<String>();
		colorModes.add("Random");
		colorModes.add("Category");
		colorModes.add("Rainbow");
		this.colorMode = new Setting("ColorMode", "Color Mode", this, "Random", colorModes);

		arrayList = new Setting("ArrayList", this, true);
		radar = new Setting("Radar", this, true);
		coords = new Setting("Coords", this, true);
		direction = new Setting("Direction", this, true);
		fps = new Setting("FPS", this, true);
		FALLENClient.INSTANCE.getSettingManager().addSetting(watermark);
		FALLENClient.INSTANCE.getSettingManager().addSetting(watermarkMode);
		FALLENClient.INSTANCE.getSettingManager().addSetting(arrayList);
		FALLENClient.INSTANCE.getSettingManager().addSetting(colorMode);
		FALLENClient.INSTANCE.getSettingManager().addSetting(radar);
		FALLENClient.INSTANCE.getSettingManager().addSetting(coords);
		FALLENClient.INSTANCE.getSettingManager().addSetting(direction);
		FALLENClient.INSTANCE.getSettingManager().addSetting(fps);
	}

	@SubscribeEvent
	public void onRenderHUD(RenderWorldLastEvent event) {
		int topRightY = 2;
		int topLeftY = 2;
		int bottomLeftY = mc.getMainWindow().getWindowY() - 10;
		int width = mc.getMainWindow().getScaledWidth();
		int height = mc.getMainWindow().getScaledHeight();
		if (this.watermark.getValBoolean()) {
			if (this.watermarkMode.getValString().equalsIgnoreCase("Text")) {
				UIUtils.drawTextOnScreen("Fallen", 2, 2, 0xfff48fb1);
				topLeftY += 10;
			} else if (this.watermarkMode.getValString().equalsIgnoreCase("Logo")) {
				mc.getTextureManager().bindTexture(new ResourceLocation("assets/forgewurst/fallen-logo.png"));
				AbstractGui.blit(event.getMatrixStack(), 2, 2, 0, 0, 75, 75, 75, 75);
				topLeftY += 78;
			}
		}
		if (this.arrayList.getValBoolean()) {
			for (Module m : FALLENClient.INSTANCE.getModuleManager().getModulesForArrayList()) {
				if (!m.isHidden() && m.getState()) {
					String name = m.getDisplayName();
					if (m.getSuffix().length() != 0) {
						name += " \2477" + m.getSuffix();
					}
					switch (this.colorMode.getValString()) {
						case "Random":
							UIUtils.drawTextOnScreen(name, width - mc.fontRenderer.getStringWidth(name) - 2, topRightY, m.getColor());
							//event.getFontRenderer().drawStringWithShadow(event.getMatrixStack(), name, event.getWidth() - event.getFontRenderer().getStringWidth(name) - 2, topRightY, m.getColor());
							break;
						case "Category":
							UIUtils.drawTextOnScreen(name, width - mc.fontRenderer.getStringWidth(name) - 2, topRightY, m.getCategoryColor());
							//event.getFontRenderer().drawStringWithShadow(event.getMatrixStack(), name, event.getWidth() - event.getFontRenderer().getStringWidth(name) - 2, topRightY, m.getCategoryColor());
							break;
						case "Rainbow":
							UIUtils.drawTextOnScreen(name, width - mc.fontRenderer.getStringWidth(name) - 2, topRightY, ColorUtils.setRainbow(50000000L * topRightY, 1.0F).getRGB());
							//event.getFontRenderer().drawStringWithShadow(event.getMatrixStack(), name, event.getWidth() - event.getFontRenderer().getStringWidth(name) - 2, topRightY, ColorUtils.setRainbow(50000000L * topRightY, 1.0F).getRGB());
							break;
					}
					topRightY += 10;
				}
			}
		}
		if (this.radar.getValBoolean()) {
			List<Entity> players = new ArrayList();
			for (Object o : mc.world.getAllEntities()) {
				Entity e = (Entity) o;
				if (e instanceof PlayerEntity && e != mc.player)
					players.add(e);
			}
			players.sort((Comparator) new Comparator<PlayerEntity>() {
				@Override
				public int compare(PlayerEntity m1, PlayerEntity m2) {
					String s1 = (FALLENClient.INSTANCE.getFriendManager().isFriend(m1.getName().getString()) ? FALLENClient.INSTANCE.getFriendManager().getAliasName(m1.getName().getString()) : m1.getName().getString()) + " " + MathHelper.floor(m1.getDistance(mc.player));
					String s2 = (FALLENClient.INSTANCE.getFriendManager().isFriend(m2.getName().getString()) ? FALLENClient.INSTANCE.getFriendManager().getAliasName(m2.getName().getString()) : m2.getName().getString()) + " " + MathHelper.floor(m2.getDistance(mc.player));
					return mc.fontRenderer.getStringWidth(s2) - mc.fontRenderer.getStringWidth(s1);
				}
			});
			for (Entity e : players) {
				String prefix = "";
				PlayerEntity pe = (PlayerEntity) e;
				if (pe.getName().getString().equalsIgnoreCase("Freecam")) return;
				if (FALLENClient.INSTANCE.getFriendManager().isFriend(pe.getName().getString())) {
					prefix = "\2479";
				} else {
					prefix = pe.getDistance(mc.player) <= 64 ? "\247c" : "\247a";
				}
				if (FALLENClient.INSTANCE.getFriendManager().isFriend(e.getName().getString())) {
					mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(), prefix + FALLENClient.INSTANCE.getFriendManager().getAliasName(pe.getName().getString()) + " \2477" + MathHelper.floor(pe.getDistance(mc.player)), 2, topLeftY, 0xffffffff);
				} else {
					mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(), prefix + pe.getName().getString() + " \2477" + MathHelper.floor(pe.getDistance(mc.player)), 2, topLeftY, 0xffffffff);
				}
				topLeftY += 10;
			}
		}
		if (this.coords.getValBoolean()) {
			mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(), "XYZ: \2477" + Math.floor(mc.player.getPosX()) + ", " + Math.floor(mc.player.getPosY()) + ", " + Math.floor(mc.player.getPosZ()), 2, bottomLeftY, 0xffffffff);
			bottomLeftY -= 10;
		}
		if (this.direction.getValBoolean()) {
			mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(), "Direction: \2477" + mc.player.getHorizontalFacing().getString().substring(0, 1).toUpperCase() + mc.player.getHorizontalFacing().getString().substring(1), 2, bottomLeftY, 0xffffffff);
			bottomLeftY -= 10;
		}
		if (this.fps.getValBoolean()) {
			//mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(),"FPS: \2477" + Minecraft.getInstance().de, 2, bottomLeftY, 0xffffffff);
			bottomLeftY -= 10;
		}
	}

}

