/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.EntityUtils;

import java.util.ArrayList;
import java.util.Arrays;

public final class FastFall extends Module {

    private final Setting mode;
    private final Setting speed;

    public FastFall(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        setState(true);

        mode = new Setting("mode", this, "normal", new ArrayList<>(Arrays.asList("normal", "ncp")));
        addSetting(mode);

        speed = new Setting("speed", "Speed", this, 0.1f, 0.05f, 10);
        addSetting(speed);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (mode.sval == "normal") {
                assert mc.player != null;
                if (mc.player.fallDistance > 1) {
                    EntityUtils.setMotionY(-speed.dval);
                }
            }
            if (mode.sval == "ncp") {
                assert mc.player != null;
                if (mc.player.fallDistance > 1) {
                    assert mc.world != null;
                    if (!mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 0.7531999805211997D, mc.player.getPosZ())).getBlock().equals(Blocks.AIR))
                        return;
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() - 0.41999998688698D, mc.player.getPosZ(), true));
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() - 0.7531999805211997D, mc.player.getPosZ(), true));
                    mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() - 0.7531999805211997D, mc.player.getPosZ());
                }
            }
        } catch (Exception ignored) {
        }
    }

}