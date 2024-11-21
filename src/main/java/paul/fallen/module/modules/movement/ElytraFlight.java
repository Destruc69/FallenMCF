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
import paul.fallen.pathfinding.LocomotionPathfinder;
import paul.fallen.utils.client.MathUtils;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ElytraFlight extends Module {

    private BlockPos target = null;

    private final Setting autoPilot;
    private final Setting upSpeed;
    private final Setting baseSpeed;
    private final Setting downSpeed;
    private final Setting easyTakeoff;

    private boolean isTakingOff = false;

    private LocomotionPathfinder airPathfinder = null;
    private List<BlockPos> path = null;

    public ElytraFlight(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        autoPilot = new Setting("AutoPilot", this, "off", new ArrayList<>(Arrays.asList("off", "assist", "full")));
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

    @Override
    public void onEnable() {
        super.onEnable();

        // Calculate the target position far in the direction the player is facing
        Vector3d playerPosition = mc.player.getPositionVec();
        Vector3d lookDirection = mc.player.getLookVec();

        // Define the distance for the autopilot target, e.g., 100 blocks in the direction the player is facing
        double targetDistance = 10000.0;
        Vector3d targetPosition = playerPosition.add(lookDirection.scale(targetDistance));

        target = new BlockPos(targetPosition.x, 255 % 2, targetPosition.z);

        airPathfinder = null;
        path = null;
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
        if (mc.player == null || mc.world == null)
            return;

        if (autoPilot.getValString().equals("assist")) {
            BlockPos middlePoint = BlockUtils.getMiddlePointBetweenBlocks(mc.player);
            if (middlePoint != null && mc.player.getDistanceSq(middlePoint.getX(), middlePoint.getY(), middlePoint.getZ()) > 1) {
                RenderUtils.drawOutlinedBox(middlePoint, 0, 1, 0, event);
            }
            BlockPos closestBlock = BlockUtils.getClosestBlock(mc.player);
            if (closestBlock != null) {
                RenderUtils.drawOutlinedBox(closestBlock, 1, 0, 0, event);
                RenderUtils.drawLine(mc.player.getPosition().down(), closestBlock, 0, 0, 1, event);
            }
        } else if (autoPilot.getValString().equals("full")) {
            if (path != null) {
                for (BlockPos blockPos : path) {
                    RenderUtils.drawOutlinedBox(blockPos, 0, 1, 0, event);
                }
            }
        }
    }

    private void handleAutoPilot() {
        if (autoPilot.getValString().equals("assist")) {
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
                if (d > 2 || d == -1) {
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

                    double pushStrength = 0.2;
                    mc.player.setMotion(pushDirection.x * pushStrength, motion.y, pushDirection.z * pushStrength);
                }
            }
        } else if (autoPilot.getValString().equals("full")) {
            // Recalculate path if the pathfinder or path is null or the player has reached the end of the path
            if (airPathfinder == null || path == null || path.isEmpty() || isAtEndOfPath()) {
                // Initialize the pathfinder with the player's current position and the calculated target position
                airPathfinder = new LocomotionPathfinder(mc.player.getPosition(), target);
                airPathfinder.compute();
                path = airPathfinder.getPath();

                // Ensure path is valid
                if (path == null || path.isEmpty()) {
                    airPathfinder = null;  // Invalidate if no path is found
                }
            }

            // Follow the calculated path if it's valid
            if (path != null && !path.isEmpty()) {
                BlockPos nextBlock = airPathfinder.getNextBlockToMove();
                if (nextBlock != null) {
                    moveToBlock(nextBlock);
                }
            }

            for (BlockPos blockPos : airPathfinder.getPath()) {
                if (!mc.world.getBlockState(blockPos).isAir()) {
                    airPathfinder = new LocomotionPathfinder(mc.player.getPosition(), target);
                    airPathfinder.compute();
                    path = airPathfinder.getPath();
                }
            }
        }
    }

    private void moveToBlock(BlockPos nextBlock) {
        // Get the player's current position
        BlockPos currentPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());

        // Determine the direction to the next block
        double deltaX = nextBlock.getX() - currentPos.getX();
        double deltaY = nextBlock.getY() - currentPos.getY();
        double deltaZ = nextBlock.getZ() - currentPos.getZ();

        // Normalize the movement direction (optional depending on movement system)
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        double moveSpeed = 0.8; // Adjust speed as necessary

        // Move towards the next block by applying the calculated direction with the moveSpeed
        if (distance > 0) {
            double moveX = deltaX / distance * moveSpeed;
            double moveY = deltaY / distance * moveSpeed;
            double moveZ = deltaZ / distance * moveSpeed;

            // Apply the movement (this would depend on your movement system, this is just a basic placeholder)
            mc.player.setVelocity(moveX, moveY, moveZ);
        }
    }

    // Method to check if the player is near the end of the path
    private boolean isAtEndOfPath() {
        if (path != null && !path.isEmpty()) {
            BlockPos lastPoint = path.get(path.size() - 1);
            double distance = mc.player.getDistanceSq(lastPoint.getX(), lastPoint.getY(), lastPoint.getZ());
            return distance < 1; // Adjust distance threshold based on how close you want to be to the end point
        }
        return false;
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