/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class MobEspHack extends Module {

    public MobEspHack(int bind, String name, String displayName, Module.Category category) {
        super(bind, name, displayName, category);
        setState(true);
    }


    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        try {
            Stream<Entity> entityStream = StreamSupport.stream(mc.world.getAllEntities().spliterator(), false)
                    .filter(e -> e instanceof MobEntity);

            for (Entity entity : entityStream.collect(Collectors.toList())) {
                RenderUtils.drawOutlinedBox(entity.getPosition(), 1, 1, 0);
            }
        } catch (Exception ignored) {
        }
    }
}
