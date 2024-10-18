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
import java.io.File;
import java.util.List;
import java.util.Random;

public class Tones extends Module {

    private final Setting shuffle;
    private Track track;
    private int currentTrackIndex = 0; // keep track of the current track index
    private boolean isTrackPlaying = false;

    public Tones(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
        shuffle = new Setting("Shuffle", this, false);
        addSetting(shuffle);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (track != null && track.isPlaying) {
            track.stop();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        java.util.List<File> mp3Files = FALLENClient.INSTANCE.getMusicManager().getMp3Files();
        if (mp3Files.isEmpty()) {
            ClientUtils.addChatMessage("You need to navigate to the music folder in the Fallen directory and place your sound files there.");
            setState(false);
            return;
        }

        if (track != null) {
            if (!isTrackPlaying) {
                track.play();
                isTrackPlaying = true;
            }
            if (track.clip.getMicrosecondPosition() >= track.clip.getMicrosecondLength()) {
                track = null;
            }
        } else {
            isTrackPlaying = false;
            if (shuffle.getValBoolean()) {
                currentTrackIndex = new Random().nextInt(mp3Files.size());
            } else {
                currentTrackIndex = (currentTrackIndex + 1) % mp3Files.size();
            }
            track = new Track(mp3Files.get(currentTrackIndex));
        }
    }

    @SubscribeEvent
    public void onRenderHUD(RenderGameOverlayEvent.Post event) {
        List<File> mp3Files = FALLENClient.INSTANCE.getMusicManager().getMp3Files();
        if (mp3Files.isEmpty() || track == null) {
            return;
        }

        int windowWidth = mc.getMainWindow().getScaledWidth();
        int textPositionX = windowWidth / 2 - 100;
        String nowPlayingText = "Now playing " + track.musicFile.getName();

        drawText(nowPlayingText, textPositionX, 3, Color.WHITE);

        double progress = (double) track.clip.getMicrosecondPosition() / (double) track.clip.getMicrosecondLength();
        int progressBarWidth = (int) (mc.fontRenderer.getStringWidth(nowPlayingText) * progress);

        // Draw the progress bar
        UIUtils.drawRect(textPositionX, 18, progressBarWidth, 5, Color.WHITE.getRGB());
    }

    private void drawText(String text, int x, int y, Color color) {
        GL11.glPushMatrix();
        GL11.glScaled(1, 1, 1);
        UIUtils.drawTextOnScreen(text, x, y, color.getRGB());
        GL11.glPopMatrix();
    }
}