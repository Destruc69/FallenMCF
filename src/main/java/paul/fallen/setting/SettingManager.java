package paul.fallen.setting;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import paul.fallen.ClientSupport;
import paul.fallen.module.Module;
import paul.fallen.utils.client.Logger;
import paul.fallen.utils.client.Logger.LogState;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingManager implements ClientSupport {

    private final ArrayList<Setting> settings;

    public SettingManager() {
        this.settings = new ArrayList<Setting>();
    }

    public void loadConfig(Gson gson) {
        for (Setting s : this.settings) {
            File file = new File(mc.gameDir + File.separator + "Fallen" + File.separator + "settings" + File.separator + s.getParentMod().getName() + File.separator + s.getName() + ".json");
            try (FileReader reader = new FileReader(file)) {
                Map<String, Object> map = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
                }.getType());
                if ((boolean) map.get("isCheck")) {
                    s.setValBoolean((boolean) map.get("value"));
                } else if ((boolean) map.get("isSlider")) {
                    s.setValDouble(Float.parseFloat(map.get("value").toString()));
                } else if ((boolean) map.get("isCombo")) {
                    s.setValString((String) map.get("value"));
                }
                Logger.log(LogState.Normal, "Loaded module settings " + s.getParentMod().getName() + ": " + s.getName() + " from Json!");
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

    public void saveConfig(Gson gson) {
        for (Setting s : this.settings) {
            File file = new File(mc.gameDir + File.separator + "Fallen" + File.separator + "settings" + File.separator + s.getParentMod().getName() + File.separator + s.getName() + ".json");
            if (!file.exists()) {
                new File(mc.gameDir + File.separator + "Fallen" + File.separator + "settings" + File.separator + s.getParentMod().getName()).mkdirs();
                try {
                    file.createNewFile();
                    Logger.log(LogState.Normal, "Created new Json file: " + file.getName());
                } catch (IOException e) {
                    Logger.log(LogState.Error, "File.createNewFile() I/O exception in SettingManager.saveConfig()!");
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                Map<String, Object> map = new HashMap<>();
                map.put("isCheck", s.isCheck());
                map.put("isSlider", s.isSlider());
                map.put("isCombo", s.isCombo());
                if (s.isCombo()) {
                    map.put("value", s.getValString());
                } else if (s.isCheck()) {
                    map.put("value", s.getValBoolean());
                } else if (s.isSlider()) {
                    map.put("value", s.getValDouble());
                }
                gson.toJson(map, writer);
                Logger.log(LogState.Normal, "Wrote Json file!");
            } catch (IOException e) {
                Logger.log(LogState.Error, "I/O exception in writing to Json: " + file.getName());
            }
        }
    }

    public void addSetting(Setting in) {
        this.settings.add(in);
        Logger.log(LogState.Normal, "Added setting " + in.getParentMod().getName() + ": " + in.getName() + ".");
    }

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public ArrayList<Setting> getSettingsByMod(Module mod) {
        ArrayList<Setting> out = new ArrayList<Setting>();
        for (Setting s : getSettings()) {
            if (s.getParentMod().equals(mod)) {
                out.add(s);
            }
        }
        if (out.isEmpty()) {
            return null;
        }
        return out;
    }

    public Setting getSettingByName(String name, Module mod) {
        for (Setting set : getSettings()) {
            if (set.getName().equalsIgnoreCase(name) && set.getParentMod() == mod) {
                return set;
            }
        }
        Logger.log(LogState.Error, "found: '" + name + "'!");
        return null;
    }
}