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
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public final class Criticals extends Module {

    private final Setting mode;

    public Criticals(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("mode", "Mode", this, "bypass", new ArrayList<>(Arrays.asList("bypass", "basic")));
        addSetting(mode);
    }

    public void doCrits() {
        double off = 0.0626;
        assert mc.player != null;
        double x = mc.player.getPosX();
        double y = mc.player.getPosY();
        double z = mc.player.getPosZ();
        if (mode.sval == "bypass") {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + off, z, false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + off + 0.00000000001, z, false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));

        } else if (mode.sval == "basic") {
            mc.player.connection.sendPacket(new CPlayerPacket(false));
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
