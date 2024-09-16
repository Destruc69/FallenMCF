/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

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
        Entity ridingEntity = mc.player != null ? mc.player.getRidingEntity() : null;
        if (ridingEntity == null || !ridingEntity.isAlive()) return;

        Vector3d motion = ridingEntity.getMotion();
        if (bypass.getValBoolean() && mc.player.ticksExisted % 5 == 0) {
            adjustMotion(motion);
        } else {
            adjustMotion(motion);
            if (bypass.getValBoolean() && mc.player.ticksExisted % 5 != 0 && mc.gameSettings.keyBindJump.isKeyDown()) {
                ridingEntity.setMotion(motion.x, motion.y / 2, motion.z);
            }
        }

        if (velocity.getValBoolean() && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            ridingEntity.setMotion(motion.x, 0.04, motion.z);
        }

        // Anti-kick mechanism
        if (!ridingEntity.isOnGround() && mc.player.ticksExisted % 2 == 0) {
            ridingEntity.setPosition(ridingEntity.getPosX() + 0.00000001, ridingEntity.getPosY() + 0.00000001, ridingEntity.getPosZ() - 0.00000001);
        } else if (!ridingEntity.isOnGround()) {
            ridingEntity.setPosition(ridingEntity.getPosX() - 0.00000001, ridingEntity.getPosY() - 0.00000001, ridingEntity.getPosZ() + 0.00000001);
        }

        // Improve collision handling
        ridingEntity.collidedHorizontally = true;
        ridingEntity.collidedVertically = true;
    }

    private void adjustMotion(Vector3d motion) {
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.getRidingEntity().setMotion(motion.x, motion.y + upSpeed.getValDouble(), motion.z);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.getRidingEntity().setMotion(motion.x, motion.y - downSpeed.getValDouble(), motion.z);
        }
    }

    @SubscribeEvent
    public void onExitVehicle(EntityMountEvent event) {
        if (event.isDismounting() && !event.getEntityBeingMounted().isOnGround()) {
            event.setCanceled(true);
        }
    }
}