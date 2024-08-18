/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.utils.render.RenderUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Disabler extends Module {

    private final ConcurrentHashMap<CPlayerPacket, Long> packets = new ConcurrentHashMap<>();
    private CPlayerPacket sentPacket = null;

    public Disabler(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            long currentTime = System.currentTimeMillis();
            Iterator<Map.Entry<CPlayerPacket, Long>> iterator = packets.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<CPlayerPacket, Long> entry = iterator.next();
                if (currentTime >= entry.getValue()) {
                    mc.player.connection.sendPacket(entry.getKey());
                    sentPacket = entry.getKey();
                    iterator.remove(); // Remove the packet from the map once sent
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CPlayerPacket) {
            CPlayerPacket packet = (CPlayerPacket) event.getPacket();
            packets.put(packet, System.currentTimeMillis() + 1000);
            //event.setCanceled(true); // Cancel the packet from being processed immediately
            event.setPacket(new CPlayerPacket());
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (sentPacket != null) {
            RenderUtils.drawOutlinedBox(
                    new BlockPos(sentPacket.getX(0), sentPacket.getY(0), sentPacket.getZ(0)),
                    0, 1, 0, event
            );
        }
    }
}