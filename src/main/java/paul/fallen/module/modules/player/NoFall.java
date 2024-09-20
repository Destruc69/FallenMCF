/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.EntityUtils;
import paul.fallen.utils.entity.InventoryUtils;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

public final class NoFall extends Module {

    private final Setting mlg;
    private boolean falling = false;
    private boolean canPlaceWater = false;

    public NoFall(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mlg = new Setting("MLG", this, false);
        addSetting(mlg);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        falling = false;
        canPlaceWater = false;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;
        try {
            if (mlg.getValBoolean()) {
                if (!mc.player.isOnGround() && EntityUtils.getFallDistance(mc.player) > 4) {
                    falling = true;
                    canPlaceWater = true;
                } else if (mc.player.isOnGround()) {
                    falling = false;
                    canPlaceWater = false;
                }

                if (falling && canPlaceWater) {
                    int waterBucketSlot = InventoryUtils.getSlot(Items.WATER_BUCKET);
                    if (waterBucketSlot != -1) {
                        InventoryUtils.setSlot(waterBucketSlot);

                        if (EntityUtils.getFallDistance(mc.player) <= 1) {
                            BlockPos waterPlacePos = mc.player.getPosition().add(0, -EntityUtils.getFallDistance(mc.player), 0);

                            BlockUtils.rightClickBlock(waterPlacePos, mc.player.inventory.currentItem, true, true);
                            BlockUtils.rightClickBlock(waterPlacePos, mc.player.inventory.currentItem, true, true);

                            canPlaceWater = false;
                        }
                    }
                }
            } else {
                if (mc.player.fallDistance > 3) {
                    mc.player.connection.sendPacket(new CPlayerPacket(true));
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            RenderUtils.drawOutlinedBox(mc.player.getPosition().add(0, -EntityUtils.getFallDistance(mc.player), 0), 0, 1, 0, event);
        } catch (Exception ignored) {
        }
    }
}