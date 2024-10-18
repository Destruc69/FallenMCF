/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.client.MathUtils;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

public final class ElytraFlight extends Module {

    private final Setting autoPilot;
    private final Setting upSpeed;
    private final Setting baseSpeed;
    private final Setting downSpeed;
    private final Setting easyTakeoff;

    private boolean isTakingOff = false;

    public ElytraFlight(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        autoPilot = new Setting("AutoPilot", this, false);
        upSpeed = new Setting("Up-Speed", this, 0.05F, 0.005F, 10, false);
        baseSpeed = new Setting("Base-Speed", this, 0.05, 0.02, 10, false);
        downSpeed = new Setting("Down-Speed", this, 0.0F, 0.002F, 10, false);
        easyTakeoff = new Setting("EasyTakeOff", this, false);

        addSetting(autoPilot);
        addSetting(upSpeed);
        addSetting(baseSpeed);
        addSetting(downSpeed);
        addSetting(easyTakeoff);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        try {
            handleEasyTakeoff();

            if (!mc.player.isElytraFlying())
                return;

            handleFlight();
            handleAutoPilot();
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (autoPilot.getValBoolean()) {
                if (mc.player.getDistanceSq(BlockUtils.getMiddlePointBetweenBlocks(mc.player).getX(), BlockUtils.getMiddlePointBetweenBlocks(mc.player).getY(), BlockUtils.getMiddlePointBetweenBlocks(mc.player).getZ()) > 1) {
                    RenderUtils.drawOutlinedBox(BlockUtils.getMiddlePointBetweenBlocks(mc.player), 0, 1, 0, event);
                }
                RenderUtils.drawOutlinedBox(BlockUtils.getClosestBlock(mc.player), 1, 0, 0, event);
                RenderUtils.drawLine(mc.player.getPosition().down(), BlockUtils.getClosestBlock(mc.player), 0, 0, 1, event);
            }
        } catch (Exception ignored) {
        }
    }

    private void handleAutoPilot() {
        if (!autoPilot.getValBoolean())
            return;

        // If the jump key is pressed, just pause auto pilot, Just for disengage.
        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            BlockPos m = BlockUtils.getMiddlePointBetweenBlocks(mc.player);
            Vector3d motion = mc.player.getMotion();

            // Y handle
            if (m == null) {
                if (mc.player.getPosY() < 256) {
                    mc.player.setMotion(motion.x, upSpeed.getValDouble(), motion.z);
                } else if (mc.player.getPosY() > 256) {
                    mc.player.setMotion(motion.x, -downSpeed.getValDouble(), motion.z);
                } else {
                    mc.player.setMotion(motion.x, 0, motion.z);
                }
            } else {
                if (mc.player.getPosY() < m.getY()) {
                    mc.player.setMotion(motion.x, upSpeed.getValDouble(), motion.z);
                } else if (mc.player.getPosY() > m.getY()) {
                    mc.player.setMotion(motion.x, -downSpeed.getValDouble(), motion.z);
                } else {
                    mc.player.setMotion(motion.x, 0, motion.z);
                }
            }

            // XZ handle
            double d = BlockUtils.getDistanceToClosestBlock(mc.player);
            if (d > 1 || d == -1) {
                if (isAnyMovementKeyDown()) {
                    MathUtils.setSpeed(baseSpeed.getValDouble());
                } else {
                    mc.player.setMotion(0, mc.player.getMotion().y, 0);
                }
            } else {
                BlockPos c = BlockUtils.getClosestBlock(mc.player);
                Vector3d playerPos = new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());

                if (c == null)
                    return;

                Vector3d pushDirection = playerPos.subtract(Vector3d.copyCentered(c)).normalize();

                double pushStrength = 0.1;
                mc.player.setMotion(pushDirection.x * pushStrength, motion.y, pushDirection.z * pushStrength);
            }
        }
    }

    private void handleFlight() {
        if (autoPilot.getValBoolean())
            return;

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