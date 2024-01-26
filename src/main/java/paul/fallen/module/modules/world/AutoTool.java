/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.world;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public final class AutoTool extends Module {
    Setting useSwords;
    Setting useHands;
    Setting repairMode;

    public AutoTool(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        useSwords = new Setting("UseSwords", this, true);
        useHands = new Setting("UseHands", this, true);
        repairMode = new Setting("Repair", this, true);
        addSetting(useSwords);
        addSetting(useHands);
        addSetting(repairMode);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            equipBestTool(new BlockPos(mc.objectMouseOver.getHitVec().x, mc.objectMouseOver.getHitVec().y, mc.objectMouseOver.getHitVec().z), useSwords.bval,
                    useHands.bval, repairMode.bval);
        } catch (Exception ignored) {
        }
    }

    public void equipBestTool(BlockPos pos, boolean useSwords, boolean useHands,
                              boolean repairMode) {
        PlayerEntity player = mc.player;
        if (player.isCreative())
            return;

        BlockState state = mc.world.getBlockState(pos);

        ItemStack heldItem = player.getHeldItemMainhand();
        float bestSpeed = getDestroySpeed(heldItem, state);
        int bestSlot = -1;

        int fallbackSlot = -1;
        PlayerInventory inventory = player.inventory;

        for (int slot = 0; slot < 9; slot++) {
            if (slot == inventory.currentItem)
                continue;

            ItemStack stack = inventory.getStackInSlot(slot);

            if (fallbackSlot == -1 && !isDamageable(stack))
                fallbackSlot = slot;

            float speed = getDestroySpeed(stack, state);
            if (speed <= bestSpeed)
                continue;

            if (!useSwords && stack.getItem() instanceof SwordItem)
                continue;

            if (isTooDamaged(stack, repairMode))
                continue;

            bestSpeed = speed;
            bestSlot = slot;
        }

        boolean useFallback =
                isDamageable(heldItem) && (isTooDamaged(heldItem, repairMode)
                        || useHands && getDestroySpeed(heldItem, state) <= 1);

        if (bestSlot != -1) {
            inventory.currentItem = bestSlot;
            return;
        }

        if (!useFallback)
            return;

        if (fallbackSlot != -1) {
            inventory.currentItem = fallbackSlot;
            return;
        }

        if (isTooDamaged(heldItem, repairMode))
            if (inventory.currentItem == 8)
                inventory.currentItem = 0;
            else
                inventory.currentItem++;
    }

    private float getDestroySpeed(ItemStack stack, BlockState state) {
        float speed = stack.getDestroySpeed(state);

        if (speed > 1) {
            int efficiency = EnchantmentHelper
                    .getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
            if (efficiency > 0 && !(stack.getItem() instanceof AirItem))
                speed += efficiency * efficiency + 1;
        }

        return speed;
    }

    private boolean isDamageable(ItemStack stack) {
        return !(stack.getItem() instanceof AirItem) && stack.getItem().isDamageable();
    }

    private boolean isTooDamaged(ItemStack stack, boolean repairMode) {
        return repairMode && stack.getMaxDamage() - stack.getDamage() <= 4;
    }
}