package paul.fallen.command.impl.client;

import paul.fallen.FALLENClient;
import paul.fallen.command.Command;
import paul.fallen.module.Module;
import paul.fallen.utils.client.ClientUtils;
import paul.fallen.utils.client.Logger;
import paul.fallen.utils.client.Logger.LogState;

import java.awt.event.KeyEvent;

public class BindCommand extends Command {

    public BindCommand() {
        this.setNames(new String[]{"bind", "b"});
    }

    public void runCommand(String[] args) {
        String modName = "";
        String keyName = "";
        if (args.length > 1) {
            modName = args[1];
            if (args.length > 2)
                keyName = args[2];
        }
        Module module = FALLENClient.INSTANCE.getModuleManager().getModule(modName);
        if (module.getName().equalsIgnoreCase("null")) {
            ClientUtils.addChatMessage("Invalid module.");
            return;
        }
        if (keyName.equalsIgnoreCase("NONE")) {
            ClientUtils.addChatMessage(module.getDisplayName() + "'s bind has been cleared.");
            module.setBind(0);
            FALLENClient.INSTANCE.getModuleManager().saveConfig(FALLENClient.INSTANCE.getGson());
            return;
        }
        try {
            module.setBind(KeyEvent.class.getField("VK_" + keyName.toUpperCase()).getInt(null));
            FALLENClient.INSTANCE.getModuleManager().saveConfig(FALLENClient.INSTANCE.getGson());
            if (KeyEvent.class.getField("VK_" + keyName.toUpperCase()).getInt(null) == 0) {
                ClientUtils.addChatMessage("Invalid key entered, Bind cleared.");
            } else {
                ClientUtils.addChatMessage(module.getDisplayName() + " bound to " + keyName);
            }
        } catch (IllegalArgumentException e) {
            Logger.log(LogState.Error, "Illegal argument exception in BindCommand!");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Logger.log(LogState.Error, "Illegal access exception in BindCommand!");
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Logger.log(LogState.Error, "No such field exception in BindCommand!");
            e.printStackTrace();
        } catch (SecurityException e) {
            Logger.log(LogState.Error, "Security exception in bind command");
            e.printStackTrace();
        }
    }

    public String getHelp() {
        return "<bind/b> (module) (key/none) - Bind a module to a key, or clear a bind.";
    }

}