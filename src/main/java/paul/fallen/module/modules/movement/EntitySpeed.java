/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.client.MathUtils;
import paul.fallen.utils.entity.EntityUtils;

import java.util.Objects;

public final class EntitySpeed extends Module {

    private final Setting speed;
    private final Setting bypass;

    public EntitySpeed(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        speed = new Setting("Speed", "Speed",  this, 0.1f, 0.05f, 10);
        bypass = new Setting("Bypass", this, false);
        addSetting(speed);
        addSetting(bypass);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        try {
            assert mc.player != null;
            if (mc.player.getRidingEntity() != null) {
                if (Objects.requireNonNull(mc.player.getRidingEntity()).isAlive()) {
                    if (!bypass.bval) {
                        double[] dir = MathUtils.directionSpeed(speed.dval);
                        assert mc.player.getRidingEntity() != null;
                        EntityUtils.setEMotionX(mc.player.getRidingEntity(), dir[0]);
                        EntityUtils.setEMotionZ(mc.player.getRidingEntity(), dir[1]);
                    } else {
                        if (mc.player.ticksExisted % 5 == 0) {
                            double[] dir = MathUtils.directionSpeed(speed.dval - Math.random() * 0.02);
                            assert mc.player.getRidingEntity() != null;
                            EntityUtils.setEMotionX(mc.player.getRidingEntity(), dir[0]);
                            EntityUtils.setEMotionZ(mc.player.getRidingEntity(), dir[1]);
                        } else {
                            EntityUtils.setEMotionX(mc.player.getRidingEntity(), mc.player.getRidingEntity().getMotion().x / 2);
                            EntityUtils.setEMotionZ(mc.player.getRidingEntity(), mc.player.getRidingEntity().getMotion().z / 2);
                        }
                    }

                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (mc.player.ticksExisted % 20 == 0) {
                            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_RIDING_JUMP, 100));
                        }
                    }

                    Objects.requireNonNull(mc.player.getRidingEntity()).rotationYaw = mc.player.rotationYaw;
                    Objects.requireNonNull(mc.player.getRidingEntity()).rotationPitch = mc.player.rotationPitch;
                }
            }
        } catch (Exception ignored) {
        }
    }
}
