/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public final class NoFall extends Module {

    Setting alternative;

    public NoFall(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        alternative = new Setting("Alternative", this, false);
        addSetting(alternative);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (!alternative.bval) {
                if (mc.player.fallDistance > 3) {
                    mc.player.connection.sendPacket(new CPlayerPacket(true));
                }
            } else {
                if (mc.player.fallDistance > 3) {
                    mc.player.setOnGround(true);
                }
            }
        } catch (Exception ignored) {
        }
    }
}