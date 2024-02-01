package paul.fallen.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;

public class InventoryUtils {

    private static final Minecraft mc = Minecraft.getInstance();

    public static void setSlot(int slot) {
        mc.player.getInventory().selected = slot;
    }

    public static void click(int slot) {
        PlayerControllerUtils.windowClick_QUICK_MOVE(slot);
    }

    public static int getSlot(Item item) {
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getItem(i).getItem().equals(item)) {
                return i;
            }
        }
        return -1;
    }
}
