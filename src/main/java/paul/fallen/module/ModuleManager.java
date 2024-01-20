package paul.fallen.module;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.ClientSupport;
import paul.fallen.module.Module.Category;
import paul.fallen.module.modules.ClickGuiHack;
import paul.fallen.module.modules.combat.AntiKnockback;
import paul.fallen.module.modules.misc.FancyChat;
import paul.fallen.module.modules.movement.ScreenWalk;
import paul.fallen.module.modules.player.Sneak;
import paul.fallen.module.modules.render.HUD;
import paul.fallen.module.modules.render.NoWeather;
import paul.fallen.module.modules.world.FastPlace;
import paul.fallen.utils.client.Logger;
import paul.fallen.utils.client.Logger.LogState;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ModuleManager implements ClientSupport {

	private final ArrayList<Module> modules = new ArrayList<Module>();

	public ModuleManager() {
		MinecraftForge.EVENT_BUS.register(this);

		Logger.log(LogState.Normal, "Adding modules to ModuleManager");
		addModule(new NoWeather(0, "NoWeather", "No Weather", Category.Render));
		//addModule(new Tracers(0, "Tracers", Category.Render));
		addModule(new ScreenWalk(0, "ScreenWalk", "Screen Walk", Category.Movement));
		addModule(new Sneak(KeyEvent.VK_Z, "Sneak", Category.Player));
		addModule(new AntiKnockback(0, "AntiKnockback", "Anti Knockback", Category.Combat));
		//addModule(new Surround(0, "Surround", Category.Combat)); /* Bugged */
		//addModule(new Untrap(0, "Untrap", Category.Combat)); /* Bugged */
		addModule(new FastPlace(KeyEvent.VK_J, "FastPlace", "Fast Place", Category.World));
		addModule(new FancyChat(0, "FancyChat", "Fancy Chat", Category.Misc));
		addModule(new HUD(0, "HUD", Category.Render));
		addModule(new ClickGuiHack(KeyEvent.VK_P, "ClickGUI", "ClickGUI", Category.Render));
	}

	public ArrayList<Module> getModules() {
		return this.modules;
	}

	public ArrayList<Module> getModulesInCategory(Category category) {
		ArrayList<Module> modules = new ArrayList<>();
		for (Module module : getModules()) {
			if (module.getCategory() == category) {
				modules.add(module);
			}
		}
		return modules;
	}

	public ArrayList<Module> getModulesForArrayList() {
		ArrayList<Module> renderList = this.modules;
		renderList.sort(new Comparator<Module>() {
			public int compare(Module m1, Module m2) {
				String s1 = String.format("%s" + ((m1.getSuffix().length() > 0) ? " %s" : ""), m1.getDisplayName(), m1.getSuffix());
				String s2 = String.format("%s" + ((m2.getSuffix().length() > 0) ? " %s" : ""), m2.getDisplayName(), m2.getSuffix());
				return mc.fontRenderer.getStringWidth(s2) - mc.fontRenderer.getStringWidth(s1);
			}
		});
		return renderList;
	}

	public Module getModule(String s) {
		for (Module m : this.modules) {
			if (m.getName().equalsIgnoreCase(s)) {
				return m;
			}
		}
		return new Module(0, "Null", Category.Misc);
	}

	public void addModule(Module m) {
		this.modules.add(m);
	}

	public void loadConfig(Gson gson) {
		for (Module m : this.modules) {
			File file = new File(mc.gameDir + File.separator + "BDSM" + File.separator + "modules" + File.separator + m.getName() + ".json");
			try (FileReader reader = new FileReader(file)) {
				Map<String, Object> map = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
				}.getType());
				m.setBind((int) Math.round((double) map.get("bind")));
				m.setState((boolean) map.get("toggled"));
				Logger.log(LogState.Normal, "Loaded module " + m.getName() + " from Json!");
			} catch (JsonSyntaxException e) {
				Logger.log(LogState.Error, "Json syntax error in ModuleManager.loadConfig()!");
				e.printStackTrace();
			} catch (JsonIOException e) {
				Logger.log(LogState.Error, "Json I/O exception in ModuleManager.loadConfig()!");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				Logger.log(LogState.Error, "Json file not found exception in ModuleManager.loadConfig()!");
				e.printStackTrace();
			} catch (IOException e1) {
				Logger.log(LogState.Error, "Json I/O exception in ModuleManager.loadConfog()!");
				e1.printStackTrace();
			}
		}
	}

	public void saveConfig(Gson gson) {
		for (Module m : this.modules) {
			File file = new File(mc.gameDir + File.separator + "Fallen" + File.separator + "modules" + File.separator + m.getName() + ".json");
			if (!file.exists()) {
				new File(mc.gameDir + File.separator + "Fallen" + File.separator + "modules").mkdirs();
				try {
					file.createNewFile();
					Logger.log(LogState.Normal, "Created new Json file: " + file.getName());
				} catch (IOException e) {
					Logger.log(LogState.Error, "File.createNewFile() I/O exception in ModuleManager.saveConfig()!");
				}
			}
			try (FileWriter writer = new FileWriter(file)) {
				Map<String, Object> map = new HashMap<>();
				map.put("name", m.getName());
				map.put("bind", m.getBind());
				map.put("toggled", m.getState());
				gson.toJson(map, writer);
				Logger.log(LogState.Normal, "Wrote Json file!");
			} catch (IOException e) {
				Logger.log(LogState.Error, "I/O exception in writing to Json: " + file.getName());
			}
		}
	}

	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event) {
		for (Module m : this.modules) {
			if (event.getKey() == m.getBind()) {
				m.toggle();
			}
		}
	}
}
