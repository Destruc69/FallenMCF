/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.setting.Setting;
import paul.fallen.utils.client.MathUtils;

public final class Disabler extends Module {

    private Setting posCancel;
    private Setting noPayload;
    private Setting transactionSpam;
    private Setting spectator;
    private Setting antiFlag;
    private Setting resourcePack;
    private Setting clientStatus;
    private Setting input;

    public Disabler(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        posCancel = new Setting("PosCancel", this, false);
        noPayload = new Setting("NoPayload", this, false);
        transactionSpam = new Setting("TransactionSpam", this, false);
        spectator = new Setting("Spectator", this, false);
        antiFlag = new Setting("AntiFlag", this, false);
        resourcePack = new Setting("ResourcePack", this, false);
        clientStatus = new Setting("ClientStatus", this, false);
        input = new Setting("Input", this, false);
        addSetting(input);
        addSetting(clientStatus);
        addSetting(resourcePack);
        addSetting(posCancel);
        addSetting(noPayload);
        addSetting(transactionSpam);
        addSetting(spectator);
        addSetting(antiFlag);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        try {
            if (antiFlag.bval) {
                if (event.getPacket() instanceof SPlayerPositionLookPacket) {
                    SPlayerPositionLookPacket sPacketPlayerPosLook = (SPlayerPositionLookPacket) event.getPacket();
                    event.setPacket(new SPlayerPositionLookPacket(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY() - Integer.MAX_VALUE, sPacketPlayerPosLook.getZ(), sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), sPacketPlayerPosLook.getFlags(), sPacketPlayerPosLook.getTeleportId()));
                }
            }
            if (noPayload.bval) {
                if (event.getPacket() instanceof CCustomPayloadPacket) {
                    event.setCanceled(true);
                }
            }
            if (posCancel.bval) {
                if (event.getPacket() instanceof CPlayerPacket && mc.player.ticksExisted % 3 == 0) {
                    event.setCanceled(true);
                }
            }
            if (input.bval) {
                mc.player.connection.sendPacket(new CInputPacket());
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (spectator.bval) {
                mc.player.connection.sendPacket(new CSpectatePacket(mc.player.getUniqueID()));
            }
            if (transactionSpam.bval) {
                mc.player.connection.sendPacket(new CConfirmTransactionPacket(0, (short) MathUtils.generateRandomNumber(-32767, 32767), false));
            }
            if (resourcePack.bval) {
                mc.player.connection.sendPacket(new CResourcePackStatusPacket(CResourcePackStatusPacket.Action.ACCEPTED));
            }
            if (clientStatus.bval) {
                mc.player.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
            }
        } catch (Exception ignored) {
        }
    }
}