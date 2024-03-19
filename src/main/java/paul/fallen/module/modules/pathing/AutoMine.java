package paul.fallen.module.modules.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.pathfinder.AStarCustomPathFinder;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class AutoMine extends Module {

    private boolean started;

    private AStarCustomPathFinder pathFinder;

    private BlockPos posA;
    private BlockPos posB;

    public AutoMine(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        started = false;

        posA = new BlockPos(0, 0, 0);
        posB = new BlockPos(0, 0, 0);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (!started) {
                BlockPos blockPos = new BlockPos(mc.objectMouseOver.getHitVec());
                if (posA.getX() == 0 && posB.getX() == 0 && posA.getZ() == 0 && posB.getZ() == 0) {
                    if (mc.playerController.getIsHittingBlock()) {
                        posA = blockPos;
                    }
                } else if (posA.getX() != 0 && posB.getX() == 0 && posA.getZ() != 0 && posB.getZ() == 0) {
                    if (mc.playerController.getIsHittingBlock()) {
                        posB = blockPos;
                    }
                } else if (posA.getX() != 0 && posB.getX() != 0 && posA.getZ() != 0 && posB.getZ() != 0) {
                    started = true;
                }
            } else {
                ArrayList<BlockPos> blockPosArrayList = sortBlockPosByY(BlockUtils.getAllBlocksBetween(posA, posB));

                blockPosArrayList.removeIf(blockPos -> mc.world.isAirBlock(blockPos));
                blockPosArrayList.removeIf(blockPos -> !BlockUtils.canSeeBlock(blockPos));

                BlockPos targetPosition = blockPosArrayList.get(0);

                if (targetPosition != null) {
                    if (mc.player.getDistanceSq(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ()) > 3) {
                        if (pathFinder.hasReachedEndOfPath() || pathFinder.getPath().size() <= 0 || pathFinder == null) {
                            pathFinder = new AStarCustomPathFinder(mc.player.getPositionVec(), new Vector3d(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ()));
                            pathFinder.compute();
                        } else {
                            pathFinder.move();
                        }
                    } else {
                        mc.playerController.onPlayerDamageBlock(targetPosition, Direction.fromAngle(mc.player.rotationYaw));
                        mc.player.swingArm(Hand.MAIN_HAND);
                        RotationUtils.rotateTo(new Vector3d(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ()), true);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private ArrayList<BlockPos> sortBlockPosByY(ArrayList<BlockPos> blockPosList) {
        if (mc.player == null) {
            // Handle null cases
            return new ArrayList<>();
        }

        // Create a copy of the original ArrayList to avoid modifying the original list
        ArrayList<BlockPos> sortedList = new ArrayList<>(blockPosList);

        // Get the player
        PlayerEntity player = mc.player;

        // Sort the list using a custom comparator based on Y positions and proximity to the player
        sortedList.sort(new Comparator<BlockPos>() {
            @Override
            public int compare(BlockPos pos1, BlockPos pos2) {
                World world = player.world;
                Block block1 = pos1.getY() >= 0 ? world.getBlockState(pos1).getBlock() : Blocks.AIR;
                Block block2 = pos2.getY() >= 0 ? world.getBlockState(pos2).getBlock() : Blocks.AIR;

                // Handle air blocks
                if (block1 == Blocks.AIR && block2 == Blocks.AIR) {
                    return 0;
                } else if (block1 == Blocks.AIR) {
                    return 1;
                } else if (block2 == Blocks.AIR) {
                    return -1;
                }

                // Compare Y positions
                int compareByY = Integer.compare(pos2.getY(), pos1.getY());
                if (compareByY != 0) {
                    return compareByY;
                }

                // Compare by squared distance
                double distanceToPos1 = pos1.distanceSq(player.getPosX(), player.getPosY(), player.getPosZ(), false);
                double distanceToPos2 = pos2.distanceSq(player.getPosX(), player.getPosY(), player.getPosZ(), false);
                return Double.compare(distanceToPos1, distanceToPos2);
            }
        });

        return sortedList;
    }
}
