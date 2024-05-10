/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.RotationUtils;

public final class Killaura extends Module {

    private final Setting strictRotation;
    private float currentStrictYaw = 0;
    private float currentStrictPitch = 0;
    private final Setting distancee;

    private boolean a = true;

    public Killaura(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        strictRotation = new Setting("StrictRotation", this, false);
        distancee = new Setting("Distance", this, 4, 1, 6, true);
        addSetting(strictRotation);
        addSetting(distancee);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();
            currentStrictYaw = mc.player.rotationYaw;
            currentStrictPitch = mc.player.rotationPitch;
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        try {
            Entity entity = findClosestEntity();
            if (entity != null) {
                float[] rot = RotationUtils.getYawAndPitch(entity.getBoundingBox().getCenter());
                assert mc.player != null;
                if (!strictRotation.getValBoolean()) {
                    if (mc.player.ticksExisted % 10 + Math.round(Math.random() * 2) - Math.round(Math.random() * 2) == 0) {
                        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], mc.player.isOnGround()));

                        if (a) {
                            mc.playerController.attackEntity(mc.player, entity);
                            mc.player.swingArm(Hand.MAIN_HAND);
                            a = false;
                        }

                        mc.player.rotationYawHead = rot[0];
                        mc.player.renderYawOffset = rot[0];
                    } else {
                        a = true;
                    }
                } else if (strictRotation.getValBoolean()) {
                    interpolateRotation(rot[0], rot[1]);
                    mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(currentStrictYaw, currentStrictPitch, mc.player.isOnGround()));

                    mc.player.rotationYawHead = currentStrictYaw;
                    mc.player.renderYawOffset = currentStrictYaw;

                    if (Math.round(currentStrictYaw) == Math.round(rot[0]) && Math.round(currentStrictPitch) == Math.round(rot[1])) {
                        if (mc.player.ticksExisted % 10 + Math.round(Math.random() * 2) - Math.round(Math.random() * 2) == 0) {

                            if (a) {
                                mc.playerController.attackEntity(mc.player, entity);
                                mc.player.swingArm(Hand.MAIN_HAND);
                                a = false;
                            }
                        } else {
                            a = true;
                        }
                    }
                }
            } else {
                currentStrictYaw = mc.player.rotationYaw;
                currentStrictPitch = mc.player.rotationPitch;
            }
        } catch (Exception ignored) {
        }
    }

    private void interpolateRotation(float targetYaw, float targetPitch) {
        float diffYaw = MathHelper.wrapDegrees(targetYaw - currentStrictYaw);
        float diffPitch = targetPitch - currentStrictPitch;
        float stepYaw = Math.signum(diffYaw) * Math.min(5.0f, Math.abs(diffYaw));
        float stepPitch = Math.signum(diffPitch) * Math.min(5.0f, Math.abs(diffPitch));

        currentStrictYaw = MathHelper.wrapDegrees(currentStrictYaw + stepYaw);
        currentStrictPitch += stepPitch;
    }

    private Entity findClosestEntity() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        assert mc.world != null;
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity != null && entity != mc.player && entity instanceof LivingEntity) {
                assert mc.player != null;
                double distance = mc.player.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                if (distance < closestDistance) { // Fixed variable name
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        if (closestEntity != null && mc.player != null) { // Removed assertion for closestEntity not being null
            if (mc.player.getDistance(closestEntity) <= distancee.getValDouble()) {
                return closestEntity;
            }
        }
        return null; // Moved return statement out of the if condition
    }
}