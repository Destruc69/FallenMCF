/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.pathfinding.LocomotionPathfinder;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

public final class AutoSprintHack extends Module {

    private LocomotionPathfinder pathfinder;

    public AutoSprintHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();
            pathfinder = new LocomotionPathfinder(mc.player.getPosition(), new BlockPos(0, 0, 0));
            pathfinder.compute();
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) throws Exception {
        try {
            mc.gameSettings.keyBindSprint.setPressed(true);

            if (pathfinder.getPath().size() > 0) {
                //pathfinder.move();

                if (mc.world.getBlockState(pathfinder.getTargetPositionInPathArray(pathfinder.getPath())).isAir() && !mc.world.getBlockState(pathfinder.getTargetPositionInPathArray(pathfinder.getPath()).up()).isAir()) {
                    BlockUtils.breakBlock(pathfinder.getTargetPositionInPathArray(pathfinder.getPath()).up(), Minecraft.getInstance().player.inventory.currentItem, true, true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            for (BlockPos b : pathfinder.getPath()) {
                RenderUtils.drawOutlinedBox(b, 0, 1, 0, event);
            }
        } catch (Exception ignored) {
        }
    }
}