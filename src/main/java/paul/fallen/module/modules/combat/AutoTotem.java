/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.PlayerControllerUtils;

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

            Player player = mc.player;
            assert player != null;
            Inventory inventory = player.getInventory();
            int nextTotemSlot = searchForTotems(inventory);

            ItemStack offhandStack = inventory.getItem(40);
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

        Player player = mc.player;
        assert player != null;
        PlayerControllerUtils.windowClick_QUICK_MOVE(nextTotemSlot);
        PlayerControllerUtils.windowClick_QUICK_MOVE(45);

        if (!offhandEmpty)
            nextTickSlot = nextTotemSlot;
    }

    private void finishMovingTotem() {
        if (nextTickSlot == -1)
            return;

        Player player = mc.player;
        assert player != null;
        PlayerControllerUtils.windowClick_QUICK_MOVE(nextTickSlot);
        nextTickSlot = -1;
    }

    private int searchForTotems(Inventory inventory) {
        int nextTotemSlot = -1;

        for (int slot = 0; slot <= 36; slot++) {
            if (!isTotem(inventory.getItem(slot).getItem()))
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
