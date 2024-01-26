/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public final class AutoTotem extends Module {

    private final Setting delay;
    private final Setting health;
    private int nextTickSlot = -1;
    private boolean wasTotemInOffhand = false;
    private int timer = 0;

    public AutoTotem(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        delay = new Setting("delay", "Delay", this, 0, 0, 20);
        health = new Setting("health", "Health", this, 0, 0, 20);
        addSetting(delay);
        addSetting(health);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            finishMovingTotem();

            PlayerEntity player = mc.player;
            assert player != null;
            PlayerInventory inventory = player.inventory;
            int nextTotemSlot = searchForTotems(inventory);

            ItemStack offhandStack = inventory.getStackInSlot(40);
            if (isTotem(offhandStack.getItem())) {
                wasTotemInOffhand = true;
                return;
            }

            if (wasTotemInOffhand) {
                timer = (int) delay.dval;
                wasTotemInOffhand = false;
            }

            float healthF = health.dval;
            if (healthF > 0 && player.getHealth() > healthF * 2F)
                return;

            if (nextTotemSlot == -1)
                return;

            if (timer > 0) {
                timer--;
                return;
            }

            moveTotem(nextTotemSlot, offhandStack);
        } catch (Exception ignored) {
        }
    }

    private void moveTotem(int nextTotemSlot, ItemStack offhandStack) {
        boolean offhandEmpty = offhandStack.isEmpty();

        PlayerEntity player = mc.player;
        assert mc.playerController != null;
        assert player != null;
        mc.playerController.windowClick(player.container.windowId, nextTotemSlot, 0, ClickType.PICKUP, player);
        mc.playerController.windowClick(player.container.windowId, 45, 0, ClickType.PICKUP, player);

        if (!offhandEmpty)
            nextTickSlot = nextTotemSlot;
    }

    private void finishMovingTotem() {
        if (nextTickSlot == -1)
            return;

        PlayerEntity player = mc.player;
        assert mc.playerController != null;
        assert player != null;
        mc.playerController.windowClick(player.container.windowId, nextTickSlot, 0, ClickType.PICKUP, player);
        nextTickSlot = -1;
    }

    private int searchForTotems(PlayerInventory inventory) {
        int nextTotemSlot = -1;

        for (int slot = 0; slot <= 36; slot++) {
            if (!isTotem(inventory.getStackInSlot(slot).getItem()))
                continue;

            if (nextTotemSlot == -1)
                nextTotemSlot = slot < 9 ? slot + 36 : slot;
        }

        return nextTotemSlot;
    }

    private boolean isTotem(Item item) {
        return item == Items.TOTEM_OF_UNDYING;
    }
}
