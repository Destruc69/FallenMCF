package paul.fallen.utils.client;

import com.mojang.text2speech.NarratorWindows;

public class NarratorUtils {
    private static final NarratorWindows narratorWindows = new NarratorWindows();

    public static void say(String s, boolean interrupt) {
        narratorWindows.say(s, interrupt);
    }

    public static void clear() {
        narratorWindows.clear();
    }
}
