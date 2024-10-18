/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.world;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.InventoryUtils;
import paul.fallen.utils.entity.PlayerUtils;

public final class AutoTool extends Module {

    public AutoTool(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onTick(PlayerEvent.BreakSpeed event) {
        if (PlayerUtils.getSlotHotbarBestToolForBlock(mc.world.getBlockState(event.getPos()).getBlock()) != -1) {
            InventoryUtils.setSlot(PlayerUtils.getSlotHotbarBestToolForBlock(mc.world.getBlockState(event.getPos()).getBlock()));
        }
    }
}