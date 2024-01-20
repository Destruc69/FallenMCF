package paul.fallen.command.impl.client;

import paul.fallen.FALLENClient;
import paul.fallen.command.Command;
import paul.fallen.utils.client.ClientUtils;

public class FriendCommand extends Command {

    public FriendCommand() {
        this.setNames(new String[]{"friend", "f"});
    }

    public void runCommand(String[] args) {
        if (args.length < 3) {
            ClientUtils.addChatMessage(getHelp());
            return;
        }
        if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("a")) {
            String alias = args[2];
            if (args.length > 3) {
                alias = args[3];
            }
            if (FALLENClient.INSTANCE.getFriendManager().isFriend(args[2]) && args.length < 3) {
                ClientUtils.addChatMessage(args[2] + " is already your friend.");
                return;
            }
            FALLENClient.INSTANCE.getFriendManager().removeFriend(args[2]);
            FALLENClient.INSTANCE.getFriendManager().addFriend(args[2], alias);
            ClientUtils.addChatMessage("Added " + args[2] + ((args.length > 3) ? (" as " + alias) : ""));
        } else if (args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("d")) {
            if (FALLENClient.INSTANCE.getFriendManager().isFriend(args[2])) {
                FALLENClient.INSTANCE.getFriendManager().removeFriend(args[2]);
                ClientUtils.addChatMessage("Removed friend: " + args[2]);
            } else {
                ClientUtils.addChatMessage(args[2] + " is not your friend.");
            }
        } else {
            ClientUtils.addChatMessage(getHelp());
        }
    }

    public String getHelp() {
        return "<friend> (add <a> | del <d>) (name) (alias)";
    }

}