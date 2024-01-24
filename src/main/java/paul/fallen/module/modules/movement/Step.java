/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class Step extends Module {
    private boolean a;

    public Step(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = mc.player;

            assert player != null;
            if (!player.isOnGround() || player.isInWater() || player.isInLava()) {
                return;
            }

            if (player.moveForward == 0 && player.moveStrafing == 0) {
                return;
            }

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                return;
            }

            AxisAlignedBB box = player.getBoundingBox().offset(0, 0.05, 0).grow(0.05);

            double stepHeight = Double.NEGATIVE_INFINITY;

            ArrayList<AxisAlignedBB> blockCollisions = new ArrayList<AxisAlignedBB>((Collection<? extends AxisAlignedBB>) mc.world.getBlockCollisionShapes(player, box));

            for (AxisAlignedBB bb : blockCollisions) {
                if (bb.maxY > stepHeight) {
                    stepHeight = bb.maxY;
                }
            }

            stepHeight = stepHeight - player.getPosY();

            if (mc.player.collidedHorizontally && mc.player.isOnGround() && mc.player.fallDistance == 0.0f && !mc.player.isOnLadder() && !mc.player.movementInput.jump) {
                if (!a) {
                    ncpStep(stepHeight);
                    a = true;
                }
            } else {
                a = false;
            }
        } catch (Exception ignored) {
        }
    }

    private void ncpStep(double height) {
        List<Double> offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
        assert mc.player != null;
        double posX = mc.player.getPosX();
        double posZ = mc.player.getPosZ();
        double y = mc.player.getPosY();
        if (height < 1.1) {
            double first = 0.42;
            double second = 0.75;
            if (height != 1) {
                first *= height;
                second *= height;
                if (first > 0.425) {
                    first = 0.425;
                }
                if (second > 0.78) {
                    second = 0.78;
                }
                if (second < 0.49) {
                    second = 0.49;
                }
            }
            if (first == 0.42) {
                first = 0.41999998688698;
            }
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y + first, posZ, false));
            if (y + second < y + height) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y + second, posZ, false));
            }
        } else if (height < 1.6) {
            for (double off : offset) {
                y += off;
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y, posZ, false));
            }
        } else if (height < 2.1) {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
            for (double off : heights) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y + off, posZ, false));
            }
        } else {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
            for (double off : heights) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(posX, y + off, posZ, false));
            }
        }
        if (height <= 0) {
            mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + 0.42D, mc.player.getPosZ());
        } else if (height > 0) {
            mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + height - 0.58, mc.player.getPosZ());
        }
    }
}

