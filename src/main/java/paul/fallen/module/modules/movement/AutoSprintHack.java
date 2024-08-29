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
import roger.pathfind.main.walk.Walker;

public final class AutoSprintHack extends Module {

    public AutoSprintHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();

            Walker.getInstance().walk(mc.player.getPosition(), mc.player.getPosition().add(10, -10, 10), 1000);
        } catch (Exception ignored) {
        }
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
        try {
        } catch (Exception ignored) {
        }
    }
}