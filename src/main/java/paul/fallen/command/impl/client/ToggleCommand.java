package paul.fallen.command.impl.client;

import paul.fallen.FALLENClient;
import paul.fallen.command.Command;
import paul.fallen.module.Module;
import paul.fallen.utils.client.ClientUtils;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        this.setNames(new String[]{"toggle", "t"});
    }

    public void runCommand(String[] args) {
        String modName = "";
        if (args.length > 1)
            modName = args[1];
        Module module = FALLENClient.INSTANCE.getModuleManager().getModule(modName);
        if (module.getName().equalsIgnoreCase("null")) {
            ClientUtils.addChatMessage("Invalid Module.");
            return;
        }
        module.toggle();
        ClientUtils.addChatMessage(module.getDisplayName() + " is now " + (module.getState() ? "\247aenabled" : "\247cdisabled"));
        FALLENClient.INSTANCE.getModuleManager().saveConfig(FALLENClient.INSTANCE.getGson());
    }


    public String getHelp() {
        return "<t/toggle> (module) - Toggles a module on or off";
    }
}
