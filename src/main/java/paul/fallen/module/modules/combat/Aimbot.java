/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.RotationUtils;

public final class Aimbot extends Module {

    public Aimbot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        setState(true);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.world != null;
            for (Entity entity : mc.world.getAllEntities()) {
                assert entity != null;
                if (entity != mc.player) {
                    assert mc.player != null;
                    if (mc.player.getDistance(entity) <= 4) {
                        RotationUtils.rotateTo(new Vector3d(entity.lastTickPosX + 0.5, entity.lastTickPosY + entity.getEyeHeight(), entity.lastTickPosZ + 0.5));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}