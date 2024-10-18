/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

public final class AntiHunger extends Module {

    public AntiHunger(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onPacketOut(PacketEvent event) {
        if (event.getPacket() instanceof CEntityActionPacket) {
            CEntityActionPacket cPacketEntityAction = (CEntityActionPacket) event.getPacket();
            if (cPacketEntityAction.getAction().equals(CEntityActionPacket.Action.START_SPRINTING) || cPacketEntityAction.getAction().equals(CEntityActionPacket.Action.STOP_SPRINTING)) {
                event.setCanceled(true);
            }
        }
    }
}
