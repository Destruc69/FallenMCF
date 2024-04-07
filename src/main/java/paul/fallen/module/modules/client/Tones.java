package paul.fallen.module.modules.client;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.music.Track;
import paul.fallen.utils.client.ClientUtils;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.Random;

public class Tones extends Module {

    private final Setting shuffle;

    private Track track;
    private int currentTrackIndex = 0; // keep track of the current track index
    private boolean a = false;

    public Tones(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        shuffle = new Setting("Shuffle", this, false);
        addSetting(shuffle);
    }

    @Override
    public void onDisable() {
        try {
            super.onDisable();

            if (track.isPlaying) {
                track.stop();
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (FALLENClient.INSTANCE.getMusicManager().getMp3Files().size() > 0) {
                if (track != null) {
                    // Inside onPlayerTick method, when a track starts playing
                    if (!a) {
                        track.play();
                        a = true;
                    }
                    if (track.clip.getMicrosecondPosition() >= track.clip.getMicrosecondLength()) {
                        track = null;
                    }
                } else {
                    a = false;
                    if (!shuffle.getValBoolean()) {
                        // When currentTrackIndex is at the last song, this condition will now correctly reset it to 0
                        if (currentTrackIndex + 1 >= FALLENClient.INSTANCE.getMusicManager().getMp3Files().size()) {
                            currentTrackIndex = 0;
                        } else {
                            currentTrackIndex++;
                        }
                    } else {
                        Random random = new Random();
                        currentTrackIndex = random.nextInt(FALLENClient.INSTANCE.getMusicManager().getMp3Files().size());
                    }
                    track = new Track(FALLENClient.INSTANCE.getMusicManager().getMp3Files().get(currentTrackIndex));
                }

            } else {
                ClientUtils.addChatMessage("You need to navigate to the music folder in the Fallen directory and place your sound files there.");
                setState(false);
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRenderHUD(RenderGameOverlayEvent.Post event) {
        try {
            if (FALLENClient.INSTANCE.getMusicManager().getMp3Files().size() > 0) {
                if (track != null) {
                    // Calculate the width of the window and the position for the text
                    int windowWidth = mc.getMainWindow().getScaledWidth();
                    int textPositionX = windowWidth / 2 - 100;

                    // Draw the "Now playing" text
                    drawText("Now playing " + track.musicFile.getName(), textPositionX, 3, new Color(255, 255, 255));

                    double progress = (double) track.clip.getMicrosecondPosition() / (double) track.clip.getMicrosecondLength();
                    int progressBarWidth = (int) (mc.fontRenderer.getStringWidth("Now playing " + track.musicFile.getName()) * progress); // Assuming the total width of the bar is 200 pixels

                    // Draw the progress bar
                    UIUtils.drawRect(textPositionX, 18, progressBarWidth, 5, new Color(255, 255, 255).getRGB());
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void drawText(String text, int x, int y, Color color) {
        GL11.glPushMatrix();
        GL11.glScaled(1, 1, 1);
        UIUtils.drawTextOnScreen(text, x, y, color.getRGB());
        GL11.glPopMatrix();
    }
}