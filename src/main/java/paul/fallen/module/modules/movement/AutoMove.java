/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

import java.util.Objects;

public final class AutoMove extends Module {
    private final Setting forward;
    private final Setting right;
    private final Setting back;
    private final Setting left;
    private final Setting lockX;
    private final Setting lockZ;

    public AutoMove(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        forward = new Setting("Forward", this, false);
        right = new Setting("Right", this, false);
        back = new Setting("Back", this, false);
        left = new Setting("Left", this, false);
        lockX = new Setting("LockX", this, false);
        lockZ = new Setting("LockZ", this, false);
        addSetting(forward);
        addSetting(right);
        addSetting(back);
        addSetting(left);
        addSetting(lockX);
        addSetting(lockZ);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.player != null;
            if (mc.player.getRidingEntity() != null) {
                if (lockX.bval) {
                    mc.player.setMotion(0, mc.player.getMotion().y, mc.player.getMotion().z);
                }
                if (lockZ.bval) {
                    mc.player.setMotion(mc.player.getMotion().x, mc.player.getMotion().y, 0);
                }
            } else {
                if (lockX.bval) {
                    Objects.requireNonNull(mc.player.getRidingEntity()).setMotion(0, Objects.requireNonNull(mc.player.getRidingEntity()).getPosX(), Objects.requireNonNull(mc.player.getRidingEntity()).getPosZ());
                }
                if (lockZ.bval) {
                    Objects.requireNonNull(mc.player.getRidingEntity()).setMotion(Objects.requireNonNull(mc.player.getRidingEntity()).getPosX(), Objects.requireNonNull(mc.player.getRidingEntity()).getPosY(), 0);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (forward.bval) {
            event.getMovementInput().moveForward++;
            event.getMovementInput().forwardKeyDown = true;
        }
        if (right.bval) {
            event.getMovementInput().moveStrafe--;
            event.getMovementInput().rightKeyDown = true;
        }
        if (left.bval) {
            event.getMovementInput().moveStrafe++;
            event.getMovementInput().leftKeyDown = true;
        }
        if (back.bval) {
            event.getMovementInput().moveForward--;
            event.getMovementInput().backKeyDown = true;
        }
    }
}
