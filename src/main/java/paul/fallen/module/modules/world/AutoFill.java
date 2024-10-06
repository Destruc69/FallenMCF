package paul.fallen.module.modules.world;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.PlayerUtils;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AutoFill extends Module {

    private ArrayList<BlockPos> fillBlocks;

    public AutoFill(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        try {
            if (fillBlocks == null || fillBlocks.isEmpty()) {
                fillBlocks = getFillBlocks();
            } else {
                fillBlocks.removeIf(blockPos -> !mc.world.getBlockState(blockPos).isAir());
                BlockPos t = fillBlocks.get(0);
                if (mc.player.ticksExisted % 5 == 0) {
                    BlockUtils.placeBlock(t.down(), PlayerUtils.geBlockItemSlotHotBar(), true, true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            for (BlockPos blockPos : getFillBlocks()) {
                RenderUtils.drawOutlinedBox(blockPos, 0, 1, 0, event);
            }
        } catch (Exception ignored) {
        }
    }

    private ArrayList<BlockPos> getFillBlocks() {
        ArrayList<BlockPos> fillBlocks = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();

        int range = 5;

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                BlockPos pos = mc.player.getPosition().add(x, -1, z);
                if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR && !visited.contains(pos)) {
                    if (isValidHole(pos, visited)) {
                        findHole(pos, visited, fillBlocks);
                    }
                }
            }
        }
        return fillBlocks;
    }

    private boolean isValidHole(BlockPos pos, Set<BlockPos> visited) {
        BlockPos below = pos.down();
        if (!mc.world.getBlockState(below).isAir()) {
            return checkPerimeter(pos, visited);
        }
        return false;
    }

    private boolean checkPerimeter(BlockPos startPos, Set<BlockPos> visited) {
        boolean northSolid = false;
        boolean southSolid = false;
        boolean eastSolid = false;
        boolean westSolid = false;

        ArrayList<BlockPos> holeBlocks = new ArrayList<>();
        findHole(startPos, visited, holeBlocks);

        for (BlockPos block : holeBlocks) {
            BlockPos north = block.north();
            BlockPos south = block.south();
            BlockPos east = block.east();
            BlockPos west = block.west();

            if (mc.world.getBlockState(north).getBlock() != Blocks.AIR) {
                northSolid = true;
            }
            if (mc.world.getBlockState(south).getBlock() != Blocks.AIR) {
                southSolid = true;
            }
            if (mc.world.getBlockState(east).getBlock() != Blocks.AIR) {
                eastSolid = true;
            }
            if (mc.world.getBlockState(west).getBlock() != Blocks.AIR) {
                westSolid = true;
            }
        }

        return northSolid && southSolid && eastSolid && westSolid;
    }

    private void findHole(BlockPos startPos, Set<BlockPos> visited, ArrayList<BlockPos> fillBlocks) {
        ArrayList<BlockPos> queue = new ArrayList<>();
        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.remove(0);
            fillBlocks.add(current);

            for (BlockPos offset : new BlockPos[]{
                    current.add(-1, 0, 0),
                    current.add(1, 0, 0),
                    current.add(0, 0, -1),
                    current.add(0, 0, 1)
            }) {
                if (mc.world.getBlockState(offset).getBlock() == Blocks.AIR && visited.add(offset)) {
                    queue.add(offset);
                }
            }
        }
    }
}
