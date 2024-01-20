package paul.fallen.command.impl.client;

import paul.fallen.command.Command;
import paul.fallen.utils.client.ClientUtils;

public class UnknownCommand extends Command {

    public void runCommand(String[] args) {
        ClientUtils.addChatMessage("Unknown command. Type \"help\" for a list of commands");
    }

}
