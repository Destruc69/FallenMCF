/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public final class AutoMove extends Module {
    private final Setting forward;
    private final Setting right;
    private final Setting back;
    private final Setting left;

    public AutoMove(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
        forward = new Setting("Forward", this, false);
        right = new Setting("Right", this, false);
        back = new Setting("Back", this, false);
        left = new Setting("Left", this, false);
        addSetting(forward);
        addSetting(right);
        addSetting(back);
        addSetting(left);
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (forward.getValBoolean()) {
            event.getMovementInput().moveForward++;
            event.getMovementInput().forwardKeyDown = true;
        }
        if (right.getValBoolean()) {
            event.getMovementInput().moveStrafe--;
            event.getMovementInput().rightKeyDown = true;
        }
        if (left.getValBoolean()) {
            event.getMovementInput().moveStrafe++;
            event.getMovementInput().leftKeyDown = true;
        }
        if (back.getValBoolean()) {
            event.getMovementInput().moveForward--;
            event.getMovementInput().backKeyDown = true;
        }
    }
}
