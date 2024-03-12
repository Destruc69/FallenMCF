/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.potion.Effects;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ItemEspHack extends Module {

    Setting mode;

    public ItemEspHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", "Mode", this, "glow", new ArrayList<>(Arrays.asList("glow", "box")));
        addSetting(mode);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        try {
            assert mc.world != null;
            Stream<Entity> entityStream = StreamSupport.stream(mc.world.getAllEntities().spliterator(), false)
                    .filter(e -> e instanceof ItemEntity);

            for (Entity entity : entityStream.collect(Collectors.toList())) {
                if (mode.sval == "box") {
                    RenderUtils.drawOutlinedBox(entity.getPosition(), 0, 1, 0, event);
                    entity.setGlowing(false);
                } else {
                    entity.setGlowing(true);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
