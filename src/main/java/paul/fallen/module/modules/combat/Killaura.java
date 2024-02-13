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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.RotationUtils;

public final class Killaura extends Module {

    private final Setting distancee;

    public Killaura(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        distancee = new Setting("Distance", this, 4, 1, 6);
        addSetting(distancee);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        try {
            Entity entity = findClosestEntity();
            if (entity != null) {
                int[] rot = RotationUtils.getYawAndPitch(new Vector3d(entity.lastTickPosX + 0.5, entity.lastTickPosY + 0.5, entity.lastTickPosZ + 0.5));
                assert mc.player != null;
                if (mc.player.ticksExisted % 10 == 0) {
                    mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], mc.player.isOnGround()));
                    assert mc.playerController != null;
                    mc.playerController.attackEntity(mc.player, entity);
                    mc.player.swingArm(Hand.MAIN_HAND);
                }
            }
        } catch (Exception ignored) {
        }
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
            if (mc.player.getDistance(closestEntity) <= distancee.dval) {
                return closestEntity;
            }
        }
        return null; // Moved return statement out of the if condition
    }
}