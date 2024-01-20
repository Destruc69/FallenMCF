package paul.fallen.module.modules.player;


import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CEntityActionPacket.Action;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.render.ColorUtils;

public class Sneak extends Module {

	public Sneak(int bind, String name, Category category) {
		super(bind, name, category);
		this.setColor(ColorUtils.generateColor());
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (mc.world != null) {
			mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, Action.RELEASE_SHIFT_KEY));
		}
	}

	@SubscribeEvent
	public void onUpdatePre(TickEvent event) {
		if (mc.world != null) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, Action.RELEASE_SHIFT_KEY));
			} else {
				mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, Action.PRESS_SHIFT_KEY));
			}
		}
	}

}
