package paul.fallen.module.modules.client;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;
import paul.fallen.music.Track;
import paul.fallen.setting.Setting;
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

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (FALLENClient.INSTANCE.getMusicManager().getMp3Files().size() > 0) {
                if (track != null) {
                    if (!a) {
                        track.play();
                        a = true;
                    }
                    if (track.clip.getMicrosecondPosition() >= track.clip.getMicrosecondLength()) {
                        track = null;
                    }
                } else {
                    a = false;
                    if (!shuffle.bval) {
                        if (currentTrackIndex + 1 <= FALLENClient.INSTANCE.getMusicManager().getMp3Files().size()) {
                            currentTrackIndex = currentTrackIndex + 1;
                        } else {
                            currentTrackIndex = 0;
                        }
                    } else {
                        Random random = new Random();
                        currentTrackIndex = random.nextInt(FALLENClient.INSTANCE.getMusicManager().getMp3Files().size());
                    }
                    track = new Track(FALLENClient.INSTANCE.getMusicManager().getMp3Files().get(currentTrackIndex));
                }
            } else {
                ClientUtils.addChatMessage("You need to navigate to the music folder in the Fallen directory and place your MP3 files there.");
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
                    drawText("Now playing " + track.musicFile.getName().replace(".mp3", "") + " | " + track.clip.getMicrosecondPosition() % 60, mc.getMainWindow().getScaledWidth() / 2 - 100, 3, new Color((int) FALLENClient.INSTANCE.getClickgui().textRGB.x, (int) FALLENClient.INSTANCE.getClickgui().textRGB.y, (int) FALLENClient.INSTANCE.getClickgui().textRGB.z));
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