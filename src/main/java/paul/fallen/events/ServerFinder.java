package paul.fallen.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.widget.Slider;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerFinder {

    private TextFieldWidget ipTextField;
    private Slider depthSlider;
    private Button toggleSearchButton;
    private boolean active = false;
    private ScheduledExecutorService executor;
    private final AtomicInteger currentOffset = new AtomicInteger(0);

    private int depthRange = 256;
    private String baseIp = "192.168.0.1";

    // String to hold the most recent console message
    private String latestConsoleMessage = "";

    private TextFieldWidget latestMessageText; // Widget to display the latest message

    @SubscribeEvent
    public void onGui(GuiScreenEvent.InitGuiEvent.Post event) {
        //if (event.getGui() != null && event.getGui() instanceof MultiplayerScreen) {
        //    int screenHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();
//
        //    toggleSearchButton = new Button(94, screenHeight - 24 - 8, 60, 20, new StringTextComponent("Search"), button -> {
        //        active = !active;
        //        if (active) {
        //            toggleSearchButton.setFGColor(Color.GREEN.getRGB());
        //            startSearch();
        //        } else {
        //            toggleSearchButton.clearFGColor();
        //            stopSearch();
        //        }
        //    });
        //    event.addWidget(toggleSearchButton);
//
        //    ipTextField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, 160, screenHeight - 24 - 8, 100, 20, new StringTextComponent("Base IP"));
        //    ipTextField.setText(baseIp);
        //    event.addWidget(ipTextField);
//
        //    depthSlider = new Slider(270, screenHeight - 24 - 8, 120, 20, new StringTextComponent("Depth: "), new StringTextComponent(""), 1, 512, depthRange, false, true, slider -> depthRange = depthSlider.getValueInt());
        //    event.addWidget(depthSlider);
//
        //    // Create and add the TextWidget for the latest message
        //    //latestMessageText = new TextFieldWidget(new StringTextComponent(latestConsoleMessage), 400, screenHeight - 24 - 8, 100, 20, new StringTextComponent(latestConsoleMessage));
        //    //event.addWidget(latestMessageText);
        //}
    }

    private void startSearch() {
        baseIp = ipTextField.getText();
        currentOffset.set(0);
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new SearchRunnable(), 0, 1, TimeUnit.SECONDS);
    }

    private void stopSearch() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    logToConsole("Executor did not terminate in time");
                }
            } catch (InterruptedException e) {
                logToConsole("Shutdown interrupted");
            }
        }
    }

    private void logToConsole(String message) {
        latestConsoleMessage = message;  // Store only the most recent message
        System.out.println(latestConsoleMessage);  // For debugging purposes, print to the system console

        // Update the latest message display
        if (latestMessageText != null) {
            latestMessageText.setMessage(new StringTextComponent(latestConsoleMessage));
        }
    }

    private class SearchRunnable implements Runnable {
        @Override
        public void run() {
            String ip = generateIpAroundBase();
            if (ip == null) {
                stopSearch();
                return;
            }

            boolean isOnline = checkServer(ip, 25565);

            if (isOnline) {
                logToConsole("Server found: " + ip);
                stopSearch();
                Minecraft.getInstance().execute(() -> toggleSearchButton.setFGColor(Color.BLUE.getRGB()));
            } else {
                logToConsole("Checked IP: " + ip + " - No server found.");
            }
        }

        private boolean checkServer(String ip, int port) {
            try {
                InetSocketAddress address = new InetSocketAddress(ip, port);
                return address.getAddress().isReachable(2000);
            } catch (UnknownHostException e) {
                logToConsole("Unknown host: " + ip);
                return false;
            } catch (IOException e) {
                logToConsole("IO error checking server: " + ip);
                return false;
            } catch (Exception e) {
                logToConsole("General error: " + e.getMessage());
                return false;
            }
        }

        private String generateIpAroundBase() {
            String[] parts = baseIp.split("\\.");
            if (parts.length != 4) return null;

            int baseLastOctet = Integer.parseInt(parts[3]);
            int offset = currentOffset.getAndIncrement();
            if (offset > depthRange) {
                return null;
            }

            int searchOctet = (baseLastOctet + offset) % 256;
            return parts[0] + "." + parts[1] + "." + parts[2] + "." + searchOctet;
        }
    }
}