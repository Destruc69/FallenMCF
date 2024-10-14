/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public final class AutoTotem extends Module {

    private final Setting delay;
    private final Setting health;
    private int nextTickSlot = -1;
    private boolean wasTotemInOffhand = false;
    private int timer = 0;

    public AutoTotem(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        delay = new Setting("Delay", this, 0, 0, 20, true);
        health = new Setting("Health", this, 0, 0, 20, true);
        addSetting(delay);
        addSetting(health);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        try {
            finishMovingTotem();

            PlayerEntity player = Minecraft.getInstance().player;
            assert player != null;
            PlayerInventory inventory = player.inventory;

            ItemStack offhandStack = inventory.getStackInSlot(40);
            if (isTotem(offhandStack.getItem())) {
                wasTotemInOffhand = true;
                return;
            }

            if (wasTotemInOffhand) {
                timer = (int) delay.getValDouble();
                wasTotemInOffhand = false;
            }

            float healthThreshold = (float) health.getValDouble();
            if (healthThreshold > 0 && player.getHealth() > healthThreshold * 2F) {
                return;
            }

            int nextTotemSlot = searchForTotems(inventory);
            if (nextTotemSlot == -1 || timer > 0) {
                timer--;
                return;
            }

            moveTotem(nextTotemSlot, offhandStack);
        } catch (Exception ignored) {
        }
    }

    private void moveTotem(int nextTotemSlot, ItemStack offhandStack) {
        PlayerEntity player = Minecraft.getInstance().player;
        assert player != null;
        assert Minecraft.getInstance().playerController != null;

        Minecraft.getInstance().playerController.windowClick(player.container.windowId, nextTotemSlot, 0, ClickType.PICKUP, player);
        Minecraft.getInstance().playerController.windowClick(player.container.windowId, 45, 0, ClickType.PICKUP, player);

        if (!offhandStack.isEmpty()) {
            nextTickSlot = nextTotemSlot;
        }
    }

    private void finishMovingTotem() {
        if (nextTickSlot == -1) {
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        assert player != null;
        assert Minecraft.getInstance().playerController != null;

        Minecraft.getInstance().playerController.windowClick(player.container.windowId, nextTickSlot, 0, ClickType.PICKUP, player);
        nextTickSlot = -1;
    }

    private int searchForTotems(PlayerInventory inventory) {
        for (int slot = 0; slot < 36; slot++) {
            if (isTotem(inventory.getStackInSlot(slot).getItem())) {
                return slot < 9 ? slot + 36 : slot;
            }
        }
        return -1;
    }

    private boolean isTotem(Item item) {
        return item == Items.TOTEM_OF_UNDYING;
    }
}