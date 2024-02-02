package paul.fallen.utils.client;

import paul.fallen.ClientSupport;

public class ClientUtils implements ClientSupport {

	public static void addChatMessage(String s) {
		mc.gui.getChat().addRecentChat("\247d[FALLEN]\2477: \247r" + s);
	}

}
