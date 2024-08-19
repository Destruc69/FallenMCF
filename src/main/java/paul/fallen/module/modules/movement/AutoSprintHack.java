/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.pathfinding.Pathfinder;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;

public final class AutoSprintHack extends Module {

    private ArrayList<BlockPos> pp;

    public AutoSprintHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Pathfinder pathfinder = new Pathfinder(mc.player.getPosition(), mc.player.getPosition().add(10, 0, 10));
        pathfinder.think(100);
        pp = pathfinder.getPath();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) throws Exception {
        try {
            mc.gameSettings.keyBindSprint.setPressed(true);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        for (BlockPos b : pp) {
            RenderUtils.drawOutlinedBox(b, 0, 1, 0, event);
        }
    }
}