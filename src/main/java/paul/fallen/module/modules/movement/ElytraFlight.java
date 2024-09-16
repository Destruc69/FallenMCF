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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.client.MathUtils;

public final class ElytraFlight extends Module {

    private final Setting ncp;
    private final Setting upSpeed;
    private final Setting baseSpeed;
    private final Setting downSpeed;
    private final Setting easyTakeoff;

    private boolean isTakingOff = false;

    public ElytraFlight(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        ncp = new Setting("NCP", this, false);
        upSpeed = new Setting("Up-Speed", this, 0.05F, 0.005F, 10, false);
        baseSpeed = new Setting("Base-Speed", this, 0.05, 0.02, 10, false);
        downSpeed = new Setting("Down-Speed", this, 0.0F, 0.002F, 10, false);
        easyTakeoff = new Setting("EasyTakeOff", this, false);

        addSetting(ncp);
        addSetting(upSpeed);
        addSetting(baseSpeed);
        addSetting(downSpeed);
        addSetting(easyTakeoff);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.player == null || !mc.player.isElytraFlying()) return;

        if (!ncp.getValBoolean()) {
            handleNonNCPFlight();
        } else {
            handleNCPFlight();
        }

        handleEasyTakeoff();
    }

    private void handleNonNCPFlight() {
        Vector3d motion = mc.player.getMotion();
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.setMotion(motion.x, upSpeed.getValDouble(), motion.z);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.setMotion(motion.x, -downSpeed.getValDouble(), motion.z);
        } else {
            mc.player.setMotion(motion.x, 0, motion.z);
        }

        if (isAnyMovementKeyDown()) {
            MathUtils.setSpeed(baseSpeed.getValDouble());
        } else {
            mc.player.setMotion(0, mc.player.getMotion().y, 0);
        }
    }

    private void handleNCPFlight() {
        Vector3d motion = mc.player.getMotion();
        Vector3d lookVec = mc.player.getLookVec();
        double speed = baseSpeed.getValDouble();
        float pitchRadians = (float) Math.toRadians(mc.player.rotationPitch);
        double horizontalDistance = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);
        double motionLength = Entity.horizontalMag(motion);
        double lookVecLength = lookVec.length();

        // Calculate vertical adjustment
        double verticalAdjustment = 0.08 * (-1.0 + Math.pow(Math.min(1.0, lookVecLength / 0.4), 2)) * 0.75;
        motion = motion.add(0, verticalAdjustment, 0);

        if (motion.y < 0 && horizontalDistance > 0) {
            double horizontalAdjustment = motion.y * -0.1 * (MathHelper.cos(pitchRadians));
            motion = motion.add(lookVec.x * horizontalAdjustment / horizontalDistance, horizontalAdjustment, lookVec.z * horizontalAdjustment / horizontalDistance);
        }

        if (pitchRadians < 0 && horizontalDistance > 0) {
            double pitchAdjustment = horizontalDistance * -MathHelper.sin(pitchRadians) * 0.04;
            motion = motion.add(-lookVec.x * pitchAdjustment / horizontalDistance, pitchAdjustment * 3.2, -lookVec.z * pitchAdjustment / horizontalDistance);
        }

        if (horizontalDistance > 0) {
            motion = motion.add((lookVec.x / horizontalDistance * motionLength - motion.x) * 0.1, 0, (lookVec.z / horizontalDistance * motionLength - motion.z) * 0.1);
        }

        mc.player.setMotion(motion.mul(0.99F, 0.98F, 0.99F));
    }

    private void handleEasyTakeoff() {
        if (easyTakeoff.getValBoolean() && mc.world.getBlockState(mc.player.getPosition().down()).getBlock().isAir(mc.world.getBlockState(mc.player.getPosition().down()), mc.world, mc.player.getPosition().down()) && mc.player.getMotion().y < 0) {
            if (!isTakingOff) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                mc.player.startFallFlying();
                isTakingOff = true;
            }
        } else {
            isTakingOff = false;
        }
    }

    private boolean isAnyMovementKeyDown() {
        return mc.gameSettings.keyBindForward.isKeyDown() ||
                mc.gameSettings.keyBindRight.isKeyDown() ||
                mc.gameSettings.keyBindBack.isKeyDown() ||
                mc.gameSettings.keyBindLeft.isKeyDown();
    }
}