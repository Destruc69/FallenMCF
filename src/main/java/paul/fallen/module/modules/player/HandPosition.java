/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.util.Hand;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public final class HandPosition extends Module {

    private final Setting rightHandX;
    private final Setting rightHandY;
    private final Setting rightHandZ;
    private final Setting leftHandX;
    private final Setting leftHandY;
    private final Setting leftHandZ;

    public HandPosition(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        this.rightHandX = new Setting("rightHandX", "Right Hand X offset", this, 0.0f, -2.0f, 2.0f);
        this.rightHandY = new Setting("rightHandY", "Right Hand Y offset", this, 0.0f, -2.0f, 2.0f);
        this.rightHandZ = new Setting("rightHandZ", "Right Hand Z offset", this, 0.0f, -2.0f, 2.0f);
        this.leftHandX = new Setting("leftHandX", "Left Hand X offset", this, 0.0f, -2.0f, 2.0f);
        this.leftHandY = new Setting("leftHandY", "Left Hand Y offset", this, 0.0f, -2.0f, 2.0f);
        this.leftHandZ = new Setting("leftHandZ", "Left Hand Z offset", this, 0.0f, -2.0f, 2.0f);
        addSetting(this.rightHandX);
        addSetting(this.rightHandY);
        addSetting(this.rightHandZ);
        addSetting(this.leftHandX);
        addSetting(this.leftHandY);
        addSetting(this.leftHandZ);
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (event.getHand() == Hand.MAIN_HAND) {
            GL11.glTranslated(rightHandX.dval, rightHandY.dval, rightHandZ.dval);
        }

        if (event.getHand() == Hand.OFF_HAND) {
            GL11.glTranslated(leftHandX.dval, leftHandY.dval, leftHandZ.dval);
        }
    }
}