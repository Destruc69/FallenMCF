package paul.fallen.module.modules.player;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

import java.util.Objects;

public final class Discord extends Module {

    public static DiscordRichPresence presence = new DiscordRichPresence();

    public static DiscordRPC rpc = DiscordRPC.INSTANCE;

    public Discord(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        onEnable();
    }

    @Override
    public void onEnable() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("891902442999017482", handlers, true, "");

        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.largeImageKey = "fallen";
        rpc.Discord_UpdatePresence(presence);

        presence.largeImageText = "Fallen Utility Mod";
        rpc.Discord_UpdatePresence(presence);
    }

    @Override
    public void onDisable() {
        rpc.Discord_Shutdown();
        rpc.Discord_ClearPresence();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.player != null;
            presence.details = mc.player.getName() + " | " + Objects.requireNonNull(mc.getCurrentServerData()).serverIP;
            presence.state = mc.player.getHealth() + " / " + mc.player.getMaxHealth();
        } catch (Exception ignored) {
        }
    }

}