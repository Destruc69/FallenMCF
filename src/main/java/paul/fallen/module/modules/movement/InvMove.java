/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class InvMove extends Module {

    public InvMove(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        KeyBinding[] moveKeys = {mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindBack, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint};

        if ((mc.currentScreen != null)
                && !(mc.currentScreen instanceof ChatScreen) && !(mc.currentScreen instanceof EditSignScreen)) {
            KeyBinding[] array;
            int length = (array = moveKeys).length;
            for (int i = 0; i < length; i++) {
                KeyBinding key = array[i];
                key.setPressed(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), key.getKey().getKeyCode()));
            }
        }
    }
}