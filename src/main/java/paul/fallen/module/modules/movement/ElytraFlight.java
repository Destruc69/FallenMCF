/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.client.MathUtils;
import paul.fallen.utils.entity.EntityUtils;

import java.util.ArrayList;
import java.util.Arrays;

public final class ElytraFlight extends Module {

    private final Setting mode;
    private final Setting upSpeed;
    private final Setting baseSpeed;
    private final Setting downSpeed;
    private final Setting autoTakeOff;
    private final Setting antiFireworkLag;
    private boolean autoTakeOffSwitchBool;

    public ElytraFlight(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", "Mode", this, "boost", new ArrayList<>(Arrays.asList("boost", "control", "bounce", "fallen")));
        upSpeed = new Setting("up-speed", "Up-Speed", this, 0.05F, (float) 0.005, 10.0F);
        baseSpeed = new Setting("base-speed", "Base-Speed", this, 0.05F, (float) 0.005, 10.0F);
        downSpeed = new Setting("down-speed", "Down-Speed", this, 0.05F, (float) 0.005, 10.0F);
        autoTakeOff = new Setting("AutoTakeOff", "AutoTakeOff", this, "help", new ArrayList<>(Arrays.asList("help", "auto", "off")));
        antiFireworkLag = new Setting("AntiFireworkLag", this, false);
        addSetting(mode);
        addSetting(upSpeed);
        addSetting(baseSpeed);
        addSetting(downSpeed);
        addSetting(autoTakeOff);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (mc.player.flyDist > 0) {
                float yaw = Minecraft.getInstance().player.yya;
                float pitch = Minecraft.getInstance().player.yHeadRot;

                if (mode.sval == "boost") {
                    if (Minecraft.getInstance().options.keyUp.isDown()) {
                        EntityUtils.setMotionX(mc.player.getDeltaMovement().x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.dval);
                        EntityUtils.setMotionZ(mc.player.getDeltaMovement().z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.dval);
                    }
                    if (Minecraft.getInstance().options.keyUp.isDown())
                        EntityUtils.setMotionY(mc.player.getDeltaMovement().y + Math.sin(Math.toRadians(pitch)) * upSpeed.dval);
                    if (Minecraft.getInstance().options.keyShift.isDown())
                        EntityUtils.setMotionY(mc.player.getDeltaMovement().y - Math.sin(Math.toRadians(pitch)) * downSpeed.dval);
                } else if (mode.sval == "control") {
                    if (mc.options.keyUp.isDown() ||
                            mc.options.keyRight.isDown() ||
                            mc.options.keyDown.isDown() ||
                            mc.options.keyLeft.isDown()) {
                        MathUtils.setSpeed(baseSpeed.dval);
                    } else {
                        EntityUtils.setMotionX(0);
                        EntityUtils.setMotionZ(0);
                    }
                    if (mc.options.keyJump.isDown() && !mc.options.keyShift.isDown()) {
                        EntityUtils.setMotionY(upSpeed.dval);
                    } else if (!mc.options.keyJump.isDown() && mc.options.keyShift.isDown()) {
                        EntityUtils.setMotionY(-downSpeed.dval);
                    } else if (!mc.options.keyJump.isDown() && !mc.options.keyShift.isDown()) {
                        EntityUtils.setMotionY(0);
                    }
                } else if (mode.sval == "fallen") {
                    mc.player.setDeltaMovement(mc.player.getDeltaMovement().x * 1.005, mc.player.getDeltaMovement().y * 1.005, mc.player.getDeltaMovement().z * 1.005);
                }

                if (antiFireworkLag.bval) {
                    for (Entity entity : mc.level.getPartEntities()) {
                        if (entity instanceof FireworkRocketEntity) {
                            if (entity.tickCount > 0) {
                                mc.level.removeEntity(entity.getId(), Entity.RemovalReason.KILLED);
                            }
                        }
                    }
                }
            } else {
                if (autoTakeOff.sval == "help") {
                    if (mc.player.getDeltaMovement().y < 0 && !mc.player.onGround()) {
                        if (!autoTakeOffSwitchBool) {
                            mc.player.startFallFlying();
                            autoTakeOffSwitchBool = true;
                        }
                    } else {
                        autoTakeOffSwitchBool = false;
                    }
                } else if (autoTakeOff.sval == "auto") {
                    if (mc.player.onGround()) {
                        mc.player.jumpFromGround();
                        autoTakeOffSwitchBool = false;
                    } else if (mc.player.getDeltaMovement().y < 0 && !mc.player.onGround()) {
                        if (!autoTakeOffSwitchBool) {
                            mc.player.startFallFlying();
                            autoTakeOffSwitchBool = true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}