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
import paul.fallen.FALLENClient;
import paul.fallen.module.Module.Category;
import paul.fallen.module.modules.client.ClickGuiHack;
import paul.fallen.module.modules.client.FallenLanguage;
import paul.fallen.module.modules.client.Pathfinder;
import paul.fallen.module.modules.client.Tones;
import paul.fallen.module.modules.combat.*;
import paul.fallen.module.modules.movement.*;
import paul.fallen.module.modules.pathing.AutoPilot;
import paul.fallen.module.modules.pathing.TreeBot;
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

	public ModuleManager() {
		MinecraftForge.EVENT_BUS.register(this);

		Logger.log(LogState.Normal, "Adding modules to ModuleManager");
		addModule(new HUD(0, "HUD", Category.Render, "Displays essential game information on screen"));
		addModule(new AntiSwing(0, "AntiSwing", "AntiSwing", Category.Combat, "Prevents swings on non-targets"));
		addModule(new Killaura(0, "Killaura", "Killaura", Category.Combat, "Automatically attacks nearby enemies"));
		addModule(new AntiBot(0, "AntiBot", "AntiBot", Category.Combat, "Removes bots in your game"));
		addModule(new AutoArmorHack(0, "AutoArmor", "AutoArmor", Category.Combat, "Automatically equips armor when available"));
		addModule(new AutoEat(0, "AutoEat", "AutoEat", Category.Combat, "Automatically eats food to restore health"));
		addModule(new AutoTotem(0, "AutoTotem", "AutoTotem", Category.Combat, "Automatically uses totems to prevent death"));
		addModule(new Criticals(0, "Criticals", "Criticals", Category.Combat, "Increases chance of dealing critical hits"));
		addModule(new CrystalAuraHack(0, "CrystalAura", "CrystalAura", Category.Combat, "Automatically places and detonates crystals"));
		addModule(new FastBow(0, "FastBow", "FastBow", Category.Combat, "Increases bow draw speed"));
		addModule(new NoKnockBack(0, "NoKnockback", "NoKnockback", Category.Combat, "Prevents knockback from attacks"));
		addModule(new InfiniteAura(0, "InfiniteAura", "InfiniteAura", Category.Combat, "Allows continuous attack without cooldown"));
		addModule(new Regen(0, "Regen", "Regen", Category.Combat, "Automatically regenerates health over time"));
		addModule(new WTap(0, "WTap", "WTap", Category.Combat, "Allows for increased knockback on hits"));
		addModule(new ComboAttack(0, "ComboAttack", "ComboAttack", Category.Combat, "Increases damage with consecutive hits"));
		addModule(new BackTrack(0, "BackTrack", "BackTrack", Category.Combat, "Allows you to track back to a previous position"));
		addModule(new LegitFightBot(0, "LegitFightBot", "LegitFightBot", Category.Combat, "Mimics legitimate player combat behavior"));
		addModule(new CrystalAuraReWrite(0, "CrystalAuraReWrite", "CrystalAuraReWrite", Category.Combat, "Enhanced crystal aura functionality"));
		addModule(new TriggerBot(0, "TriggerBot", "TriggerBot", Category.Combat, "Automatically attacks when a target is within range"));
		addModule(new AutoTNT(0, "AutoTNT", "AutoTNT", Category.Combat, "Automatically places and detonates TNT"));
		addModule(new Arson(0, "Arson", "Arson", Category.Combat, "Sets targets on fire for damage over time"));
		addModule(new Surround(0, "Surround", "Surround", Category.Combat, "Surrounds the player with blocks for protection"));
		addModule(new Untrap(0, "UnTrap", "UnTrap", Category.Combat, "Free yourself from being trapped by blocks"));

		addModule(new AutoPilot(0, "AutoPilot", "AutoPilot", Category.Pathing, "Automatically navigates towards a target"));
		addModule(new TreeBot(0, "TreeBot", "TreeBot", Category.Pathing, "Automates tree cutting and resource gathering"));

		addModule(new FallenLanguage(0, "FallenLanguage", "FallenLanguage", Category.Client, "Displays messages in a modified language"));
		clickGuiHack = new ClickGuiHack(KeyEvent.VK_P, "ClickGUI", "ClickGUI", Category.Client, "Opens a customizable GUI for module settings");
		addModule(clickGuiHack);
		addModule(new Tones(0, "Tones", "Tones", Category.Client, "Plays sound alerts for various events"));
		pathfinder = new Pathfinder(0, "Pathfinder", "Pathfinder", Category.Client, "Improves navigation and movement around obstacles");
		addModule(pathfinder);

		addModule(new AntiAFK(0, "AntiAFK", "AntiAFK", Category.Movement, "Prevents the player from being kicked for inactivity"));
		addModule(new ElytraFlight(0, "ElytraFlight", "ElytraFlight", Category.Movement, "Allows flying with elytra without needing rockets"));
		addModule(new AntiHunger(0, "AntiHunger", "AntiHunger", Category.Movement, "Prevents hunger from depleting"));
		addModule(new AntiVoid(0, "AntiVoid", "AntiVoid", Category.Movement, "Prevents falling into the void"));
		addModule(new AutoMove(0, "AutoMove", "AutoMove", Category.Movement, "Automatically moves the player forward"));
		addModule(new AutoSneak(0, "AutoSneak", "AutoSneak", Category.Movement, "Automatically sneaks when necessary"));
		addModule(new AutoSprintHack(0, "AutoSprint", "AutoSprint", Category.Movement, "Automatically sprints without holding the sprint key"));
		addModule(new AutoSwimHack(0, "AutoSwim", "AutoSwim", Category.Movement, "Improves swimming mechanics for faster movement"));
		addModule(new BlinkHack(0, "Blink", "Blink", Category.Movement, "Allows you to blink or teleport short distances"));
		addModule(new EntityFlight(0, "EntityFlight", "EntityFlight", Category.Movement, "Enables flight while riding an entity"));
		addModule(new EntitySpeed(0, "EntitySpeed", "EntitySpeed", Category.Movement, "Increases speed while riding an entity"));
		addModule(new FastFall(0, "FastFall", "FastFall", Category.Movement, "Allows the player to fall faster"));
		addModule(new FastLadderHack(0, "FastLadder", "FastLadder", Category.Movement, "Speeds up climbing ladders"));
		addModule(new Flight(0, "Flight", "Flight", Category.Movement, "Grants the ability to fly freely in the world"));
		addModule(new GlideHack(0, "Glide", "Glide", Category.Movement, "Enables gliding to reduce fall damage"));
		addModule(new HighJump(0, "HighJump", "HighJump", Category.Movement, "Allows for jumping higher than normal"));
		addModule(new InvMove(0, "InvMove", "InvMove", Category.Movement, "Enables movement while in the inventory"));
		addModule(new NoSlowDown(0, "NoSlowdown", "NoSlowdown", Category.Movement, "Prevents slow movement when using items"));
		addModule(new Speed(0, "Speed", "Speed", Category.Movement, "Increases player movement speed"));
		addModule(new YawLock(0, "YawLock", "YawLock", Category.Movement, "Locks the player's yaw to a fixed direction"));
		addModule(new Step(0, "Step", "Step", Category.Movement, "Allows the player to step up blocks smoothly"));
		addModule(new TridentFlight(0, "TridentFlight", "TridentFlight", Category.Movement, "Grants flight abilities when using a trident"));
		addModule(new LongJump(0, "LongJump", "LongJump", Category.Movement, "Allows for long-distance jumps"));
		addModule(new Jesus(0, "Jesus", "Jesus", Category.Movement, "Walk on water as if it were solid ground"));
		addModule(new FallFly(0, "FallFly", "FallFly", Category.Movement, "Allows flying while falling to avoid damage"));
		addModule(new BetterSwim(0, "BetterSwim", "BetterSwim", Category.Movement, "Improves swimming mechanics"));
		addModule(new FastSwim(0, "FastSwim", "FastSwim", Category.Movement, "Speeds up swimming for quicker travel"));
		addModule(new PerfectHorseJump(0, "PerfectHorseJump", "PerfectHorseJump", Category.Movement, "Enables optimal jumping for horses"));

		addModule(new HandPosition(0, "HandPosition", "HandPosition", Category.Player, "Adjusts the position of the player's hand"));
		addModule(new AntiCollide(0, "AntiCollide", "AntiCollide", Category.Player, "Prevents collisions with other players"));
		addModule(new AntiCooldown(0, "AntiCooldown", "AntiCooldown", Category.Player, "Reduces cooldowns for abilities and items"));
		addModule(new Cheststealer(0, "ChestStealer", "ChestStealer", Category.Player, "Automatically steals items from chests"));
		addModule(new Disabler(0, "Disabler", "Disabler", Category.Player, "Disables certain game mechanics to enhance performance"));
		addModule(new Discord(0, "Discord", "Discord", Category.Player, "Integrates Discord features into the game"));
		addModule(new Freeze(0, "Freeze", "Freeze", Category.Player, "Prevents movement of the player"));
		addModule(new HideMyAss(0, "HideMyAss", "HideMyAss", Category.Player, "Hides the player's name from others"));
		addModule(new MoreInv(0, "MoreInv", "MoreInv", Category.Player, "Increases inventory space for items"));
		addModule(new NoFall(0, "NoFall", "NoFall", Category.Player, "Prevents fall damage from occurring"));
		addModule(new FakeHackers(0, "FakeHackers", "FakeHackers", Category.Player, "Creates fake players to confuse opponents"));
		addModule(new PacketTimer(0, "PacketTimer", "PacketTimer", Category.Player, "Adjusts packet sending rates for better performance"));
		addModule(new AttributeModifier(0, "AttributeModifier", "AttributeModifier", Category.Player, "Modifies player attributes dynamically"));

		addModule(new AntiForge(0, "AntiForge", "AntiForge", Category.Player, "Prevents the use of forged items"));
		addModule(new ServerCrasher(0, "ServerCrasher", "ServerCrasher", Category.Player, "Causes the server to crash with specific commands"));

		addModule(new ChestEspHack(0, "ChestESP", "ChestESP", Category.Render, "Highlights chests through walls"));
		addModule(new AntiRender(0, "AntiRender", "AntiRender", Category.Render, "Disables rendering of specific elements"));
		addModule(new FreeCam(0, "Freecam", "Freecam", Category.Render, "Allows for free camera movement without player input"));
		addModule(new Breadcrumbs(0, "Breadcrumbs", "Breadcrumbs", Category.Render, "Leaves a trail of breadcrumbs for navigation"));
		addModule(new FullbrightHack(0, "Fullbright", "Fullbright", Category.Render, "Eliminates darkness for better visibility"));
		addModule(new ItemEspHack(0, "ItemESP", "ItemESP", Category.Render, "Displays item locations through walls"));
		addModule(new MobEspHack(0, "MobESP", "MobESP", Category.Render, "Highlights mobs for easy spotting"));
		addModule(new PlayerEspHack(0, "PlayerESP", "PlayerESP", Category.Render, "Highlights other players for visibility"));
		addModule(new WaypointModule(0, "Waypoint", "Waypoint", Category.Render, "Sets and displays waypoints on the map"));
		addModule(new HeadRoll(0, "HeadRoll", "HeadRoll", Category.Render, "Rolls the player's head for visual effect"));
		addModule(new BodySpin(0, "BodySpin", "BodySpin", Category.Render, "Spins the player's body for visual effect"));

		addModule(new FakePlayer(0, "FakePlayer", "FakePlayer", Category.World, "Creates a fake player in the world"));
		addModule(new AntiFog(0, "AntiFog", "AntiFog", Category.World, "Removes fog effects for better visibility"));
		addModule(new AntiWeather(0, "AntiWeather", "AntiWeather", Category.World, "Prevents weather effects from affecting gameplay"));
		addModule(new AutoMount(0, "AutoMount", "AutoMount", Category.World, "Automatically mounts available entities"));
		addModule(new AutoTool(0, "AutoTool", "AutoTool", Category.World, "Automatically selects the best tool for actions"));
		addModule(new Scaffold(0, "Scaffold", "Scaffold", Category.World, "Creates blocks under the player while moving"));
		addModule(new Nuker(0, "Nuker", "Nuker", Category.World, "Destroys blocks in a specified area rapidly"));
		addModule(new AutoHighway(0, "AutoHighway", "AutoHighway", Category.World, "Automatically builds highways while traveling"));
		addModule(new OverKill(0, "OverKill", "OverKill", Category.World, "Deals excessive damage to targets"));
		addModule(new FastBreak(0, "FastBreak", "FastBreak", Category.World, "Increases block-breaking speed"));
		addModule(new AutoFarm(0, "AutoFarm", "AutoFarm", Category.World, "Automatically farms crops and resources"));
		addModule(new AutoFish(0, "AutoFish", "AutoFish", Category.World, "Automatically catches fish"));
		addModule(new AutoEChestFarm(0, "AutoEChestFarm", "AutoEChestFarm", Category.World, "Automatically farms ender chests"));
		addModule(new Tunneler(0, "Tunneler", "Tunneler", Category.World, "Automatically digs tunnels in specified directions"));
		addModule(new AutoFill(0, "AutoFill", "AutoFill", Category.World, "Automatically fills containers with items"));

		// Sort modules so they are shown alphabetically
		this.modules.sort((module1, module2) -> {
			String name1 = module1.getName();
			String name2 = module2.getName();

			return Character.compare(name1.charAt(0), name2.charAt(0));
		});
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
		return new Module(0, "Null", Category.World, "");
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
		if (mc.world != null && mc.player != null) {
			if (mc.currentScreen != FALLENClient.INSTANCE.getClickgui()) {
				if (event.getAction() == GLFW.GLFW_PRESS) { // Check if the key is pressed, not released
					for (Module m : this.modules) {
						if (event.getKey() == m.getBind() && !(mc.currentScreen instanceof ChatScreen)) {
							m.setState(!m.getState());
						}
					}
				}
			}
		}
	}
}