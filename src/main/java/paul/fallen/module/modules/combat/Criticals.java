/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

public final class Criticals extends Module {

    private final Setting neww;

    public Criticals(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        neww = new Setting("New", this, false);
        addSetting(neww);
    }

    public void doCrits() {
        if (neww.getValBoolean()) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.05000000074505806, mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.012511000037193298, mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
        } else {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.1625, mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 4.0E-6, mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 1.0E-6, mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
            mc.player.connection.sendPacket(new CPlayerPacket());
        }
    }

    @SubscribeEvent
    public void onPacketOut(PacketEvent event) {
        if (event.getPacket() instanceof CUseEntityPacket) {
            CUseEntityPacket cPacketUseEntity = (CUseEntityPacket) event.getPacket();

            if (cPacketUseEntity.getAction().equals(CUseEntityPacket.Action.ATTACK)) {
                assert mc.world != null;
                if (cPacketUseEntity.getEntityFromWorld(mc.world) != null) {
                    assert mc.player != null;
                    if (mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                        doCrits();
                    }
                }
            }
        }
    }
}
