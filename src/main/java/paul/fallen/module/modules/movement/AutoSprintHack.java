/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.pathfinding.Pathfinder;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;

public final class AutoSprintHack extends Module {

    private ArrayList<Pathfinder.CustomBlockPos> pp = new ArrayList<>();
    private Pathfinder pathfinder;

    public AutoSprintHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();

            pathfinder = new Pathfinder(mc.player.getPosition(), mc.player.getPosition().add(10, -10, 10));
            pathfinder.think();
            pp = pathfinder.getPath();
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) throws Exception {
        try {
            mc.gameSettings.keyBindSprint.setPressed(true);

            pathfinder.act();
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            for (Pathfinder.CustomBlockPos b : pp) {
                if (b.getActionCost() == Pathfinder.BREAK_COST) {
                    RenderUtils.drawOutlinedBox(b.getBlockPos(), 1, 0, 0, event);
                } else if (b.getActionCost() == Pathfinder.PLACE_COST) {
                    RenderUtils.drawOutlinedBox(b.getBlockPos(), 0, 1, 0, event);
                } else if (b.getActionCost() == Pathfinder.TRAVERSE_COST) {
                    RenderUtils.drawOutlinedBox(b.getBlockPos(), 0, 0, 1, event);
                }
            }
        } catch (Exception ignored) {
        }
    }
}