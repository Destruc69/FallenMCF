/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.PlayerControllerUtils;
import paul.fallen.utils.entity.RotationUtils;

import java.util.ArrayList;
import java.util.Arrays;

public final class AutoTNT extends Module {

    private final Setting mode;

    public AutoTNT(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", this, "legit", new ArrayList<>(Arrays.asList("packet", "legit")));
        addSetting(mode);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            Entity closestEntity = findClosestEntity();
            if (closestEntity != null) {
                BlockPos tntPos = findTNTPlacementPosition(closestEntity);
                if (tntPos != null) {
                    placeTNTAndIgnite(tntPos);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private BlockPos findTNTPlacementPosition(Entity entity) {
        BlockPos enBP = entity.getPosition().add(0.5, 0, 0.5);
        BlockPos[] offsets = {
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, -1),
                new BlockPos(1, 0, 0),
                new BlockPos(-1, 0, 0),
                new BlockPos(1, 1, 0),
                new BlockPos(-1, 1, 0),
                new BlockPos(0, 1, 1),
                new BlockPos(0, 1, -1)
        };

        for (BlockPos offset : offsets) {
            BlockPos pos = enBP.add(offset);
            if (isValidPlacementPosition(pos)) {
                return pos;
            }
        }
        return null;
    }

    private boolean isValidPlacementPosition(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return block.equals(Blocks.AIR) || block.equals(Blocks.TNT);
    }

    private Entity findClosestEntity() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        assert mc.world != null;
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity != null && entity != mc.player) {
                double distance = mc.player.getDistanceSq(entity);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        return (closestEntity != null && mc.player.getDistance(closestEntity) < 5) ? closestEntity : null;
    }

    private void placeTNTAndIgnite(BlockPos tntPos) {
        assert mc.world != null;
        if (mc.world.getBlockState(tntPos).getBlock() == Blocks.TNT) {
            mc.player.inventory.currentItem = getSlot(Items.FLINT_AND_STEEL);
            if (mc.player.ticksExisted % 5 == 0) {
                placeBlock(tntPos);
            }
        } else {
            mc.player.inventory.currentItem = getSlot(Item.getItemFromBlock(Blocks.TNT));
            if (mc.player.ticksExisted % 5 == 0) {
                placeBlock(tntPos);
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        PlayerControllerUtils.rightClickBlock(new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.DOWN, pos);
        mc.player.swingArm(Hand.MAIN_HAND);

        float[] rot = RotationUtils.getYawAndPitch(new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        if ("packet".equals(mode.getValString())) {
            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], mc.player.isOnGround()));
        } else {
            mc.player.rotationYaw = rot[0];
            mc.player.rotationPitch = rot[1];
        }
    }

    private int getSlot(Item item) {
        for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return 0; // Default slot if item not found
    }
}