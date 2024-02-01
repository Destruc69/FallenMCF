package paul.fallen.friend;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import paul.fallen.ClientSupport;
import paul.fallen.FALLENClient;
import paul.fallen.utils.client.Logger;
import paul.fallen.utils.client.Logger.LogState;
import paul.fallen.utils.text.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendManager implements ClientSupport {

    private final ArrayList<Friend> friends = new ArrayList<Friend>();

    public FriendManager() {
        Logger.log(LogState.Normal, "Initiating Gson for FriendManager");

        addFriend("PaulBigBalls", "Paul");
    }

    public ArrayList<Friend> getFriendsList() {
        return friends;
    }

    public void loadConfig(Gson gson) {
        File dir = new File(mc.gameDirectory + File.separator + "Fallen" + File.separator + "friends");
        if (dir.exists()) {
            File[] directoryListing = dir.listFiles();
            for (File f : directoryListing) {
                try (FileReader reader = new FileReader(f)) {
                    Map<String, Object> map = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    Friend friend = new Friend((String) map.get("name"), (String) map.get("alias"));
                    this.friends.add(friend);
                    Logger.log(LogState.Normal, "Loaded friend " + friend.getName() + " from Json!");
                } catch (JsonSyntaxException e) {
                    Logger.log(LogState.Error, "Json syntax error in SettingManager.loadConfig()!");
                    e.printStackTrace();
                } catch (JsonIOException e) {
                    Logger.log(LogState.Error, "Json I/O exception in SettingManager.loadConfig()!");
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    Logger.log(LogState.Error, "Json file not found exception in SettingManager.loadConfig()!");
                    e.printStackTrace();
                } catch (IOException e1) {
                    Logger.log(LogState.Error, "Json I/O exception in SettingManager.loadConfog()!");
                    e1.printStackTrace();
                }
            }
        }
    }

    public void saveConfig(Gson gson) {
        for (Friend f : this.friends) {
            File file = new File(mc.gameDirectory + File.separator + "Fallen" + File.separator + "friends" + File.separator + f.getName() + ".json");
            if (!file.exists()) {
                new File(mc.gameDirectory + File.separator + "Fallen" + File.separator + "friends").mkdirs();
                try {
                    file.createNewFile();
                    Logger.log(LogState.Normal, "Created new Json file: " + file.getName());
                } catch (IOException e) {
                    Logger.log(LogState.Error, "File.createNewFile() I/O exception in ModuleManager.saveConfig()!");
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", f.getName());
                map.put("alias", f.getAlias());
                gson.toJson(map, writer);
                Logger.log(LogState.Normal, "Wrote Json file!");
            } catch (IOException e) {
                Logger.log(LogState.Error, "I/O exception in writing to Json: " + file.getName());
            }
        }
    }

    public void addFriend(String name, String alias) {
        getFriendsList().add(new Friend(name, alias));
        saveConfig(FALLENClient.INSTANCE.getGson());
    }

    public void removeFriend(String name) {
        for (Friend friend : getFriendsList()) {
            if (friend.name.equalsIgnoreCase(name)) {
                getFriendsList().remove(friend);
                File file = new File(mc.gameDirectory + File.separator + "Fallen" + File.separator + "friends" + File.separator + friend.getName() + ".json");
                if (file.exists()) {
                    file.delete();
                }
                break;
            }
        }
        saveConfig(FALLENClient.INSTANCE.getGson());
    }

    public String getAliasName(String name) {
        String alias = "";
        for (Friend friend : getFriendsList()) {
            if (friend.name.equalsIgnoreCase(StringUtils.stripControlCodes(name))) {
                alias = friend.alias;
                break;
            }
        }
        return alias;
    }

    public boolean isFriend(String name) {
        boolean isFriend = false;
        for (Friend friend : friends) {
            if (friend.name.equalsIgnoreCase(StringUtils.stripControlCodes(name))) {
                isFriend = true;
                break;
            }
        }
        if (mc.player.getGameProfile().getName() == name) {
            isFriend = true;
        }
        return isFriend;
    }

}