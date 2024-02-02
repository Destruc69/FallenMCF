/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
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
            if (mc.player.isElytraFlying()) {
                float yaw = Minecraft.getInstance().player.rotationYaw;
                float pitch = Minecraft.getInstance().player.rotationPitch;

                if (mode.sval == "boost") {
                    if (Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown()) {
                        EntityUtils.setMotionX(mc.player.getMotion().x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.dval);
                        EntityUtils.setMotionZ(mc.player.getMotion().z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.dval);
                    }
                    if (Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown())
                        EntityUtils.setMotionY(mc.player.getMotion().y + Math.sin(Math.toRadians(pitch)) * upSpeed.dval);
                    if (Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown())
                        EntityUtils.setMotionY(mc.player.getMotion().y - Math.sin(Math.toRadians(pitch)) * downSpeed.dval);
                } else if (mode.sval == "control") {
                    if (mc.gameSettings.keyBindForward.isKeyDown() ||
                            mc.gameSettings.keyBindRight.isKeyDown() ||
                            mc.gameSettings.keyBindBack.isKeyDown() ||
                            mc.gameSettings.keyBindLeft.isKeyDown()) {
                        MathUtils.setSpeed(baseSpeed.dval);
                    } else {
                        EntityUtils.setMotionX(0);
                        EntityUtils.setMotionZ(0);
                    }
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        EntityUtils.setMotionY(upSpeed.dval);
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                        EntityUtils.setMotionY(-downSpeed.dval);
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        EntityUtils.setMotionY(0);
                    }
                } else if (mode.sval == "fallen") {
                    mc.player.setMotion(mc.player.getMotion().x * 1.005, mc.player.getMotion().y * 1.005, mc.player.getMotion().z * 1.005);
                }

                if (antiFireworkLag.bval) {
                    for (Entity entity : mc.world.getAllEntities()) {
                        if (entity instanceof FireworkRocketEntity) {
                            if (entity.ticksExisted > 0) {
                                mc.world.removeEntityFromWorld(entity.getEntityId());
                            }
                        }
                    }
                }
            } else {
                if (autoTakeOff.sval == "help") {
                    if (mc.player.getMotion().y < 0 && !mc.player.isOnGround()) {
                        if (!autoTakeOffSwitchBool) {
                            mc.player.startFallFlying();
                            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                            autoTakeOffSwitchBool = true;
                        }
                    } else {
                        autoTakeOffSwitchBool = false;
                    }
                } else if (autoTakeOff.sval == "auto") {
                    if (mc.player.isOnGround()) {
                        mc.player.jump();
                        autoTakeOffSwitchBool = false;
                    } else if (mc.player.getMotion().y < 0 && !mc.player.isOnGround()) {
                        if (!autoTakeOffSwitchBool) {
                            mc.player.startFallFlying();
                            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                            autoTakeOffSwitchBool = true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent event) {
        if (mode.sval == "bounce") {
            if (event.getPacket() instanceof CPlayerPacket) {
                mc.player.connection.sendPacket(new CPlayerPacket(false));
                event.setCanceled(true);
            }
        }
    }
}