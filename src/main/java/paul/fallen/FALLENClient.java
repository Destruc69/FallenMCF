package paul.fallen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import paul.fallen.clickgui.ClickGui;
import paul.fallen.command.CommandManager;
import paul.fallen.events.AutoJoin;
import paul.fallen.events.GuiTweaks;
import paul.fallen.events.ServerFinder;
import paul.fallen.friend.FriendManager;
import paul.fallen.irc.IRC;
import paul.fallen.module.ModuleManager;
import paul.fallen.music.MusicManager;
import paul.fallen.packetevent.ChannelHandlerInput;
import paul.fallen.setting.SettingManager;
import paul.fallen.stevebot.mod.adapter.MinecraftAdapterImpl;
import paul.fallen.stevebot.mod.adapter.OpenGLAdapterImpl;
import paul.fallen.stevebot.mod.events.*;
import paul.fallen.utils.client.Logger;
import paul.fallen.utils.client.Logger.LogState;
import paul.fallen.waypoint.WaypointManager;
import stevebot.core.StevebotApi;
import stevebot.core.data.blocks.BlockLibrary;
import stevebot.core.data.blocks.BlockProvider;
import stevebot.core.data.blocks.BlockUtils;
import stevebot.core.data.items.ItemLibrary;
import stevebot.core.data.items.ItemUtils;
import stevebot.core.math.vectors.vec3.Vector3d;
import stevebot.core.minecraft.MinecraftAdapter;
import stevebot.core.minecraft.OpenGLAdapter;
import stevebot.core.pathfinding.PathHandler;
import stevebot.core.pathfinding.actions.ActionUtils;
import stevebot.core.player.*;
import stevebot.core.rendering.Renderer;

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
    private final ClickGui clickgui;
    private final IRC irc;
    private final Gson gson;
    //SteveBot
    private static EventManager eventManager;
    private static ModEventProducer eventProducer;
    private static BlockLibrary blockLibrary;
    private static BlockProvider blockProvider;
    private static ItemLibrary itemLibrary;
    private static PlayerCamera playerCamera;
    private static PlayerMovement playerMovement;
    private static PlayerInput playerInput;
    private static PlayerInventory playerInventory;
    private static Renderer renderer;
    private static PathHandler pathHandler;
    private final StevebotApi stevebotApi;
    private final EventListener<PostInitEvent> listenerPostInit = new EventListener<PostInitEvent>() {
        @Override
        public Class<PostInitEvent> getEventClass() {
            return PostInitEvent.class;
        }

        @Override
        public void onEvent(PostInitEvent event) {
            blockLibrary.onEventInitialize();
            itemLibrary.onEventInitialize();
        }
    };

    private final EventListener<BlockEvent.BreakEvent> listenerBreakBlock = new EventListener<BlockEvent.BreakEvent>() {
        @Override
        public Class<BlockEvent.BreakEvent> getEventClass() {
            return BlockEvent.BreakEvent.class;
        }


        @Override
        public void onEvent(BlockEvent.BreakEvent event) {
            blockProvider.getBlockCache().onEventBlockBreak(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
        }
    };

    private final EventListener<BlockEvent.EntityPlaceEvent> listenerPlaceBlock = new EventListener<BlockEvent.EntityPlaceEvent>() {
        @Override
        public Class<BlockEvent.EntityPlaceEvent> getEventClass() {
            return BlockEvent.EntityPlaceEvent.class;
        }

        @Override
        public void onEvent(BlockEvent.EntityPlaceEvent event) {
            blockProvider.getBlockCache().onEventBlockPlace(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
        }
    };

    private final EventListener<TickEvent.RenderTickEvent> listenerRenderTick = new EventListener<TickEvent.RenderTickEvent>() {
        @Override
        public Class<TickEvent.RenderTickEvent> getEventClass() {
            return TickEvent.RenderTickEvent.class;
        }

        @Override
        public void onEvent(TickEvent.RenderTickEvent event) {
            playerCamera.onRenderTickEvent(event.phase == TickEvent.Phase.START);
        }
    };

    private final EventListener<TickEvent.ClientTickEvent> listenerClientTick = new EventListener<TickEvent.ClientTickEvent>() {
        @Override
        public Class<TickEvent.ClientTickEvent> getEventClass() {
            return TickEvent.ClientTickEvent.class;
        }


        @Override
        public void onEvent(TickEvent.ClientTickEvent event) {
            pathHandler.onEventClientTick();
        }

    };

    private final EventListener<RenderWorldLastEvent> listenerRenderWorld = new EventListener<RenderWorldLastEvent>() {
        @Override
        public Class<RenderWorldLastEvent> getEventClass() {
            return RenderWorldLastEvent.class;
        }

        @Override
        public void onEvent(RenderWorldLastEvent event) {
            renderer.onEventRender(new Vector3d(PlayerUtils.getPlayerPosition().x, PlayerUtils.getPlayerPosition().y, PlayerUtils.getPlayerPosition().z));
        }
    };

    private final EventListener<TickEvent.PlayerTickEvent> listenerPlayerTick = new EventListener<TickEvent.PlayerTickEvent>() {
        @Override
        public Class<TickEvent.PlayerTickEvent> getEventClass() {
            return TickEvent.PlayerTickEvent.class;
        }


        @Override
        public void onEvent(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                playerInput.onEventPlayerTick();
            }
        }
    };

    private final EventListener<ModConfig.ModConfigEvent> listenerConfigChanged = new EventListener<ModConfig.ModConfigEvent>() {
        @Override
        public Class<ModConfig.ModConfigEvent> getEventClass() {
            return ModConfig.ModConfigEvent.class;
        }

        @Override
        public void onEvent(ModConfig.ModConfigEvent event) {
            playerInput.onEventConfigChanged();
        }
    };


    public FALLENClient() {
        Logger.log(LogState.Normal, "Starting " + this.name + " Client | Version " + this.version);

        INSTANCE = this;

        Logger.log(LogState.Normal, "Initializing EventManager");

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(new ChannelHandlerInput());

        MinecraftForge.EVENT_BUS.register(new GuiTweaks());

        MinecraftForge.EVENT_BUS.register(new AutoJoin());

        MinecraftForge.EVENT_BUS.register(new ServerFinder());

        irc = new IRC();
        MinecraftForge.EVENT_BUS.register(irc);

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
        this.clickgui = new ClickGui();

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

        // SteveBot

        // minecraft
        MinecraftAdapter minecraftAdapter = new MinecraftAdapterImpl();
        OpenGLAdapter openGLAdapter = new OpenGLAdapterImpl();

        ActionUtils.initMinecraftAdapter(minecraftAdapter);

        // events
        eventManager = new EventManagerImpl();
        eventProducer = new ModEventProducer(eventManager);

        eventManager.addListener(listenerPostInit);
        eventManager.addListener(listenerBreakBlock);
        eventManager.addListener(listenerPlaceBlock);
        eventManager.addListener(listenerRenderTick);
        eventManager.addListener(listenerRenderWorld);
        eventManager.addListener(listenerPlayerTick);
        eventManager.addListener(listenerClientTick);
        eventManager.addListener(listenerConfigChanged);

        // block library
        blockLibrary = new BlockLibrary(minecraftAdapter);

        // block provider
        blockProvider = new BlockProvider(minecraftAdapter, blockLibrary);

        // block utils
        BlockUtils.initialize(minecraftAdapter, blockProvider, blockLibrary);

        // item library
        itemLibrary = new ItemLibrary(minecraftAdapter);

        // item utils
        ItemUtils.initialize(minecraftAdapter, itemLibrary);

        // renderer
        renderer = new Renderer(openGLAdapter, blockProvider);

        // player camera
        playerCamera = new PlayerCamera(minecraftAdapter);

        // player input
        playerInput = new PlayerInput(minecraftAdapter);

        // player movement
        playerMovement = new PlayerMovement(playerInput, playerCamera);

        // player inventory
        playerInventory = new PlayerInventory(minecraftAdapter);

        // player utils
        PlayerUtils.initialize(minecraftAdapter, playerInput, playerCamera, playerMovement, playerInventory);

        // path handler
        pathHandler = new PathHandler(minecraftAdapter, renderer);

        //API
        stevebotApi = new StevebotApi(pathHandler);
    }

    @SubscribeEvent
    public void preInit(FMLClientSetupEvent event) {
        eventProducer.onPreInit();
    }

    @SubscribeEvent
    public void init(FMLCommonSetupEvent event) {
        eventProducer.onInit();
    }

    @SubscribeEvent
    public void postInit(FMLLoadCompleteEvent event) {
        eventProducer.onPostInit();
        itemLibrary.insertBlocks(blockLibrary.getAllBlocks());
        blockLibrary.insertItems(itemLibrary.getAllItems());
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

    public ClickGui getClickgui() {
        return this.clickgui;
    }

    public Gson getGson() {
        return this.gson;
    }

    public MusicManager getMusicManager() {
        return this.musicManager;
    }

    public IRC getIrc() {
        return this.irc;
    }

    public StevebotApi getStevebotApi() {
        return this.stevebotApi;
    }
}
