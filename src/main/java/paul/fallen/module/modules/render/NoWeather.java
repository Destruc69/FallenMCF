package paul.fallen.module.modules.render;


import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class NoWeather extends Module {

	public NoWeather(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
		this.setState(true);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.PlayerTickEvent event) {
		try {
			mc.world.setRainStrength(0);
		} catch (Exception ignored) {
		}
	}
}
