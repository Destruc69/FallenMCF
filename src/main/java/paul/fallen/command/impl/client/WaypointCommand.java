package paul.fallen.command.impl.client;

import paul.fallen.FALLENClient;
import paul.fallen.command.Command;
import paul.fallen.utils.client.ClientUtils;
import paul.fallen.waypoint.Waypoint;

public class WaypointCommand extends Command {

    public WaypointCommand() {
        this.setNames(new String[]{"waypoint", "w"});
    }

    public void runCommand(String[] args) {
        if (args[0].toLowerCase() == "add" || args[0].toLowerCase() == "a") {
            int x = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            Waypoint waypoint = new Waypoint(x, z);

            if (!FALLENClient.INSTANCE.getWaypointManager().getWaypoints().contains(waypoint)) {
                FALLENClient.INSTANCE.getWaypointManager().addWaypoint(x, z);
            }
        } else if (args[0].toLowerCase() == "delete" || args[0].toLowerCase() == "d") {
            int x = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            Waypoint waypoint = new Waypoint(x, z);

            if (FALLENClient.INSTANCE.getWaypointManager().getWaypoints().contains(waypoint)) {
                FALLENClient.INSTANCE.getWaypointManager().removeWaypoint(x, z);
            }
        } else {
            getHelp();
        }
    }


    public String getHelp() {
        return "<waypoint> (add <a> | del <d>) (x) (z)";
    }
}
