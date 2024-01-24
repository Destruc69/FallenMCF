/*
 * Copyright � 2018 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class PlayerControllerUtils {
    private static final Minecraft mc = Minecraft.getInstance();

    public static ItemStack windowClick_PICKUP(int slot) {
        return mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP,
                mc.player);
    }

    public static ItemStack windowClick_QUICK_MOVE(int slot) {
        return mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE,
                mc.player);
    }

    public static ItemStack windowClick_THROW(int slot) {
        return mc.playerController.windowClick(0, slot, 1, ClickType.THROW,
                mc.player);
    }

    public static void rightClickBlock(BlockPos blockPos, Direction direction) {
        assert mc.player != null;
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(mc.player, Hand.MAIN_HAND, blockPos, direction));
    }

    public static void rightClickItem(Hand hand) {
        assert mc.player != null;
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickItem(mc.player, hand));
    }

    public static void interactWithEntity(Hand hand, Entity entity) {
        assert mc.player != null;
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.EntityInteract(mc.player, hand, entity));
    }

    public static void leftClickBlock(BlockPos blockPos, Direction direction) {
        assert mc.player != null;
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickBlock(mc.player, blockPos, direction));
    }
}
