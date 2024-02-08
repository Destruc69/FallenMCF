package paul.fallen.module.modules.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.pathfinder.AStarCustomPathFinder;
import paul.fallen.utils.client.ClientUtils;
import paul.fallen.utils.entity.RotationUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class AutoMine extends Module {

    private boolean isStarted;

    private BlockPos posA;
    private BlockPos posB;

    private ArrayList<BlockPos> blockPosArrayList;

    private int save;
    private boolean a = true;

    public AutoMine(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    public static ArrayList<BlockPos> getAllBlocksBetween(BlockPos posA, BlockPos posB) {
        ArrayList<BlockPos> blockPosList = new ArrayList<>();

        int minX = Math.min(posA.getX(), posB.getX());
        int minY = Math.min(posA.getY(), posB.getY());
        int minZ = Math.min(posA.getZ(), posB.getZ());
        int maxX = Math.max(posA.getX(), posB.getX());
        int maxY = Math.max(posA.getY(), posB.getY());
        int maxZ = Math.max(posA.getZ(), posB.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blockPosList.add(new BlockPos(x, y, z));
                }
            }
        }

        return blockPosList;
    }

    @Override
    public void onEnable() {
        isStarted = false;
        blockPosArrayList = new ArrayList<>();
        posA = new BlockPos(0, 0, 0);
        posB = new BlockPos(0, 0, 0);
        save = 0;

        a = false;

        try {
            ClientUtils.addChatMessage("[AutoMine] Right click Pos A and B.");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDisable() {

    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.objectMouseOver != null;
            BlockPos blockPos = new BlockPos(mc.objectMouseOver.getHitVec().x, mc.objectMouseOver.getHitVec().y, mc.objectMouseOver.getHitVec().z);

            if (!isStarted) {
                a = false;
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    if (posA.getY() == 0 || posB.getY() == 0) {
                        if (posA.getY() == 0) {
                            posA = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            ClientUtils.addChatMessage("Pos A:" + posA);
                        } else {
                            posB = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            ClientUtils.addChatMessage("Pos B: " + posB);
                        }
                    }
                }

                if (posA.getY() != 0 && posB.getY() != 0) {
                    if (!posA.equals(posB)) {
                        blockPosArrayList = getAllBlocksBetween(posA, posB);
                        ClientUtils.addChatMessage("Okay, Engaging!");
                        isStarted = true;
                    } else {
                        ClientUtils.addChatMessage("Pos A and Pos B cannot be the same pos!");
                        posA = new BlockPos(0, 0, 0);
                        posB = new BlockPos(0, 0, 0);
                        blockPosArrayList.clear();
                    }
                }
            } else {
                try {
                    ArrayList<BlockPos> blockPosArraySorted = sortBlockPosByY(blockPosArrayList);

                    blockPosArrayList.removeIf(blockPos1 -> {
                        assert mc.world != null;
                        return mc.world.getBlockState(blockPos1).getBlock().equals(Blocks.AIR);
                    });

                    BlockPos targPos = blockPosArraySorted.get(0);

                    int percentage = (blockPosArrayList.size() * 100) / getAllBlocksBetween(posA, posB).size();
                    if (mc.isSingleplayer()) {
                        mc.ingameGUI.setOverlayMessage(new StringTextComponent(blockPosArraySorted.size() + "/" + getAllBlocksBetween(posA, posB).size() + " | " + percentage + "%"), false);
                    } else {
                        if (save != percentage) {
                            save = percentage;
                            ClientUtils.addChatMessage(blockPosArraySorted.size() + "/" + getAllBlocksBetween(posA, posB).size() + " | " + percentage + "%");
                        }
                    }
                    if (percentage <= 0) {
                        ClientUtils.addChatMessage("[AutoMine] Completed");
                        setState(false);
                    }

                    AStarCustomPathFinder pathfinderAStar = null;

                    if (targPos != null) {
                        // Start
                        if (!a) {
                            pathfinderAStar = new AStarCustomPathFinder(mc.player.getPositionVec(), new Vector3d(targPos.getX(), targPos.getY(), targPos.getZ()));
                            pathfinderAStar.compute();
                            a = true; // Set 'a' to true to initiate pathfinding
                        }

                        if (pathfinderAStar != null) {
                            if (pathfinderAStar.hasReachedEndOfPath()) {
                                pathfinderAStar = new AStarCustomPathFinder(mc.player.getPositionVec(), new Vector3d(targPos.getX(), targPos.getY(), targPos.getZ()));
                                pathfinderAStar.compute();
                            }
                        }

                        if (pathfinderAStar.getPath().size() > 0) {
                            pathfinderAStar.move();
                        }

                        if (mc.player.getDistanceSq(targPos.getX(), targPos.getY(), targPos.getZ()) < 3) {
                            mc.playerController.onPlayerDamageBlock(targPos, Direction.DOWN);
                            mc.player.swingArm(Hand.MAIN_HAND);

                            RotationUtils.rotateTo(new Vector3d(targPos.getX(), targPos.getY(), targPos.getZ()), true);
                        }
                    }
                } catch (Exception ignored) {
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
