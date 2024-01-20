package paul.fallen.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class ScreenWalk extends Module {

	public ScreenWalk(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
		this.setState(true);
	}

	@SubscribeEvent
	public void onInput(InputUpdateEvent event) {
		KeyBinding[] moveKeys = {mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft,
				mc.gameSettings.keyBindBack, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindJump,
				mc.gameSettings.keyBindSprint};

		if ((mc.currentScreen instanceof Screen)
				&& !(mc.currentScreen instanceof ChatScreen) && !(mc.currentScreen instanceof EditSignScreen)) {
			KeyBinding[] array;
			int length = (array = moveKeys).length;
			for (int i = 0; i < length; i++) {
				KeyBinding key = array[i];
				key.setPressed(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), key.getKey().getKeyCode()));
			}
		}
	}

}
