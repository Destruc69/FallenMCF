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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

import java.util.ArrayList;

public final class Disabler extends Module {

    private Setting pingSpoof;

    private ArrayList<IPacket> packets = new ArrayList<>();

    public Disabler(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        pingSpoof = new Setting("PingSpoof", this, false);
        addSetting(pingSpoof);
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
                if (event.getPacket() instanceof CKeepAlivePacket) {
                    CKeepAlivePacket cKeepAlivePacket = (CKeepAlivePacket) event.getPacket();
                    event.setCanceled(true);
                    mc.player.connection.sendPacket(new CKeepAlivePacket(cKeepAlivePacket.getKey() + getFps() / 2));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private int getFps() {
        String debugString = mc.debug;
        String[] splits = debugString.split(" ");
        return Integer.parseInt(splits[0]);
    }
}