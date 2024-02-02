/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

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
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.PlayerControllerUtils;
import paul.fallen.utils.entity.RotationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class AutoTNT extends Module {

    private final Setting mode;

    public AutoTNT(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("mode", "Mode", this, "legit", new ArrayList<>(Arrays.asList("packet", "legit")));
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
        BlockPos blockPos = null;
        assert mc.world != null;
        if (mc.world.getBlockState(enBP.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(0, 0, 1)).getBlock().equals(Blocks.TNT)) {
            blockPos = enBP.add(0, 0, 1);
        } else if (mc.world.getBlockState(enBP.add(0, 0, -1)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(0, 0, -1)).getBlock().equals(Blocks.TNT)) {
            blockPos = enBP.add(0, 0, -1);
        } else if (mc.world.getBlockState(enBP.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(1, 0, 0)).getBlock().equals(Blocks.TNT)) {
            blockPos = enBP.add(1, 0, 0);
        } else if (mc.world.getBlockState(enBP.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(-1, 0, 0)).getBlock().equals(Blocks.TNT)) {
            blockPos = enBP.add(-1, 0, 0);
        } else if (mc.world.getBlockState(enBP.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(enBP.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(-1, 1, 0)).getBlock().equals(Blocks.TNT)) {
            blockPos = enBP.add(1, 1, 0);
        } else if (mc.world.getBlockState(enBP.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(enBP.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(1, 1, 0)).getBlock().equals(Blocks.TNT)) {
            blockPos = enBP.add(-1, 1, 0);
        } else if (mc.world.getBlockState(enBP.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(enBP.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(0, 1, 1)).getBlock().equals(Blocks.TNT)) {
            blockPos = enBP.add(0, 1, 1);
        } else if (mc.world.getBlockState(enBP.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(enBP.add(0, 0, -1)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(0, 1, -1)).getBlock().equals(Blocks.TNT)) {
            blockPos = enBP.add(0, 1, -1);
        }
        return blockPos;
    }

    private Entity findClosestEntity() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        assert mc.world != null;
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity != null && entity != mc.player) {
                assert mc.player != null;
                double distance = mc.player.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        assert closestEntity != null;
        if (mc.player.getDistance(closestEntity) < 5) {
            return closestEntity;
        } else {
            return null;
        }
    }

    private void placeTNTAndIgnite(BlockPos tntPos) {
        assert mc.world != null;
        if (mc.world.getBlockState(tntPos).getBlock() == Blocks.TNT) {
            assert mc.player != null;
            mc.player.inventory.currentItem = getSlot(Items.FLINT_AND_STEEL);
            if (mc.player.ticksExisted % 5 == 0) {
                //PlayerControllerUtils.rightClickBlock(tntPos, Direction.DOWN);
                //mc.playerController.clickBlock(tntPos, Direction.DOWN);
                PlayerControllerUtils.rightClickBlock(new Vector3d(0.5, 0.5, 0.5), Direction.DOWN, tntPos);
                mc.player.swingArm(Hand.MAIN_HAND);

                int[] rot = RotationUtils.getYawAndPitch(new Vector3d(tntPos.getX() + 0.5, tntPos.getY() + 0.5, tntPos.getZ() + 0.5));
                if (mode.sval == "packet") {
                    mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], mc.player.isOnGround()));
                } else {
                    mc.player.rotationYaw = rot[0];
                    mc.player.rotationPitch = rot[1];
                }
            }
        } else {
            assert mc.player != null;
            mc.player.inventory.currentItem = getSlot(Item.getItemFromBlock(Blocks.TNT));
            if (mc.player.ticksExisted % 5 == 0) {
                //PlayerControllerUtils.rightClickBlock(tntPos, Direction.DOWN);
                PlayerControllerUtils.rightClickBlock(new Vector3d(0.5, 0, 0.5), Direction.DOWN, tntPos);
                mc.player.swingArm(Hand.MAIN_HAND);

                int[] rot = RotationUtils.getYawAndPitch(new Vector3d(tntPos.getX() + 0.5, tntPos.getY(), tntPos.getZ() + 0.5));
                if (mode.sval == "packet") {
                    mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], mc.player.isOnGround()));
                } else {
                    mc.player.rotationYaw = rot[0];
                    mc.player.rotationPitch = rot[1];
                }
            }
        }
    }

    private int getSlot(Item item) {
        for (int i = 0; i < Objects.requireNonNull(mc.player).inventory.mainInventory.size(); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                return i;
            }
        }
        return 0;
    }
}
