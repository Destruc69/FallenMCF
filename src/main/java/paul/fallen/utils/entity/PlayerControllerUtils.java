/*
 * Copyright � 2018 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;

public final class PlayerControllerUtils {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void windowClick_QUICK_MOVE(int slot) {
        mc.gameMode.handleInventoryMouseClick(0, slot, 0, ClickType.QUICK_MOVE,
                mc.player);
    }
}
