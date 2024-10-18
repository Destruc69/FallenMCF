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
import paul.fallen.utils.entity.EntityUtils;

import java.util.ArrayList;
import java.util.Arrays;

public final class GlideHack extends Module {
    private final Setting mode;

    public GlideHack(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        mode = new Setting("Mode", this, "normal",
                new ArrayList<>(Arrays.asList("normal", "aac", "vulcan")));
        addSetting(mode);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (mode.getValString() == "normal") {
                assert mc.player != null;
                if (mc.player.fallDistance > 1) {
                    EntityUtils.setMotionY(-mc.player.getMotion().y / -1.35);
                }
            }
            if (mode.getValString() == "aac") {
                assert mc.player != null;
                if (mc.player.getMotion().y > 0) {
                    EntityUtils.setMotionY(mc.player.getMotion().y / 0.9800000190734863);
                    EntityUtils.setMotionY(mc.player.getMotion().y + 0.03);
                    EntityUtils.setMotionY(mc.player.getMotion().y * 0.9800000190734863);
                    mc.player.jumpMovementFactor = 0.03625f;
                }
            }
            if (mode.getValString() == "vulcan") {
                assert mc.player != null;
                if (mc.player.getMotion().y <= -0.10) {
                    if (mc.player.ticksExisted % 2 == 0) {
                        EntityUtils.setMotionY(-0.1);
                    } else {
                        EntityUtils.setMotionY(-0.16);
                    }
                    mc.player.jumpMovementFactor = 0.0265f;
                }
            }
        } catch (Exception ignored) {
        }
    }
}