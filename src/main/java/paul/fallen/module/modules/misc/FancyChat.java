package paul.fallen.module.modules.misc;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class FancyChat extends Module {

	public FancyChat(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
	}

	@SubscribeEvent
	public void onChatMessage(ClientChatEvent event) {
		if (!event.getMessage().startsWith("$") && !event.getMessage().startsWith("/"))
			event.setMessage(event.getMessage() + "[FALLEN]");
	}

}
