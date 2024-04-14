/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class Cheststealer extends Module {
    double slot;

    public Cheststealer(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent.PlayerTickEvent event) {
        if (mc.currentScreen instanceof ContainerScreen) {
            assert mc.player != null;
            if (mc.player.ticksExisted % 5 == 0) {
                if (slot + 1 <= 27) {
                    slot = slot + 1;
                }
            }
            ContainerScreen getState = (ContainerScreen) mc.currentScreen;
            if (!getState.getContainer().inventorySlots.get((int) slot).getStack().getItem().equals(Items.AIR)) {
                assert mc.playerController != null;
                mc.playerController.windowClick(getState.getContainer().windowId, (int) slot, 0, ClickType.QUICK_MOVE, mc.player);
                double emptySlot = getEmptySlot();
                mc.playerController.windowClick(getState.getContainer().getSlot((int) emptySlot).slotNumber, (int) emptySlot, 0, ClickType.QUICK_MOVE, mc.player);
            }
        } else {
            slot = 0;
        }
    }

    private double getEmptySlot() {
        for (int x = 0; x < mc.player.inventory.mainInventory.size(); x++) {
            if (mc.player.inventory.getStackInSlot(x).getItem().equals(Items.AIR)) {
                return x;
            }
        }
        return 0;
    }

}