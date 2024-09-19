package paul.fallen.module.modules.movement;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class FallFly extends Module {

	public FallFly(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			return;

		mc.player.startFallFlying();
		mc.player.setSwimming(true);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		if(mc.player != null) {
			mc.player.stopFallFlying();
			mc.player.setSwimming(false);
		}
	}
}
