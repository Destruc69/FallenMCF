package paul.fallen.module.modules.world;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.*;
import java.util.stream.IntStream;

public class AutoFill extends Module {

    private BlockPos position;

    public AutoFill(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        try {
            if (position == null || !mc.world.getBlockState(position).isAir()) {
                position = getPosition();
            }
            if (position != null && mc.player.getDistanceSq(Vector3d.copyCentered(position)) <= 4) {
                BlockUtils.placeBlock(position.down(), mc.player.inventory.currentItem, true, true);
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (position != null) {
                RenderUtils.drawOutlinedBox(position, 0, 1, 0, event);
            }
        } catch (Exception ignored) {
        }
    }

    private BlockPos getPosition() {
        Set<BlockPos> visited = new HashSet<>();
        int range = 4;

        return IntStream.rangeClosed(-range, range)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(-range, range)
                        .mapToObj(z -> {
                            BlockPos pos = mc.player.getPosition().add(x, -1, z);
                            if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR && !visited.contains(pos) && isValidHole(pos, visited)) {
                                return pos;
                            }
                            return null;
                        }))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private boolean isValidHole(BlockPos pos, Set<BlockPos> visited) {
        BlockPos below = pos.down();
        return !mc.world.getBlockState(below).isAir() && checkPerimeter(pos, visited);
    }

    private boolean checkPerimeter(BlockPos startPos, Set<BlockPos> visited) {
        List<BlockPos> holeBlocks = findHoles(startPos, visited);

        boolean northSolid = holeBlocks.stream().anyMatch(block -> mc.world.getBlockState(block.north()).getBlock() != Blocks.AIR);
        boolean southSolid = holeBlocks.stream().anyMatch(block -> mc.world.getBlockState(block.south()).getBlock() != Blocks.AIR);
        boolean eastSolid = holeBlocks.stream().anyMatch(block -> mc.world.getBlockState(block.east()).getBlock() != Blocks.AIR);
        boolean westSolid = holeBlocks.stream().anyMatch(block -> mc.world.getBlockState(block.west()).getBlock() != Blocks.AIR);

        return northSolid && southSolid && eastSolid && westSolid;
    }

    private List<BlockPos> findHoles(BlockPos startPos, Set<BlockPos> visited) {
        List<BlockPos> fillBlocks = new ArrayList<>();
        List<BlockPos> queue = new ArrayList<>();
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
        return fillBlocks;
    }
}
