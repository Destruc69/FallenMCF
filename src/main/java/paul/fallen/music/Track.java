package paul.fallen.music;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Track {
    public final File musicFile;
    public Clip clip;
    public boolean isPlaying;

    public Track(File musicFile) {
        this.musicFile = musicFile;
        this.isPlaying = false;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null && !isPlaying) {
            clip.start();
            isPlaying = true;
        }
    }

    public void pause() {
        if (clip != null && isPlaying) {
            clip.stop();
            isPlaying = false;
        }
    }

    public void resume() {
        if (clip != null && !isPlaying) {
            clip.start();
            isPlaying = true;
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.setMicrosecondPosition(0); // rewind to start
            isPlaying = false;
        }
    }
}
