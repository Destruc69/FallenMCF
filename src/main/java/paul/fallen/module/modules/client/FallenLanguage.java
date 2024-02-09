package paul.fallen.module.modules.client;

import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.client.ClientUtils;

import java.util.HashMap;
import java.util.Map;

public class FallenLanguage extends Module {

    public FallenLanguage(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        onEnable();
    }

    @Override
    public void onEnable() {
        try {
            ClientUtils.addChatMessage("You are now using the Fallen language, a fictional collection of words and letters believed to originate from ancient North Korea.");
            ClientUtils.addChatMessage("All text messages sent will be converted to Fallen language, while all messages received will be converted from Fallen language to English. This means that only players with this module enabled will understand the messages. Therefore, messages received will only make sense if the player who sent them also had this module enabled.");
            ClientUtils.addChatMessage("For those who don't have the module enabled, they will see the message as Fallen language and will not understand it. If they inquire about its meaning, simply ignore them.");
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            ClientUtils.addChatMessage("You are using the Fallen language, a fictional collection of words and letters believed to originate from ancient North Korea.");
            ClientUtils.addChatMessage("All text messages sent will be converted to Fallen language, while all messages received will be converted from Fallen language to English. This means that only players with this module enabled will understand the messages. Therefore, messages received will only make sense if the player who sent them also had this module enabled.");
            ClientUtils.addChatMessage("For those who don't have the module enabled, they will see the message as Fallen language and will not understand it. If they inquire about its meaning, simply ignore them.");
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String obfuscateMessage = obfuscate(event.getMessage());
        event.setMessage(obfuscateMessage);
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        String decodedMessage = decode(event.getMessage());
        event.setComponent(new StringTextComponent(decodedMessage));

        if (decodedMessage.contains(mc.player.getDisplayName().getString())) {
            mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
    }

    private String obfuscate(String str) {
        Map<Character, Character> substitutionMap = generateSubstitutionMap();
        StringBuilder obfuscatedNote = new StringBuilder();

        for (char character : String.valueOf(str).toCharArray()) {
            obfuscatedNote.append(substitutionMap.getOrDefault(character, character));
        }

        return obfuscatedNote.toString();
    }

    private String decode(String str) {
        Map<Character, Character> substitutionMap = generateSubstitutionMap();
        StringBuilder decodedNote = new StringBuilder();

        for (char character : str.toCharArray()) {
            if (character == ' ') {
                // Handle spaces
                decodedNote.append(' ');
            } else {
                // Handle other characters using the substitution map
                Character decodedChar = substitutionMap.get(character);
                if (decodedChar != null) {
                    decodedNote.append(decodedChar);
                } else {
                    // If the character is not found in the substitution map, append it as is
                    decodedNote.append(character);
                }
            }
        }

        return decodedNote.toString();
    }

    private Map<Character, Character> generateSubstitutionMap() {
        Map<Character, Character> substitutionMap = new HashMap<>();

        // Define pairs of characters
        char[] originalChars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        char[] substituteChars = "gjeyldhzufiomnqarxvbtkwpsc5418703629".toCharArray();

        // Populate the substitution map
        for (int i = 0; i < originalChars.length; i++) {
            substitutionMap.put(originalChars[i], substituteChars[i]); // Reversed the mapping
        }

        return substitutionMap;
    }
}
