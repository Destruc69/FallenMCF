/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public final class AntiAFK extends Module {
    private final Setting crazy;

    public AntiAFK(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
        crazy = new Setting("CrazyMode", this, false);
        addSetting(crazy);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.player != null;
            if (!crazy.getValBoolean()) {
                if (mc.player.ticksExisted % 5 == 0) {
                    mc.player.rotationYaw++;
                    if (mc.player.isOnGround()) {
                        mc.player.jump();
                    }
                }
            } else {
                mc.player.rotationYaw = mc.player.rotationYaw + Math.round(Math.random() * 90);
                mc.player.rotationPitch = Math.round(Math.random() * 90);
            }
        } catch (Exception ignored) {
        }
    }

}