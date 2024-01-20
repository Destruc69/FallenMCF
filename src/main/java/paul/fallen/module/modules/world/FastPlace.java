package paul.fallen.module.modules.world;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class FastPlace extends Module {

	private int oldRightClickDelayTimer;

	public FastPlace(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		//this.oldRightClickDelayTimer = mc.rightClickDelayTimer;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		//mc.rightClickDelayTimer = this.oldRightClickDelayTimer;
	}

	@SubscribeEvent
	public void onUpdate(TickEvent event) {
		//mc.rightClickDelayTimer = Math.min(mc.rightClickDelayTimer, 2);
	}

}
