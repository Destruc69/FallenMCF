/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.item.TridentItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class TridentFlight extends Module {
    public TridentFlight(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (mc.player.isSpinAttacking()) {
                mc.player.setMotion(mc.player.getMotion().x * 1.025, mc.player.getMotion().y, mc.player.getMotion().z * 1.025);
            }
        } catch (Exception ignored) {
        }
    }
}
