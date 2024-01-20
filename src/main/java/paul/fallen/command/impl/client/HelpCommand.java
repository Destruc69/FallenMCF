package paul.fallen.command.impl.client;

import paul.fallen.FALLENClient;
import paul.fallen.command.Command;
import paul.fallen.utils.client.ClientUtils;

public class HelpCommand extends Command {

    public HelpCommand() {
        this.setNames(new String[]{"help"});
    }

    public void runCommand(String[] args) {
        for (Command c : FALLENClient.INSTANCE.getCommandManager().getCommandList()) {
            ClientUtils.addChatMessage(c.getHelp());
        }
    }

    public String getHelp() {
        return "<help> - Show this help page";
    }
}
