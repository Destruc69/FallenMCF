package paul.fallen.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.widget.Slider;

import java.awt.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class ServerFinder {

    private TextFieldWidget title;
    private Slider threads;
    private Button search;

    private boolean active = false;
    private ScheduledExecutorService executor;
    private int index = 0;

    @SubscribeEvent
    public void onGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof MultiplayerScreen) {
            int screenHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();

            // Adjusting position for the search button
            search = new Button(94, screenHeight - 24 - 8, 50, 20, new StringTextComponent("Search"), new Button.IPressable() {
                @Override
                public void onPress(Button p_onPress_1_) {
                    active = !active;
                    if (active) {
                        search.setFGColor(Color.GREEN.getRGB());
                        startSearch();
                    } else {
                        search.clearFGColor();
                        stopSearch();
                        index = 0;
                    }
                    threads.setValue(Math.round(threads.getValue()));
                }
            });
            event.addWidget(search);

            threads = new Slider(94 + 55, screenHeight - 24 - 8, 100, 20, new StringTextComponent("Threads "), new StringTextComponent(""), 1, 50, 3, true, true, new Button.IPressable() {
                @Override
                public void onPress(Button p_onPress_1_) {

                }
            });
            event.addWidget(threads);

            // Adjusting position for the title text field
            title = new TextFieldWidget(Minecraft.getInstance().fontRenderer, 4, screenHeight - 24 - 8, 80, 20, new StringTextComponent("ServerFinder"));
            title.setText("ServerFinder");
            title.setEnabled(false);
            event.addWidget(title);
        }
    }

    private void startSearch() {
        int numThreads = (int) threads.getValue();
        executor = Executors.newScheduledThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executor.scheduleAtFixedRate(new SearchRunnable(), 0, 1, TimeUnit.SECONDS);
        }
    }

    private void stopSearch() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

    private class SearchRunnable implements Runnable {
        @Override
        public void run() {
            ServerData serverData = new ServerData("MinecraftServer_" + index, generateIp(), true);
            ServerPinger serverPinger = new ServerPinger();
            try {
                serverPinger.ping(serverData, new Runnable() {
                    @Override
                    public void run() {
                        // Check if the server responded successfully
                        if (serverData.pingToServer > 0) {
                            Minecraft.getInstance().execute(() -> {
                                if (Minecraft.getInstance().currentScreen instanceof MultiplayerScreen) {
                                    MultiplayerScreen multiplayerScreen = (MultiplayerScreen) Minecraft.getInstance().currentScreen;
                                    multiplayerScreen.getServerList().addServerData(serverData);
                                }
                            });
                        }
                    }
                });
            } catch (UnknownHostException e) {
                // Handle the exception
            }

            search.setMessage(new StringTextComponent("Search [" + index + "]"));
            index++;
        }
    }

    private String generateIp() {
        int port = 25565;
        double ran1 = Math.round(Math.random() * 255);
        double ran2 = Math.round(Math.random() * 255);
        double ran3 = Math.round(Math.random() * 255);
        double ran4 = Math.round(Math.random() * 255);

        return ran1 + "." + ran2 + "." + ran3 + "." + ran4 + ":" + port;
    }
}