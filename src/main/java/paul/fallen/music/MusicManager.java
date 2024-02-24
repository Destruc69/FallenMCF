package paul.fallen.music;

import paul.fallen.ClientSupport;
import paul.fallen.utils.client.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MusicManager implements ClientSupport {

    private final ArrayList<File> soundFiles = new ArrayList<>();

    public MusicManager() {
        Logger.log(Logger.LogState.Normal, "Initializing MusicManager");
        loadMp3Files();
    }

    public ArrayList<File> getMp3Files() {
        return soundFiles;
    }

    public void loadMp3Files() {
        File dir = new File(mc.gameDir + File.separator + "Fallen" + File.separator + "music");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                Collections.addAll(soundFiles, files);
            }
        } else {
            if (dir.mkdirs()) {
                Logger.log(Logger.LogState.Normal, "Created new directory: " + dir.getName());
            }
        }
    }

    public void refreshMp3Files() {
        soundFiles.clear();
        loadMp3Files();
    }
}