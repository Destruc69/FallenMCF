/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.events.PacketReceiveEvent;
import paul.fallen.events.PacketSendEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public final class Disabler extends Module {

    Setting keepAlive;
    Setting cConfirmTransaction;
    Setting antiFlag;
    Setting sConfirmTransaction;

    public Disabler(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        keepAlive = new Setting("KeepAlive", this, false);
        cConfirmTransaction = new Setting("CPacketConfirmTransaction", this, false);
        antiFlag = new Setting("AntiFlag", this, false);
        sConfirmTransaction = new Setting("SPacketConfirmTransaction", this, false);
        addSetting(keepAlive);
        addSetting(cConfirmTransaction);
        addSetting(antiFlag);
        addSetting(cConfirmTransaction);
    }

    @SubscribeEvent
    public void onPacketOut(PacketSendEvent event) {
        if (keepAlive.bval) {
            if (event.getPacket() instanceof CKeepAlivePacket) {
                assert mc.player != null;
                mc.player.connection.sendPacket(new CKeepAlivePacket(Integer.MIN_VALUE + Math.round(Math.random() * 100)));
                event.setCanceled(true);
            }
        }
        if (cConfirmTransaction.bval) {
            if (event.getPacket() instanceof CConfirmTransactionPacket) {
                CConfirmTransactionPacket confirmTransaction = (CConfirmTransactionPacket) event.getPacket();
                assert mc.player != null;
                mc.player.connection.sendPacket(new CConfirmTransactionPacket(Integer.MAX_VALUE, confirmTransaction.getUid(), false));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPacketIn(PacketReceiveEvent event) {
        if (antiFlag.bval) {
            if (event.getPacket() instanceof SPlayerPositionLookPacket) {
                SPlayerPositionLookPacket sPacketPlayerPosLook = (SPlayerPositionLookPacket) event.getPacket();
                event.setPacket(new SPlayerPositionLookPacket(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY() - Integer.MAX_VALUE, sPacketPlayerPosLook.getZ(), sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), sPacketPlayerPosLook.getFlags(), sPacketPlayerPosLook.getTeleportId()));
            }
        }
        if (sConfirmTransaction.bval) {
            if (event.getPacket() instanceof SConfirmTransactionPacket) {
                SConfirmTransactionPacket sPacketConfirmTransaction = (SConfirmTransactionPacket) event.getPacket();
                if (sPacketConfirmTransaction.getActionNumber() < 0) {
                    event.setCanceled(true);
                }
            }
        }
    }
}