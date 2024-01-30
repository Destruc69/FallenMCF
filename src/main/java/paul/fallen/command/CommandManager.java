package paul.fallen.command;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.command.impl.client.*;
import paul.fallen.module.Module;

import java.util.ArrayList;

public class CommandManager implements ClientSupport {

    private final ArrayList<Command> commandList = new ArrayList<Command>();
    private final SettingCommand settingCommand = new SettingCommand();
    private final UnknownCommand unknownCommand = new UnknownCommand();

    public String prefix = null;

    public CommandManager() {
        MinecraftForge.EVENT_BUS.register(this);

        commandList.add(new HelpCommand());
        commandList.add(new FriendCommand());
        commandList.add(new ToggleCommand());
        commandList.add(new BindCommand());
        commandList.add(new WaypointCommand());

        ArrayList<String> nameList = new ArrayList<String>();
        for (Module m : FALLENClient.INSTANCE.getModuleManager().getModules()) {
            nameList.add(m.getName());
        }
        settingCommand.setNames(nameList.toArray(new String[0]));
        commandList.add(settingCommand);
    }

    public ArrayList<Command> getCommandList() {
        return this.commandList;
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatEvent event) {
        if (prefix != null) {
            if (event.getMessage().startsWith(prefix)) {
                event.setCanceled(true);
                String message = event.getMessage().substring(1);
                String[] cmd = message.split(" ");
                Command command = getCommandFromMessage(message);
                command.runCommand(cmd);
            }
        } else {
            // Revert to default prefix
            if (event.getMessage().startsWith("$")) {
                event.setCanceled(true);
                String message = event.getMessage().substring(1);
                String[] cmd = message.split(" ");
                Command command = getCommandFromMessage(message);
                command.runCommand(cmd);
            }
        }
    }

    public Command getCommandFromMessage(String message) {
        for (Command command : this.commandList) {
            if (command.getNames() == null)
                return new UnknownCommand();
            String[] names;
            for (int length = (names = command.getNames()).length, i = 0; i < length; i++) {
                String name = names[i];
                if (message.split(" ")[0].equalsIgnoreCase(name))
                    return command;
            }
        }
        return unknownCommand;
    }

}