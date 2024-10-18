/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.client.MathUtils;
import paul.fallen.utils.entity.EntityUtils;

public final class EntitySpeed extends Module {

    private final Setting speed;
    private final Setting bypass;

    public EntitySpeed(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
        speed = new Setting("Speed", this, 0.1f, 0.05f, 10, false);
        bypass = new Setting("Bypass", this, false);
        addSetting(speed);
        addSetting(bypass);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        try {
            if (mc.player == null || mc.player.getRidingEntity() == null) return;

            Entity ridingEntity = mc.player.getRidingEntity();
            if (!ridingEntity.isAlive()) return;

            double[] dir = MathUtils.directionSpeed(speed.getValDouble());

            if (bypass.getValBoolean()) {
                if (mc.player.ticksExisted % 5 == 0) {
                    dir[0] -= Math.random() * 0.02;
                    dir[1] -= Math.random() * 0.02;
                }
            }

            EntityUtils.setEMotionX(ridingEntity, dir[0]);
            EntityUtils.setEMotionZ(ridingEntity, dir[1]);

            if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.ticksExisted % 20 == 0) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_RIDING_JUMP, 100));
            }

            ridingEntity.rotationYaw = mc.player.rotationYaw;
            ridingEntity.rotationPitch = mc.player.rotationPitch;
        } catch (Exception ignored) {
        }
    }
}