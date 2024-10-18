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
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.RotationUtils;

public final class Killaura extends Module {

    private final Setting delay;
    private final Setting rotate;
    private final Setting distance;

    private long lastAttackTime = 0L;

    public Killaura(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        delay = new Setting("Delay", this, 10, 0, 20, true);
        addSetting(delay);

        rotate = new Setting("Rotate", this, false);
        addSetting(rotate);

        distance = new Setting("Distance", this, 4, 1, 6, true);
        addSetting(distance);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Entity targetEntity = findClosestEntity();
        if (targetEntity == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < delay.getValDouble() * 50) return; // Convert delay to milliseconds

        lastAttackTime = currentTime;

        Vector3d entityCenter = targetEntity.getBoundingBox().getCenter();
        float[] rotations = RotationUtils.getYawAndPitch(entityCenter.add(
                mc.player.ticksExisted % 2 == 0 ? Math.random() * 2 : -(Math.random() * 2),
                mc.player.ticksExisted % 2 == 0 ? Math.random() * 2 : -(Math.random() * 2),
                mc.player.ticksExisted % 2 == 0 ? Math.random() * 2 : -(Math.random() * 2)
        ));

        if (rotate.getValBoolean()) {
            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rotations[0], rotations[1], mc.player.isOnGround()));
        }

        mc.playerController.attackEntity(mc.player, targetEntity);
        mc.player.swingArm(Hand.MAIN_HAND);
    }

    private Entity findClosestEntity() {
        Entity closestEntity = null;
        double closestDistance = distance.getValDouble() * distance.getValDouble(); // Square distance for efficiency

        if (mc.world == null || mc.player == null) return null;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player) {
                double distanceToEntity = mc.player.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                if (distanceToEntity < closestDistance) {
                    closestDistance = distanceToEntity;
                    closestEntity = entity;
                }
            }
        }
        return closestEntity;
    }
}