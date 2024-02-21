package paul.fallen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import paul.fallen.clickgui.Clickgui;
import paul.fallen.command.CommandManager;
import paul.fallen.friend.FriendManager;
import paul.fallen.module.ModuleManager;
import paul.fallen.music.MusicManager;
import paul.fallen.packetevent.ChannelHandlerInput;
import paul.fallen.setting.SettingManager;
import paul.fallen.utils.client.Logger;
import paul.fallen.utils.client.Logger.LogState;
import paul.fallen.waypoint.WaypointManager;

@Mod("fallen")
public class FALLENClient implements ClientSupport {

    public static FALLENClient INSTANCE;
    private final String name = "Fallen";
    private final String version = "1.16.5";
    private final String author = "Paul";
    private final ModuleManager moduleManager;
    private final SettingManager settingManager;
    private final FriendManager friendManager;
    private final CommandManager commandManager;
    private final MusicManager musicManager;
    private final WaypointManager waypointManager;
    private final Clickgui clickgui;
    private final Gson gson;

    public FALLENClient() {
        Logger.log(LogState.Normal, "Starting " + this.name + " Client | Version " + this.version);

        INSTANCE = this;

        Logger.log(LogState.Normal, "Initializing EventManager");

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(new ChannelHandlerInput());

        Logger.log(LogState.Normal, "Initializing Gson with pretty printing");
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        Logger.log(LogState.Normal, "Initializing MusicManager");
        musicManager = new MusicManager();

        Logger.log(LogState.Normal, "Initializing FriendManager");
        this.friendManager = new FriendManager();
        MinecraftForge.EVENT_BUS.register(friendManager);

        Logger.log(LogState.Normal, "Initializing SettingsManager");
        this.settingManager = new SettingManager();
        MinecraftForge.EVENT_BUS.register(settingManager);

        Logger.log(LogState.Normal, "Initializing ModuleManager");
        this.moduleManager = new ModuleManager();
        MinecraftForge.EVENT_BUS.register(moduleManager);

        Logger.log(LogState.Normal, "Initializing WaypointManager");
        this.waypointManager = new WaypointManager();
        MinecraftForge.EVENT_BUS.register(waypointManager);

        Logger.log(LogState.Normal, "Initializing CommandManager");
        this.commandManager = new CommandManager();
        MinecraftForge.EVENT_BUS.register(commandManager);

        Logger.log(LogState.Normal, "Loading FriendManager config");
        this.friendManager.loadConfig(gson);

        Logger.log(LogState.Normal, "Saving FriendManager config");
        this.friendManager.saveConfig(gson);

        Logger.log(LogState.Normal, "Loading ModuleManager config");
        this.moduleManager.loadConfig(gson);
        Logger.log(LogState.Normal, "Saving ModuleManager config");
        this.moduleManager.saveConfig(gson);

        Logger.log(LogState.Normal, "Loading WaypointManager config");
        this.waypointManager.loadConfig(gson);
        Logger.log(LogState.Normal, "Saving WaypointManager config");
        this.waypointManager.saveConfig(gson);

        Logger.log(LogState.Normal, "Loading SettingManager config");
        this.settingManager.loadConfig(gson);
        Logger.log(LogState.Normal, "Saving SettingManager config");
        this.settingManager.saveConfig(gson);

        Logger.log(LogState.Normal, "Initializing ImGui");
        this.clickgui = new Clickgui();

        Runtime.getRuntime().addShutdownHook(new Thread("Fallen Client shutdown thread") {
            public void run() {
                Logger.log(LogState.Normal, "Saving FriendManager config");
                INSTANCE.friendManager.saveConfig(gson);

                Logger.log(LogState.Normal, "Saving ModuleManager config");
                INSTANCE.moduleManager.saveConfig(gson);

                Logger.log(LogState.Normal, "Saving SettingManager config");
                INSTANCE.settingManager.saveConfig(gson);

                Logger.log(LogState.Normal, "Saving WaypointManager config");
                INSTANCE.waypointManager.saveConfig(gson);
            }
        });
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public SettingManager getSettingManager() {
        return this.settingManager;
    }

    public FriendManager getFriendManager() {
        return this.friendManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public WaypointManager getWaypointManager() {
        return this.waypointManager;
    }

    public Clickgui getClickgui() {
        return this.clickgui;
    }

    public Gson getGson() {
        return this.gson;
    }

    public MusicManager getMusicManager() {
        return this.musicManager;
    }
}
