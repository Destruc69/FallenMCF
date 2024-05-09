/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.*;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.system.CallbackI;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.utils.client.ClientUtils;
import paul.fallen.utils.entity.InventoryUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Disabler extends Module {

    private Setting basic;
    private Setting pingSpoof;
    private Setting pingSpoofDelay;
    private Setting grimAc;
    private Setting tridentDelay;

    private final ConcurrentHashMap<IPacket, Long> packets = new ConcurrentHashMap<>();
    private int tick = 0;

    public Disabler(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        basic = new Setting("Basic", this, false);
        pingSpoof = new Setting("PingSpoof", this, false);
        pingSpoofDelay = new Setting("PingSpoofDelay", this, 1000, 200, 5000, true);
        grimAc = new Setting("GrimAc", this, false);
        tridentDelay = new Setting("TridentDelay", this, 0, 0, 20, true);
        addSetting(basic);
        addSetting(pingSpoof);
        addSetting(pingSpoofDelay);
        addSetting(grimAc);
        addSetting(tridentDelay);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        tick = (int) tridentDelay.getValDouble();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (pingSpoof.getValBoolean()) {
                for (final Iterator<Map.Entry<IPacket, Long>> iterator = packets.entrySet().iterator(); iterator.hasNext(); ) {
                    final Map.Entry<IPacket, Long> entry = iterator.next();

                    if (entry.getValue() < System.currentTimeMillis()) {
                        mc.player.connection.sendPacket(entry.getKey());
                        iterator.remove();
                    }
                }
            }
            if (grimAc.getValBoolean()) {
                if (tick <= 0) {
                    if (tridentDelay.getValDouble() != 0)
                        tick = (int) tridentDelay.getValDouble();

                    int tridentSlot = InventoryUtils.getSlot(Items.TRIDENT);
                    int oldSlot = mc.player.inventory.currentItem;

                    if (tridentSlot == -1) {
                        setState(false);
                        ClientUtils.addChatMessage("You need to have 3 or more riptide 3 tridents in your hotbar for GrimAC disabler!");
                        return;
                    }

                    mc.player.connection.sendPacket(new CHeldItemChangePacket(tridentSlot));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0, 0, 0), Direction.DOWN, mc.player.getPosition(), false)));
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(oldSlot));
                } else {
                    tick--;
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        try {
            if (basic.getValBoolean()) {
                if (event.getPacket() instanceof CConfirmTransactionPacket ||
                        event.getPacket() instanceof CAnimateHandPacket ||
                        event.getPacket() instanceof CEntityActionPacket ||
                        event.getPacket() instanceof CPlayerDiggingPacket ||
                        event.getPacket() instanceof CPlayerAbilitiesPacket ||
                        event.getPacket() instanceof CClientStatusPacket) {
                    event.setCanceled(true);
                }
            }
            if (pingSpoof.getValBoolean()) {
                if (event.getPacket() instanceof CConfirmTransactionPacket || event.getPacket() instanceof CKeepAlivePacket) {
                    packets.put(event.getPacket(), (long) (System.currentTimeMillis() + pingSpoofDelay.getValDouble()));
                    event.setCanceled(true);
                }
            }
        } catch (Exception ignored) {
        }
    }
}