/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.render;

import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ChestEspHack extends Module {

    public ChestEspHack(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        try {
            assert mc.world != null;
            Stream<TileEntity> tileEntityStream = mc.world.loadedTileEntityList.stream()
                    .filter(e -> e instanceof ChestTileEntity || e instanceof EnderChestTileEntity || e instanceof ShulkerBoxTileEntity);

            for (TileEntity tileEntity : tileEntityStream.collect(Collectors.toList())) {
                if (tileEntity instanceof ShulkerBoxTileEntity) {
                    RenderUtils.drawOutlinedBox(tileEntity.getPos(), 1, 0, 0, event);
                } else if (tileEntity instanceof ChestTileEntity) {
                    RenderUtils.drawOutlinedBox(tileEntity.getPos(), 0, 1, 0, event);
                } else if (tileEntity instanceof EnderChestTileEntity) {
                    RenderUtils.drawOutlinedBox(tileEntity.getPos(), 0, 0, 1, event);
                }
            }
        } catch (Exception ignored) {
        }
    }
}