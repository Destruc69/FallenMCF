/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import com.sun.jna.platform.KeyboardUtils;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import paul.fallen.module.Module;

public final class InvMove extends Module {

    public InvMove(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        try {
            if (!(mc.gameSettings.keyBindChat.isKeyDown()) && !(mc.currentScreen instanceof EditSignScreen) && mc.currentScreen != null) {
                if (KeyboardUtils.isPressed(mc.gameSettings.keyBindForward.getKey().getKeyCode())) {
                    event.getMovementInput().moveForward = 1;
                    event.getMovementInput().forwardKeyDown = true;
                }
                if (KeyboardUtils.isPressed(mc.gameSettings.keyBindBack.getKey().getKeyCode())) {
                    event.getMovementInput().moveForward = -1;
                    event.getMovementInput().backKeyDown = true;
                }
                if (KeyboardUtils.isPressed(mc.gameSettings.keyBindRight.getKey().getKeyCode())) {
                    event.getMovementInput().moveStrafe = -1;
                    event.getMovementInput().rightKeyDown = true;
                }
                if (KeyboardUtils.isPressed(mc.gameSettings.keyBindLeft.getKey().getKeyCode())) {
                    event.getMovementInput().moveStrafe = 1;
                    event.getMovementInput().leftKeyDown = true;
                }
                if (KeyboardUtils.isPressed(GLFW.GLFW_KEY_RIGHT)) {
                    assert mc.player != null;
                    mc.player.rotationYaw = mc.player.rotationYaw + 3;
                }
                if (KeyboardUtils.isPressed(GLFW.GLFW_KEY_LEFT)) {
                    assert mc.player != null;
                    mc.player.rotationYaw = mc.player.rotationYaw - 3;
                }
                if (KeyboardUtils.isPressed(GLFW.GLFW_KEY_UP)) {
                    assert mc.player != null;
                    mc.player.rotationPitch = mc.player.rotationPitch - 3;
                }
                if (KeyboardUtils.isPressed(GLFW.GLFW_KEY_DOWN)) {
                    assert mc.player != null;
                    mc.player.rotationPitch = mc.player.rotationPitch + 3;
                }
            }
        } catch (Exception ignored) {
        }
    }
}