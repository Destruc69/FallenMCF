package paul.fallen.waypoint;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.utils.client.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WaypointManager implements ClientSupport {

    private final ArrayList<Waypoint> waypoints = new ArrayList<>();

    public WaypointManager() {
        Logger.log(Logger.LogState.Normal, "Initiating Gson for WaypointManager");
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void loadConfig(Gson gson) {
        File dir = new File(mc.gameDir + File.separator + "Fallen" + File.separator + "waypoints");
        if (dir.exists()) {
            File[] directoryListing = dir.listFiles();
            for (File f : directoryListing) {
                try (FileReader reader = new FileReader(f)) {
                    Map<String, Object> map = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    Waypoint waypoint = new Waypoint((Integer) map.get("x"), (Integer) map.get("z"));
                    this.waypoints.add(waypoint);
                    Logger.log(Logger.LogState.Normal, "Loaded waypoint " + "[" + waypoint.getX() + "," + " " + waypoint.getZ() + "]" + " from Json!");
                } catch (JsonSyntaxException | JsonIOException | IOException e) {
                    Logger.log(Logger.LogState.Error, "Error loading waypoints from Json: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveConfig(Gson gson) {
        for (Waypoint waypoint : this.waypoints) {
            File file = new File(mc.gameDir + File.separator + "Fallen" + File.separator + "waypoints" + File.separator + "wp_" + waypoint.getX() + "-" + waypoint.getZ() + ".json");
            if (!file.exists()) {
                new File(mc.gameDir + File.separator + "Fallen" + File.separator + "waypoints").mkdirs();
                try {
                    file.createNewFile();
                    Logger.log(Logger.LogState.Normal, "Created new Json file: " + file.getName());
                } catch (IOException e) {
                    Logger.log(Logger.LogState.Error, "File.createNewFile() I/O exception in WaypointManager.saveConfig()!");
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                Map<String, Object> map = new HashMap<>();
                map.put("x", waypoint.getX());
                map.put("z", waypoint.getZ());
                gson.toJson(map, writer);
                Logger.log(Logger.LogState.Normal, "Wrote Json file: " + file.getName());
            } catch (IOException e) {
                Logger.log(Logger.LogState.Error, "Error writing to Json file: " + file.getName());
            }
        }
    }

    public void addWaypoint(int x, int z) {
        this.waypoints.add(new Waypoint(x, z));
        saveConfig(FALLENClient.INSTANCE.getGson());
    }

    public void removeWaypoint(int x, int z) {
        this.waypoints.removeIf(waypoint -> waypoint.getX() == x && waypoint.getZ() == z);
        saveConfig(FALLENClient.INSTANCE.getGson());
    }
}