/*
 * Copyright � 2018 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

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

    public static void rightClickBlock(Vector3d hitVec, Direction side, BlockPos pos) {
        assert mc.player != null;
        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(hitVec, side, pos, false)));
    }
}
