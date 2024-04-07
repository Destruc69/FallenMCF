/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

import java.util.Objects;

public final class EntityFlight extends Module {

    private final Setting upSpeed;
    private final Setting downSpeed;
    private final Setting bypass;
    private final Setting velocity;

    public EntityFlight(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        upSpeed = new Setting("UpSpeed", this, 0.1f, 0.05f, 10, false);
        downSpeed = new Setting("DownSpeed", this, 0.1f, 0.05f, 10, false);
        bypass = new Setting("Bypass", this, false);
        velocity = new Setting("Velocity", this, false);
        addSetting(upSpeed);
        addSetting(downSpeed);
        addSetting(bypass);
        addSetting(velocity);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        try {
            assert mc.player != null;
            if (mc.player.getRidingEntity() != null) {
                if (Objects.requireNonNull(mc.player.getRidingEntity()).isAlive()) {
                    if (!bypass.getValBoolean()) {
                        assert mc.player.getRidingEntity() != null;
                        if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            Objects.requireNonNull(mc.player.getRidingEntity()).setMotion(Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().x, Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().y + upSpeed.getValDouble(), Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().z);
                        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                            Objects.requireNonNull(mc.player.getRidingEntity()).setMotion(Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().x, Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().y - downSpeed.getValDouble(), Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().z);
                        }
                    } else {
                        if (mc.player.ticksExisted % 5 == 0) {
                            assert mc.player.getRidingEntity() != null;
                            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                                Objects.requireNonNull(mc.player.getRidingEntity()).setMotion(Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().x, Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().y + upSpeed.getValDouble(), Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().z);
                            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                                Objects.requireNonNull(mc.player.getRidingEntity()).setMotion(Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().x, Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().y - downSpeed.getValDouble(), Objects.requireNonNull(mc.player.getRidingEntity()).getMotion().z);
                            }
                        } else {
                            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                                mc.player.getRidingEntity().setMotion(mc.player.getRidingEntity().getMotion().x, mc.player.getRidingEntity().getMotion().y / 2, mc.player.getMotion().z);
                            }
                        }
                    }
                    if (velocity.getValBoolean()) {
                        if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                            mc.player.getRidingEntity().setMotion(mc.player.getRidingEntity().getMotion().x, 0.04, mc.player.getRidingEntity().getMotion().z);
                        }
                    }

                    //lil anti kick
                    if (!(mc.player.getRidingEntity().isOnGround())) {
                        if (mc.player.ticksExisted % 2 == 0) {
                            mc.player.getRidingEntity().setPosition(mc.player.getRidingEntity().getPosX() + 0.00000001, mc.player.getRidingEntity().getPosY() + 0.00000001, mc.player.getRidingEntity().getPosZ() - 0.00000001);
                        } else {
                            mc.player.getRidingEntity().setPosition(mc.player.getRidingEntity().getPosX() - 0.00000001, mc.player.getRidingEntity().getPosY() - 0.00000001, mc.player.getRidingEntity().getPosZ() + 0.00000001);
                        }
                    }
                }
                //I find you can fly better when collided
                mc.player.getRidingEntity().collidedHorizontally = true;
                mc.player.getRidingEntity().collidedVertically = true;
            }
        } catch (Exception e) {
        }
    }

    @SubscribeEvent
    public void onExitVehicle(EntityMountEvent event) {
        if (event.isDismounting()) {
            if (!(event.getEntityBeingMounted().isOnGround())) {
                event.setCanceled(true);
            }
        }
    }
}
