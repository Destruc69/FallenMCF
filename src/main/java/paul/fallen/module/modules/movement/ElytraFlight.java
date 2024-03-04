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

        mode = new Setting("Mode", "Mode", this, "boost", new ArrayList<>(Arrays.asList("boost", "control", "packet", "bounce")));
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
        addSetting(antiFireworkLag);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player.isElytraFlying()) {
                float yaw = mc.player.rotationYaw;
                float pitch = mc.player.rotationPitch;

                switch (mode.sval) {
                    case "boost":
                        handleBoostMode(mc, yaw, pitch);
                        break;
                    case "control":
                        handleControlMode(mc);
                        break;
                    case "packet":
                        handlePacketMode(mc);
                        break;
                    case "bounce":
                        handleBounceMode(mc);
                        break;
                }

                if (antiFireworkLag.bval) {
                    removeFireworkEntities(mc);
                }
            } else {
                handleAutoTakeOff(mc);
            }
        } catch (Exception ignored) {
        }
    }

    private void handleBoostMode(Minecraft mc, float yaw, float pitch) {
        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            EntityUtils.setMotionX(mc.player.getMotion().x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.dval);
            EntityUtils.setMotionZ(mc.player.getMotion().z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.dval);
        }
        if (mc.gameSettings.keyBindJump.isKeyDown())
            EntityUtils.setMotionY(mc.player.getMotion().y + Math.sin(Math.toRadians(pitch)) * upSpeed.dval);
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            EntityUtils.setMotionY(mc.player.getMotion().y - Math.sin(Math.toRadians(pitch)) * downSpeed.dval);
    }

    private void handleBounceMode(Minecraft mc) {
    }

    private void handleControlMode(Minecraft mc) {
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
    }

    private void handlePacketMode(Minecraft mc) {
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.setVelocity(0, +upSpeed.dval, 0);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.setVelocity(0, -downSpeed.dval, 0);
        } else {
            EntityUtils.setMotionY(0);
        }

        if (mc.gameSettings.keyBindForward.isKeyDown() ||
                mc.gameSettings.keyBindRight.isKeyDown() ||
                mc.gameSettings.keyBindBack.isKeyDown() ||
                mc.gameSettings.keyBindLeft.isKeyDown()) {
            MathUtils.setSpeed(baseSpeed.dval);
        } else {
            EntityUtils.setMotionX(0);
            EntityUtils.setMotionZ(0);
        }

        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        mc.player.connection.sendPacket(new CPlayerPacket(true));
    }

    private void removeFireworkEntities(Minecraft mc) {
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof FireworkRocketEntity && entity.ticksExisted > 0) {
                mc.world.removeEntityFromWorld(entity.getEntityId());
            }
        }
    }

    private void handleAutoTakeOff(Minecraft mc) {
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
}