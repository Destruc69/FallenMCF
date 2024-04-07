/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.entity.Entity;
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
    private final Setting ncpInterpolate;

    public ElytraFlight(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        ncp = new Setting("NCP", this, false);
        upSpeed = new Setting("Up-Speed", this, 0.05F, (float) 0.005, 10.0F, false);
        baseSpeed = new Setting("Base-Speed", this, 0.05F, 0.02F, 10.0F, false);
        downSpeed = new Setting( "Down-Speed", this, 0.05F, 0.002F, 10.0F, false);
        ncpInterpolate = new Setting("NCP-Interpolate", this, false);
        addSetting(ncp);
        addSetting(upSpeed);
        addSetting(baseSpeed);
        addSetting(downSpeed);
        addSetting(ncpInterpolate);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
             if (!ncp.getValBoolean()) {
                 if (mc.gameSettings.keyBindJump.isKeyDown()) {
                     mc.player.setMotion(mc.player.getMotion().x, upSpeed.getValDouble(), mc.player.getMotion().z);
                 } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                     mc.player.setMotion(mc.player.getMotion().x, -downSpeed.getValDouble(), mc.player.getMotion().z);
                 } else {
                     mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
                 }
                 if (mc.gameSettings.keyBindForward.isKeyDown() ||
                 mc.gameSettings.keyBindRight.isKeyDown() ||
                 mc.gameSettings.keyBindBack.isKeyDown() ||
                 mc.gameSettings.keyBindLeft.isKeyDown()) {
                     MathUtils.setSpeed(baseSpeed.getValDouble());
                 } else {
                     mc.player.setMotion(0, mc.player.getMotion().y, 0);
                 }
             } else {
                 if (!ncpInterpolate.getValBoolean()) {
                     Vector3d vector3d = mc.player.getMotion();

                     if (mc.gameSettings.keyBindJump.isKeyDown()) {
                         vector3d = mc.player.getMotion().add(0, upSpeed.getValDouble(), 0);
                     } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                         vector3d = mc.player.getMotion().add(0, -downSpeed.getValDouble(), 0);
                     }
                     if (mc.gameSettings.keyBindForward.isKeyDown()) {
                         double[] dir = MathUtils.directionSpeed(baseSpeed.getValDouble());
                         vector3d = mc.player.getMotion().add(dir[0], 0, dir[1]);
                     }

                     Vector3d vector3d1 = mc.player.getLookVec();
                     double d0 = 0.08D;
                     float f = mc.player.rotationPitch * ((float) Math.PI / 180F);
                     double d1 = Math.sqrt(vector3d1.x * vector3d1.x + vector3d1.z * vector3d1.z);
                     double d3 = Math.sqrt(Entity.horizontalMag(vector3d));
                     double d4 = vector3d1.length();
                     float f1 = MathHelper.cos(f);
                     f1 = (float) ((double) f1 * (double) f1 * Math.min(1.0D, d4 / 0.4D));
                     vector3d = mc.player.getMotion().add(0.0D, d0 * (-1.0D + (double) f1 * 0.75D), 0.0D);
                     if (vector3d.y < 0.0D && d1 > 0.0D) {
                         double d5 = vector3d.y * -0.1D * (double) f1;
                         vector3d = vector3d.add(vector3d1.x * d5 / d1, d5, vector3d1.z * d5 / d1);
                     }

                     if (f < 0.0F && d1 > 0.0D) {
                         double d9 = d3 * (double) (-MathHelper.sin(f)) * 0.04D;
                         vector3d = vector3d.add(-vector3d1.x * d9 / d1, d9 * 3.2D, -vector3d1.z * d9 / d1);
                     }

                     if (d1 > 0.0D) {
                         vector3d = vector3d.add((vector3d1.x / d1 * d3 - vector3d.x) * 0.1D, 0.0D, (vector3d1.z / d1 * d3 - vector3d.z) * 0.1D);
                     }

                     mc.player.setMotion(vector3d.mul((double) 0.99F, (double) 0.98F, (double) 0.99F));
                 } else {
                     Vector3d currentMotion = mc.player.getMotion();
                     Vector3d interpolatedMotion = new Vector3d(currentMotion.x, currentMotion.y, currentMotion.z);

                     // Interpolate upward motion
                     if (mc.gameSettings.keyBindJump.isKeyDown()) {
                         Vector3d upMotion = new Vector3d(0, upSpeed.getValDouble(), 0);
                         interpolatedMotion = interpolateMotion(interpolatedMotion, upMotion);
                     }
                     // Interpolate downward motion
                     else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                         Vector3d downMotion = new Vector3d(0, -downSpeed.getValDouble(), 0);
                         interpolatedMotion = interpolateMotion(interpolatedMotion, downMotion);
                     }

                     // Interpolate forward motion
                     if (mc.gameSettings.keyBindForward.isKeyDown()) {
                         double[] dir = MathUtils.directionSpeed(baseSpeed.getValDouble());
                         Vector3d forwardMotion = new Vector3d(dir[0], 0, dir[1]);
                         interpolatedMotion = interpolateMotion(interpolatedMotion, forwardMotion);
                     }

                     Vector3d lookVector = mc.player.getLookVec();
                     double acceleration = 0.08D;
                     float pitch = mc.player.rotationPitch * ((float) Math.PI / 180F);
                     double horizontalMotionMagnitude = Math.sqrt(Entity.horizontalMag(interpolatedMotion));
                     double lookVectorLength = lookVector.length();
                     float f1 = MathHelper.cos(pitch);
                     f1 = (float) ((double) f1 * (double) f1 * Math.min(1.0D, lookVectorLength / 0.4D));
                     interpolatedMotion = interpolatedMotion.add(0.0D, acceleration * (-1.0D + (double) f1 * 0.75D), 0.0D);

                     if (interpolatedMotion.y < 0.0D && lookVectorLength > 0.0D) {
                         double factor = interpolatedMotion.y * -0.1D * (double) f1;
                         interpolatedMotion = interpolatedMotion.add(lookVector.x * factor / lookVectorLength, factor, lookVector.z * factor / lookVectorLength);
                     }

                     if (pitch < 0.0F && lookVectorLength > 0.0D) {
                         double factor = horizontalMotionMagnitude * (double) (-MathHelper.sin(pitch)) * 0.04D;
                         interpolatedMotion = interpolatedMotion.add(-lookVector.x * factor / lookVectorLength, factor * 3.2D, -lookVector.z * factor / lookVectorLength);
                     }

                     if (lookVectorLength > 0.0D) {
                         interpolatedMotion = interpolatedMotion.add((lookVector.x / lookVectorLength * horizontalMotionMagnitude - interpolatedMotion.x) * 0.1D, 0.0D, (lookVector.z / lookVectorLength * horizontalMotionMagnitude - interpolatedMotion.z) * 0.1D);
                     }

                     mc.player.setMotion(interpolatedMotion.mul((double) 0.99F, (double) 0.98F, (double) 0.99F));
                 }
             }
        } catch (Exception ignored) {
        }
    }

    // Interpolation function
    private Vector3d interpolateMotion(Vector3d current, Vector3d target) {
        double interpolationFactor = 0.001; // Adjust as needed
        double interpolatedX = current.x + (target.x - current.x) * interpolationFactor;
        double interpolatedY = current.y + (target.y - current.y) * interpolationFactor;
        double interpolatedZ = current.z + (target.z - current.z) * interpolationFactor;
        return new Vector3d(interpolatedX, interpolatedY, interpolatedZ);
    }
}