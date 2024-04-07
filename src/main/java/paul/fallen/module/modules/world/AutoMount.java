/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public final class AutoMount extends Module {

    Setting maxDistance;
    Setting delay;

    public AutoMount(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        maxDistance = new Setting("MaxDistance", this, 4, 2, 6, true);
        delay = new Setting("Delay", this, 10, 2, 10, true);
        addSetting(maxDistance);
        addSetting(delay);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.player != null;
            if (mc.player.getRidingEntity() == null) {
                assert mc.world != null;
                for (Entity entity : mc.world.getAllEntities()) {
                    if (entity != null && entity != mc.player && entity.isAlive()) {
                        if (mc.player.getDistance(entity) <= maxDistance.getValDouble()) {
                            if (mc.player.ticksExisted % delay.getValDouble() == 0) {
                                assert mc.playerController != null;
                                mc.playerController.interactWithEntity(mc.player, entity, Hand.MAIN_HAND);
                                mc.player.swingArm(Hand.MAIN_HAND);
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}