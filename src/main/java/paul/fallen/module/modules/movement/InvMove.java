/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import paul.fallen.module.Module;

public final class InvMove extends Module {

    private boolean w = false;
    private boolean d = false;
    private boolean s = false;
    private boolean a = false;
    private boolean jump = false;
    private boolean up = false;
    private boolean right = false;
    private boolean down = false;
    private boolean left = false;

    public InvMove(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        try {
            if (!(mc.gameSettings.keyBindChat.isKeyDown()) && !(mc.currentScreen instanceof EditSignScreen) && mc.currentScreen != null) {
                if (w) {
                    event.getMovementInput().moveForward = 1;
                    event.getMovementInput().forwardKeyDown = true;
                }
                if (s) {
                    event.getMovementInput().moveForward = -1;
                    event.getMovementInput().backKeyDown = true;
                }
                if (d) {
                    event.getMovementInput().moveStrafe = -1;
                    event.getMovementInput().rightKeyDown = true;
                }
                if (a) {
                    event.getMovementInput().moveStrafe = 1;
                    event.getMovementInput().leftKeyDown = true;
                }
                event.getMovementInput().jump = jump;
                if (right) {
                    mc.player.rotationYaw += 1;
                }
                if (down) {
                    mc.player.rotationPitch += 1;
                }
                if (left) {
                    mc.player.rotationYaw -= 1;
                }
                if (up) {
                    mc.player.rotationPitch -= 1;
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        w = event.getKey() == GLFW.GLFW_KEY_W;
        d = event.getKey() == GLFW.GLFW_KEY_D;
        s = event.getKey() == GLFW.GLFW_KEY_S;
        a = event.getKey() == GLFW.GLFW_KEY_A;
        jump = event.getKey() == GLFW.GLFW_KEY_SPACE;
        up = event.getKey() == GLFW.GLFW_KEY_UP;
        right = event.getKey() == GLFW.GLFW_KEY_RIGHT;
        down = event.getKey() == GLFW.GLFW_KEY_DOWN;
        left = event.getKey() == GLFW.GLFW_KEY_LEFT;
    }
}