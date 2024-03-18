package paul.fallen.module;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import paul.fallen.ClientSupport;
import paul.fallen.module.Module.Category;
import paul.fallen.module.modules.client.ClickGuiHack;
import paul.fallen.module.modules.client.FallenLanguage;
import paul.fallen.module.modules.client.Pathfinder;
import paul.fallen.module.modules.client.Tones;
import paul.fallen.module.modules.combat.*;
import paul.fallen.module.modules.movement.*;
import paul.fallen.module.modules.pathing.AutoMine;
import paul.fallen.module.modules.pathing.AutoPilot;
import paul.fallen.module.modules.player.*;
import paul.fallen.module.modules.render.*;
import paul.fallen.module.modules.world.*;
import paul.fallen.utils.client.Logger;
import paul.fallen.utils.client.Logger.LogState;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModuleManager implements ClientSupport {

	private final ArrayList<Module> modules = new ArrayList<Module>();

	public ClickGuiHack clickGuiHack;
	public Pathfinder pathfinder;

	// For modules that use mixins
	public Timer timer;

	public ModuleManager() {
		MinecraftForge.EVENT_BUS.register(this);

		Logger.log(LogState.Normal, "Adding modules to ModuleManager");
		addModule(new HUD(0, "HUD", Category.Render));
		addModule(new AntiSwing(0, "AntiSwing", "AntiSwing", Category.Combat));
		addModule(new Killaura(0, "Killaura", "Killaura", Category.Combat));
		addModule(new AntiBot(0, "AntiBot", "AntiBot", Category.Combat));
		addModule(new AutoArmorHack(0, "AutoArmor", "AutoArmor", Category.Combat));
		addModule(new AutoEat(0, "AutoEat", "AutoEat", Category.Combat));
		addModule(new AutoTotem(0, "AutoTotem", "AutoTotem", Category.Combat));
		addModule(new Criticals(0, "Criticals", "Criticals", Category.Combat));
		addModule(new CrystalAuraHack(0, "CrystalAura", "CrystalAura", Category.Combat));
		addModule(new FastBow(0, "FastBow", "FastBow", Category.Combat));
		addModule(new NoKnockBack(0, "NoKnockback", "NoKnockback", Category.Combat));
		addModule(new InfiniteAura(0, "InfiniteAura", "InfiniteAura", Category.Combat));
		addModule(new Regen(0, "Regen", "Regen", Category.Combat));
		addModule(new WTap(0, "WTap", "WTap", Category.Combat));
		addModule(new ComboAttack(0, "ComboAttack", "ComboAttack", Category.Combat));
		addModule(new BackTrack(0, "BackTrack", "BackTrack", Category.Combat));
		addModule(new LegitFightBot(0, "LegitFightBot", "LegitFightBot", Category.Combat));

		addModule(new AutoPilot(0, "AutoPilot", "AutoPilot", Category.Pathing));
		addModule(new AutoMine(0, "AutoMine", "AutoMine", Category.Pathing));

		addModule(new FallenLanguage(0, "FallenLanguage", "FallenLanguage", Category.Client));
		clickGuiHack = new ClickGuiHack(KeyEvent.VK_P, "ClickGUI", "ClickGUI", Category.Client);
		addModule(clickGuiHack);
		addModule(new Tones(0, "Tones", "Tones", Category.Client));
		pathfinder = new Pathfinder(0, "Pathfinder", "Pathfinder", Category.Client);
		addModule(pathfinder);

		addModule(new AntiAFK(0, "AntiAFK", "AntiAFK", Category.Movement));
		addModule(new ElytraFlight(0, "ElytraFlight", "ElytraFlight", Category.Movement));
		addModule(new AntiHunger(0, "AntiHunger", "AntiHunger", Category.Movement));
		addModule(new AntiVoid(0, "AntiVoid", "AntiVoid", Category.Movement));
		addModule(new AutoMove(0, "AutoMove", "AutoMove", Category.Movement));
		addModule(new AutoSneak(0, "AutoSneak", "AutoSneak", Category.Movement));
		addModule(new AutoSprintHack(0, "AutoSprint", "AutoSprint", Category.Movement));
		addModule(new AntiAFK(0, "AutoSwim", "AutoSwim", Category.Movement));
		addModule(new BlinkHack(0, "Blink", "Blink", Category.Movement));
		addModule(new EntityFlight(0, "EntityFlight", "EntityFlight", Category.Movement));
		addModule(new EntitySpeed(0, "EntitySpeed", "EntitySpeed", Category.Movement));
		addModule(new FastFall(0, "FastFall", "FastFall", Category.Movement));
		addModule(new FastLadderHack(0, "FastLadder", "FastLadder", Category.Movement));
		addModule(new Flight(0, "Flight", "Flight", Category.Movement));
		addModule(new GlideHack(0, "Glide", "Glide", Category.Movement));
		addModule(new HighJump(0, "HighJump", "HighJump", Category.Movement));
		addModule(new InvMove(0, "InvMove", "InvMove", Category.Movement));
		addModule(new NoSlowDown(0, "NoSlowdown", "NoSlowdown", Category.Movement));
		addModule(new Speed(0, "Speed", "Speed", Category.Movement));
		addModule(new YawLock(0, "YawLock", "YawLock", Category.Movement));
		addModule(new Step(0, "Step", "Step", Category.Movement));
		addModule(new TridentFlight(0, "TridentFlight", "TridentFlight", Category.Movement));
		addModule(new LongJump(0, "LongJump", "LongJump", Category.Movement));
		addModule(new Jesus(0, "Jesus", "Jesus", Category.Movement));

		addModule(new HandPosition(0, "HandPosition", "HandPosition", Category.Player));
		addModule(new AntiCollide(0, "AntiCollide", "AntiCollide", Category.Player));
		addModule(new AntiCooldown(0, "AntiCooldown", "AntiCooldown", Category.Player));
		addModule(new Cheststealer(0, "ChestStealer", "ChestStealer", Category.Player));
		addModule(new Disabler(0, "Disabler", "Disabler", Category.Player));
		addModule(new Discord(0, "Discord", "Discord", Category.Player));
		addModule(new Freeze(0, "Freeze", "Freeze", Category.Player));
		addModule(new HideMyAss(0, "HideMyAss", "HideMyAss", Category.Player));
		addModule(new MoreInv(0, "MoreInv", "MoreInv", Category.Player));
		addModule(new NoFall(0, "NoFall", "NoFall", Category.Player));

		timer = new Timer(0, "Timer", "Timer", Category.Player);
		addModule(timer);


		addModule(new AntiForge(0, "AntiForge", "AntiForge", Category.Player));
		addModule(new ServerCrasher(0, "ServerCrasher", "ServerCrasher", Category.Player));

		addModule(new ChestEspHack(0, "ChestESP", "ChestESP", Category.Render));
		addModule(new AntiRender(0, "AntiRender", "AntiRender", Category.Render));
		addModule(new FreeCam(0, "Freecam", "Freecam", Category.Render));
		addModule(new Breadcrumbs(0, "Breadcrumbs", "Breadcrumbs", Category.Render));
		addModule(new FullbrightHack(0, "Fullbright", "Fullbright", Category.Render));
		addModule(new ItemEspHack(0,"ItemESP", "ItemESP", Category.Render));
		addModule(new MobEspHack(0, "MobESP", "MobESP", Category.Render));
		addModule(new PlayerEspHack(0, "PlayerESP", "PlayerESP", Category.Render));
		addModule(new WaypointModule(0, "Waypoint", "Waypoint", Category.Render));
		addModule(new HeadRoll(0, "HeadRoll", "HeadRoll", Category.Render));
		addModule(new Chams(0, "Chams", "Chams", Category.Render));
		addModule(new BodySpin(0, "BodySpin", "BodySpin", Category.Render));

		addModule(new AntiFog(0, "AntiFog", "AntiFog", Category.World));
		addModule(new AntiWeather(0, "AntiWeather", "AntiWeather", Category.World));
		addModule(new AutoMount(0, "AutoMount", "AutoMount", Category.World));
		addModule(new AutoTool(0, "AutoTool", "AutoTool", Category.World));
		addModule(new Scaffold(0, "Scaffold", "Scaffold", Category.World));
		addModule(new Nuker(0, "Nuker", "Nuker", Category.World));
		addModule(new AutoHighway(0, "AutoHighway", "AutoHighway", Category.World));
		addModule(new OverKill(0, "OverKill", "OverKill", Category.World));
		addModule(new FastBreak(0, "FastBreak", "FastBreak", Category.World));
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
		ArrayList<Module> moduleArrayList = new ArrayList<>();
		for (Module module : getModules()) {
			if (module.toggled) {
				moduleArrayList.add(module);
			}
		}
		return moduleArrayList;
	}

	public Module getModule(String s) {
		for (Module m : this.modules) {
			if (m.getName().equalsIgnoreCase(s)) {
				return m;
			}
		}
		return new Module(0, "Null", Category.World);
	}

	public void addModule(Module m) {
		this.modules.add(m);
	}

	public void loadConfig(Gson gson) {
		for (Module m : this.modules) {
			File file = new File(mc.gameDir + File.separator + "Fallen" + File.separator + "modules" + File.separator + m.getName() + ".json");
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
		if (event.getAction() == GLFW.GLFW_PRESS) { // Check if the key is pressed, not released
			for (Module m : this.modules) {
				if (event.getKey() == m.getBind() && !(mc.currentScreen instanceof ChatScreen)) {
					m.setState(!m.getState());
				}
			}
		}
	}
}