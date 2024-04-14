/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Disabler extends Module {

    private Setting pingSpoof;
    private Setting pingSpoofDelay;

    private final ConcurrentHashMap<IPacket, Long> packets = new ConcurrentHashMap<>();

    public Disabler(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        pingSpoof = new Setting("PingSpoof", this, false);
        pingSpoofDelay = new Setting("PingSpoofDelay", this, 1000, 200, 5000, true);
        addSetting(pingSpoof);
        addSetting(pingSpoofDelay);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            for (final Iterator<Map.Entry<IPacket, Long>> iterator = packets.entrySet().iterator(); iterator.hasNext(); ) {
                final Map.Entry<IPacket, Long> entry = iterator.next();

                if (entry.getValue() < System.currentTimeMillis()) {
                    mc.player.connection.sendPacket(entry.getKey());
                    iterator.remove();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        try {
            if (!pingSpoof.getValBoolean()) {
                if (event.getPacket() instanceof CConfirmTransactionPacket ||
                        event.getPacket() instanceof CAnimateHandPacket ||
                        event.getPacket() instanceof CEntityActionPacket ||
                        event.getPacket() instanceof CPlayerDiggingPacket ||
                        event.getPacket() instanceof CPlayerAbilitiesPacket ||
                        event.getPacket() instanceof CClientStatusPacket) {
                    event.setCanceled(true);
                }
            } else {
                if (event.getPacket() instanceof CConfirmTransactionPacket || event.getPacket() instanceof CKeepAlivePacket) {
                    packets.put(event.getPacket(), (long) (System.currentTimeMillis() + pingSpoofDelay.getValDouble()));
                    event.setCanceled(true);
                }
            }
        } catch (Exception ignored) {
        }
    }
}