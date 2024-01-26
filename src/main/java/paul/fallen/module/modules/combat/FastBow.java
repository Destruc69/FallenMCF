/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class FastBow extends Module {

    public FastBow(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        try {
            if (event.phase != TickEvent.Phase.START) return;

            assert mc.player != null;
            if (mc.player.getHealth() > 0.0f && mc.player.isOnGround()) {
                mc.player.inventory.getCurrentItem();
                mc.player.inventory.getCurrentItem().getItem();
                if (Minecraft.getInstance().gameSettings.keyBindUseItem.isKeyDown()) {
                    assert mc.playerController != null;
                    assert mc.world != null;
                    mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
                    for (int i = 0; i < 20; ++i) {
                        mc.player.connection.sendPacket(new CPlayerPacket());
                    }
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.DOWN));
                    mc.player.inventory.getCurrentItem().getItem().onPlayerStoppedUsing(mc.player.inventory.getCurrentItem(), mc.world, mc.player, 10);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
