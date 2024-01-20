package paul.fallen.module.modules.render;


import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class NoWeather extends Module {

	public NoWeather(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent event) {
		mc.world.setRainStrength(0);
	}

}
