package paul.fallen.utils.client;

import net.minecraft.util.text.StringTextComponent;
import paul.fallen.ClientSupport;

public class ClientUtils implements ClientSupport {

	public static void addChatMessage(String s) {
		mc.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent("\247d[FALLEN]\2477: \247r" + s));
	}

}
