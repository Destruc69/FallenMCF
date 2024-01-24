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

    Setting damage;

    public NoFall(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        setState(true);

        damage = new Setting("Damage", this, false);
        addSetting(damage);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.player != null;
            if (mc.player.fallDistance > 3) {
                if (!damage.bval) {
                    mc.player.connection.sendPacket(new CPlayerPacket(true));
                } else {
                    mc.player.setOnGround(true);
                    mc.player.isAirBorne = false;
                    mc.player.collidedHorizontally = false;
                    mc.player.collidedVertically = true;
                }
            }
        } catch (Exception ignored) {
        }
    }
}


