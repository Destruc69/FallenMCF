/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.client.MathUtils;
import paul.fallen.utils.entity.EntityUtils;
import paul.fallen.utils.entity.InventoryUtils;
import paul.fallen.utils.entity.RotationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CrystalAuraHack extends Module {

    private final Setting range;
    private final Setting autoPlace;
    private final Setting faceBlocks;
    private final Setting tick;

    public CrystalAuraHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        range = new Setting("range", this, 4, 2, 6, true);
        autoPlace = new Setting("Auto-place crystals", this, true);
        faceBlocks = new Setting("Face crystals", this, false);
        tick = new Setting("Tick", this, 6, 1, 20, true);

        addSetting(range);
        addSetting(autoPlace);
        addSetting(faceBlocks);
        addSetting(tick);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        try {
            if (event.phase != TickEvent.Phase.START) return;

            // Your CrystalAura logic here
            ArrayList<Entity> crystals = getNearbyCrystals();

            if (!crystals.isEmpty()) {
                detonate(crystals);
                return;
            }

            if (!autoPlace.getValBoolean()) return;

            assert mc.player != null;
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
                if (InventoryUtils.getSlot(Items.END_CRYSTAL) != -1) {
                    if (InventoryUtils.getSlot(Items.END_CRYSTAL) > 9) {
                        InventoryUtils.click(InventoryUtils.getSlot(Items.END_CRYSTAL));
                        if (InventoryUtils.getSlot(Items.AIR) < 9) {
                            InventoryUtils.click(InventoryUtils.getSlot(Items.AIR));
                        }
                    } else {
                        InventoryUtils.setSlot(InventoryUtils.getSlot(Items.END_CRYSTAL));
                    }
                }
            } else {
                ArrayList<Entity> targets = getNearbyTargets();
                placeCrystalsNear(targets);
            }
        } catch (Exception ignored) {
        }
    }

    private ArrayList<BlockPos> placeCrystalsNear(ArrayList<Entity> targets) {
        ArrayList<BlockPos> newCrystals = new ArrayList<>();

        boolean shouldSwing = false;
        for (Entity target : targets) {
            ArrayList<BlockPos> freeBlocks = getFreeBlocksNear(target);

            assert mc.player != null;
            if (mc.player.ticksExisted % tick.getValDouble() == 0) {
                for (BlockPos pos : freeBlocks)
                    if (placeCrystal(pos)) {
                        shouldSwing = true;
                        newCrystals.add(pos);
                        break;
                    }
            }
        }

        if (shouldSwing) {
            // Swing the player's hand if a crystal was placed
            mc.player.swingArm(Hand.MAIN_HAND);
        }

        return newCrystals;
    }

    private void detonate(ArrayList<Entity> crystals) {
        for (Entity e : crystals) {
            Vector3d toLook = new Vector3d(e.getBoundingBox().getCenter().x, e.getBoundingBox().getCenter().y, e.getBoundingBox().getCenter().z);
            if (faceBlocks.getValBoolean()) {
                float[] rot = RotationUtils.getYawAndPitch(toLook);
                assert mc.player != null;
                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], mc.player.isOnGround()));
            }
            assert mc.playerController != null;
            assert mc.player != null;
            mc.playerController.attackEntity(mc.player, e);
            mc.player.connection.sendPacket(new CUseEntityPacket(e, Hand.MAIN_HAND, toLook, mc.player.isSneaking()));
        }

        if (!crystals.isEmpty()) {
            // Swing the player's hand if crystals were detonated
            mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    private ArrayList<Entity> getNearbyCrystals() {
        PlayerEntity player = mc.player;
        double rangeSq = Math.pow(range.getValDouble(), 2);

        Comparator<Entity> furthestFromPlayer = Comparator
                .<Entity>comparingDouble(e -> {
                    assert mc.player != null;
                    return mc.player.getDistanceSq(e);
                })
                .reversed();


        assert mc.world != null;
        Stream<Entity> entityStream = StreamSupport.stream(mc.world.getAllEntities().spliterator(), false)
                .filter(e -> e instanceof EnderCrystalEntity);

        ArrayList<Entity> crystals = entityStream
                .filter(e -> !e.removed)
                .filter(e -> {
                    assert player != null;
                    return player.getDistanceSq(e.getPosX(), e.getPosY(), e.getPosZ()) <= rangeSq;
                })
                .sorted(Comparator.comparingDouble(entity -> -player.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ())))
                .collect(Collectors.toCollection(ArrayList::new));

        return crystals;
    }

    private ArrayList<Entity> getNearbyTargets() {
        double rangeSq = Math.pow(range.getValDouble(), 2);

        assert mc.world != null;
        Stream<Entity> entityStream = StreamSupport.stream(mc.world.getAllEntities().spliterator(), false);

        Comparator<Entity> furthestFromPlayer = Comparator
                .<Entity>comparingDouble(e -> {
                    assert mc.player != null;
                    return mc.player.getDistanceSq(e);
                })
                .reversed();

        Stream<Entity> stream = entityStream
                .filter(e -> !e.removed)
                .filter(e -> e instanceof LivingEntity && ((LivingEntity) e).getHealth() > 0)
                .filter(e -> e != mc.player)
                .filter(e -> {
                    assert mc.player != null;
                    return mc.player.getDistanceSq(e.getPosX(), e.getPosY(), e.getPosZ()) <= rangeSq;
                });

        // Apply entity filters here if needed.

        return stream.sorted(furthestFromPlayer)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<BlockPos> getFreeBlocksNear(Entity target) {
        Vector3d eyesVec = mc.player.getEyePosition(mc.getRenderPartialTicks());
        double rangeD = range.getValDouble();
        double rangeSq = Math.pow(rangeD + 0.5, 2);
        int rangeI = 2;

        BlockPos center = target.getPosition();
        BlockPos min = center.add(-rangeI, -rangeI, -rangeI);
        BlockPos max = center.add(rangeI, rangeI, rangeI);
        AxisAlignedBB targetBB = target.getBoundingBox();

        Vector3d targetEyesVec = target.getPositionVec().add(new Vector3d(0, target.getEyeHeight(), 0));

        Comparator<BlockPos> closestToTarget =
                Comparator.comparingDouble(
                        pos -> targetEyesVec.squareDistanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ())));

        ArrayList<BlockPos> freeBlocks = new ArrayList<>();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    // Check if the position is within range
                    if (MathUtils.getDistance(eyesVec, new Vector3d(pos.getX(), pos.getY(), pos.getZ())) <= rangeSq) {
                        // Check if the block is replaceable and has a crystal base
                        if (isReplaceable(pos) && hasCrystalBase(pos)) {
                            // Check if it doesn't intersect with the target's bounding box
                            if (!targetBB.intersects(new AxisAlignedBB(pos))) {
                                freeBlocks.add(pos);
                            }
                        }
                    }
                }
            }
        }

        freeBlocks.sort(closestToTarget);
        return freeBlocks;
    }

    private boolean isReplaceable(BlockPos pos) {
        assert mc.world != null;
        BlockState state = mc.world.getBlockState(pos);
        return state.getBlock().isAir(state, mc.world, pos);
    }

    private boolean hasCrystalBase(BlockPos pos) {
        assert mc.world != null;
        Block block = mc.world.getBlockState(pos.down()).getBlock();
        return block == Blocks.BEDROCK || block == Blocks.OBSIDIAN;
    }

    private boolean placeCrystal(BlockPos pos) {
        double rangeSq = Math.pow(range.getValDouble(), 2);
        Vector3d posVec = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
        double distanceSqPosVec = MathUtils.getDistance(mc.player.getPositionVec().add(0, 1, 0), new Vector3d(posVec.x, posVec.y, posVec.z));

        for (Direction side : Direction.values()) {
            BlockPos neighbor = pos.offset(side);

            // Check if neighbor can be right-clicked
            if (!isClickableNeighbor(neighbor)) continue;

            Vector3d dirVec = new Vector3d(side.getDirectionVec().getX(), side.getDirectionVec().getY(), side.getDirectionVec().getZ());
            Vector3d hitVec = posVec.add(dirVec.scale(0.5));

            // Check if hitVec is within range
            if (MathUtils.getDistance(mc.player.getPositionVec().add(0, 1, 0), new Vector3d(hitVec.x, hitVec.y, hitVec.z)) > rangeSq) continue;

            // Check if side is visible (facing away from player)
            if (distanceSqPosVec > MathUtils.getDistance(mc.player.getPositionVec().add(0, 1, 0), new Vector3d(posVec.x + dirVec.x, posVec.y + dirVec.y, posVec.z + dirVec.z))) {
                continue;
            }

            assert mc.player != null;
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL))
                return false;

            Vector3d toLook = new Vector3d(neighbor.getX() + 0.5, mc.player.lastTickPosY > neighbor.getY() ? neighbor.getY() + 1 : neighbor.getY(), neighbor.getZ() + 0.5);
            float[] rot = RotationUtils.getYawAndPitch(toLook);
            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], mc.player.isOnGround()));

            // Place the crystal
            mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(hitVec, Direction.UP, neighbor, false));
            mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(hitVec, Direction.UP, neighbor, false)));
            return true;
        }

        return false;
    }

    private boolean isClickableNeighbor(BlockPos pos) {
        assert mc.world != null;

        // Check neighboring positions (excluding the center position)
        BlockPos[] neighbors = {
                pos.add(-1, 0, 0), pos.add(1, 0, 0),
                pos.add(0, -1, 0), pos.add(0, 1, 0),
                pos.add(0, 0, -1), pos.add(0, 0, 1)
        };

        for (BlockPos neighborPos : neighbors) {
            BlockState neighborState = mc.world.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();

            // Check if the neighbor block is not air (supporting block)
            if (!neighborBlock.isAir(neighborState, mc.world, neighborPos)) {
                return true;
            }
        }

        // No supporting neighbor found
        return false;
    }
}
