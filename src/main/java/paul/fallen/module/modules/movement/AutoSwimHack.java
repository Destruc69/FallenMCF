/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

import java.util.ArrayList;
import java.util.Arrays;

public final class AutoSwimHack extends Module {
    private final Setting mode;

    public AutoSwimHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        mode = new Setting("Mode", this, "Dolphin", new ArrayList<>(Arrays.asList("Dolphin", "Fish")));
        addSetting(mode);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = event.player;

            if (player.isInWater() && !player.isSneaking()
                    && !mc.gameSettings.keyBindJump.isKeyDown()) {
                assert mc.player != null;
                player.setMotion(mc.player.getMotion().x, mc.player.getMotion().y + getUpwardsMotion(), mc.player.getMotion().z);
            }
        } catch (Exception ignored) {
        }
    }

    private double getUpwardsMotion() {
        switch (mode.getValString()) {
            case "Dolphin":
                return 0.04;
            case "Fish":
                return 0.02;
            default:
                return 0.0;
        }
    }
}
