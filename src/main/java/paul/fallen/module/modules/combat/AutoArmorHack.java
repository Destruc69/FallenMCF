/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AirItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.utils.entity.PlayerControllerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class AutoArmorHack extends Module {
    private final Setting useEnchantements;
    private final Setting swapWhileMoving;
    private final Setting delay;

    private int timer;

    public AutoArmorHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        useEnchantements = new Setting("UseEnchantements", this, true);
        swapWhileMoving = new Setting("SwapWhileMoving", this, true);
        delay = new Setting("Delay", this, 10, 2, 50, true);

        addSetting(useEnchantements);
        addSetting(swapWhileMoving);
        addSetting(delay);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer = 0;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            // wait for timer
            if (timer > 0) {
                timer--;
                return;
            }

            // check screen
            if (mc.currentScreen instanceof ContainerScreen)
                return;

            PlayerEntity player = mc.player;
            PlayerInventory inventory = player.inventory;

            if (!swapWhileMoving.getValBoolean()
                    && (player.moveForward != 0
                    || player.moveStrafing != 0))
                return;

            // store slots and values of best armor pieces
            int[] bestArmorSlots = new int[4];
            int[] bestArmorValues = new int[4];

            // initialize with currently equipped armor
            for (int type = 0; type < 4; type++) {
                bestArmorSlots[type] = -1;

                ItemStack stack = inventory.armorItemInSlot(type);
                if (!(stack.getItem() instanceof ArmorItem) && !(stack.getItem() instanceof AirItem))
                    continue;

                ArmorItem item = (ArmorItem) stack.getItem();
                bestArmorValues[type] = getArmorValue(item, stack);
            }

            // search inventory for better armor
            for (int slot = 0; slot < 36; slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);

                if (!(stack.getItem() instanceof ArmorItem) && !(stack.getItem() instanceof AirItem))
                    continue;

                ArmorItem item = (ArmorItem) stack.getItem();
                int armorIndex = item.getEquipmentSlot().getIndex();
                int armorValue = getArmorValue(item, stack);

                if (armorValue > bestArmorValues[armorIndex]) {
                    bestArmorSlots[armorIndex] = slot;
                    bestArmorValues[armorIndex] = armorValue;
                }
            }

            // equip better armor in random order
            ArrayList<Integer> types = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
            Collections.shuffle(types);
            for (int type : types) {
                // check if better armor was found
                int slot = bestArmorSlots[type];
                if (slot == -1)
                    continue;

                // check if armor can be swapped
                // needs 1 free slot where it can put the old armor
                ItemStack oldArmor = inventory.armorItemInSlot(type);
                if (inventory.getFirstEmptyStack() == -1)
                    continue;

                if (oldArmor.getItem() instanceof AirItem)
                    return;

                // hotbar fix
                if (slot < 9)
                    slot += 36;

                // swap armor
                if (!(oldArmor.getItem() instanceof AirItem))
                    PlayerControllerUtils.windowClick_QUICK_MOVE(8 - type);
                PlayerControllerUtils.windowClick_QUICK_MOVE(slot);

                break;
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacketOutput(PacketEvent event) {
        if (event.getPacket() instanceof CClickWindowPacket)
            timer = (int) delay.getValDouble();
    }

    private int getArmorValue(ArmorItem item, ItemStack stack) {
        int armorPoints = item.getDamageReduceAmount();
        int prtPoints = 0;
        int armorToughness = (int) item.getToughness();
        int armorType = item.getArmorMaterial()
                .getDamageReductionAmount(EquipmentSlotType.LEGS);

        if (useEnchantements.getValBoolean()) {
            Enchantment protection = Enchantments.PROTECTION;
            int prtLvl =
                    EnchantmentHelper.getEnchantmentLevel(protection, stack);

            PlayerEntity player = mc.player;
            DamageSource dmgSource = DamageSource.causePlayerDamage(player);
            prtPoints = protection.calcModifierDamage(prtLvl, dmgSource);
        }

        return armorPoints * 5 + prtPoints * 3 + armorToughness + armorType;
    }
}
