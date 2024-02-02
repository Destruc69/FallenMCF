/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public final class NoSlowDown extends Module {

    private final Setting ncp;
    private final Setting other;

    public NoSlowDown(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        ncp = new Setting("NCP", "ncp", this, false);
        other = new Setting("Other", "other", this, true);
        addSetting(ncp);
        addSetting(other);
    }

    @SubscribeEvent
    public void onUpdate(InputUpdateEvent event) {
        try {
            assert mc.player != null;
            if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem().isFood()) {
                event.getMovementInput().moveForward *= 5;
                event.getMovementInput().moveStrafe *= 5;
                if (ncp.bval) {
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 1.0, mc.player.getPosZ()), Direction.DOWN));
                }
                if (other.bval) {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(getHandSlot()));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private int getHandSlot() {
        assert mc.player != null;
        return mc.player.inventory.currentItem;
    }
}