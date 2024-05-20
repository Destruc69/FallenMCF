/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.EntityUtils;

public final class HighJump extends Module {

    private final Setting effect;
    private final Setting speed;
    private boolean a;

    public HighJump(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        effect = new Setting("Effect", this, false);
        speed = new Setting("Speed", this, 0.2f, 0.1f, 20.0f, true);
        addSetting(effect);
        addSetting(speed);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (effect.getValBoolean()) {
            mc.player.clearActivePotions();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.player != null;
            if (!effect.getValBoolean()) {
                if (mc.player.getMotion().y > 0) {
                    if (!a) {
                        EntityUtils.setMotionY(speed.getValDouble());
                        a = true;
                    }
                } else {
                    a = false;
                }
            } else {
                mc.player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 255, (int) speed.getValDouble(), true, true, true));
            }
        } catch (Exception ignored) {
        }
    }
}