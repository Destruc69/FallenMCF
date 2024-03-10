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
import paul.fallen.module.Module;

public final class YawLock extends Module {

    public double yawthis;

    public YawLock(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();
            assert mc.player != null;
            yawthis = mc.player.rotationYaw;
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.player != null;
            mc.player.rotationYaw = (float) yawthis;
        } catch (Exception ignored) {
        }
    }
}