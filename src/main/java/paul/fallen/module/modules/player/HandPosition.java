/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public final class HandPosition extends Module {

    private final Setting rightHandX;
    private final Setting rightHandY;
    private final Setting rightHandZ;
    private final Setting leftHandX;
    private final Setting leftHandY;
    private final Setting leftHandZ;
    private final Setting animation;

    public HandPosition(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        this.rightHandX = new Setting("Right Hand X offset", this, 0.0f, -2.0f, 2.0f, true);
        this.rightHandY = new Setting("Right Hand Y offset", this, 0.0f, -2.0f, 2.0f, true);
        this.rightHandZ = new Setting("Right Hand Z offset", this, 0.0f, -2.0f, 2.0f, true);
        this.leftHandX = new Setting("Left Hand X offset", this, 0.0f, -2.0f, 2.0f, true);
        this.leftHandY = new Setting("Left Hand Y offset", this, 0.0f, -2.0f, 2.0f, true);
        this.leftHandZ = new Setting("Left Hand Z offset", this, 0.0f, -2.0f, 2.0f, true);
        this.animation = new Setting("Animation", this, false);
        addSetting(this.rightHandX);
        addSetting(this.rightHandY);
        addSetting(this.rightHandZ);
        addSetting(this.leftHandX);
        addSetting(this.leftHandY);
        addSetting(this.leftHandZ);
        addSetting(this.animation);
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (event.getHand() == Hand.MAIN_HAND) {
            event.getMatrixStack().translate(rightHandX.getValDouble(), rightHandY.getValDouble(), rightHandZ.getValDouble());

            if (animation.getValBoolean() && mc.player.swingProgress > 0) {
                // Slow down the animation significantly by using a small factor and increase frequency for smoother animation
                float animationProgress = mc.player.swingProgress * 0.1f; // Adjust the factor to control the animation speed
                float progress = (float)(Math.sin(animationProgress * Math.PI) * 75); // Adjust frequency for smoother animation
                event.getMatrixStack().rotate(Vector3f.YP.rotation(progress));
            }
        }

        if (event.getHand() == Hand.OFF_HAND) {
            event.getMatrixStack().translate(leftHandX.getValDouble(), leftHandY.getValDouble(), leftHandZ.getValDouble());

            if (animation.getValBoolean() && mc.player.swingProgress > 0) {
                // Slow down the animation significantly by using a small factor and increase frequency for smoother animation
                float animationProgress = mc.player.swingProgress * 0.1f; // Adjust the factor to control the animation speed
                float progress = (float)(Math.sin(animationProgress * Math.PI) * 75); // Adjust frequency for smoother animation
                event.getMatrixStack().rotate(Vector3f.YP.rotation(progress));
            }
        }
    }
}