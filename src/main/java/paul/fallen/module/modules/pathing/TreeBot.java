package paul.fallen.module.modules.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;
import roger.pathfind.main.walk.Walker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class TreeBot extends Module {

    private static final double BREAK_DISTANCE = 2.0; // Distance to start breaking logs
    private BlockPos cachedTreePos = null; // Position of the current tree
    private List<BlockPos> logsToBreak = new ArrayList<>(); // Track the logs to be broken

    public TreeBot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        cachedTreePos = null; // Reset cache when enabled
        logsToBreak.clear(); // Clear the list when module is enabled
        Walker.getInstance().setActive(false); // Deactivate Walker
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase != TickEvent.Phase.START) return;

            // If there are no logs to break, find a new tree
            if (logsToBreak.isEmpty()) {
                cachedTreePos = findFirstLog(); // Find the nearest tree
                if (cachedTreePos != null) {
                    logsToBreak = getEntireTree(cachedTreePos); // Get all logs from the tree
                }
            }

            // Move towards the tree if not close enough
            if (cachedTreePos != null && mc.player.getDistanceSq(Vector3d.copyCentered(cachedTreePos)) > BREAK_DISTANCE * BREAK_DISTANCE) {
                Walker.getInstance().walk(mc.player.getPosition(), cachedTreePos, 10);
            } else {
                // If close enough, break the next log
                breakNextLog();
            }

            // Check if all logs are broken to find the next tree
            if (logsToBreak.isEmpty() && cachedTreePos != null) {
                cachedTreePos = null; // Clear the cached tree position to find a new one
            }

        } catch (Exception ignored) {
        }
    }

    private void breakNextLog() {
        if (!logsToBreak.isEmpty()) {
            BlockPos logToBreak = logsToBreak.remove(0); // Get the next log to break
            BlockUtils.breakBlock(logToBreak, mc.player.inventory.currentItem, true, true); // Break log
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (cachedTreePos != null) {
                RenderUtils.drawOutlinedBox(cachedTreePos, 0, 1, 0, event); // Render cached tree position
            }
        } catch (Exception ignored) {
        }
    }

    public List<BlockPos> getEntireTree(BlockPos stump) {
        List<BlockPos> logs = new ArrayList<>();

        // Check up to 10 blocks above the stump for logs
        for (int y = 0; y < 10; y++) {
            BlockPos currentPos = stump.add(0, y, 0);
            Block block = mc.world.getBlockState(currentPos).getBlock();

            if (isTreeLog(block)) {
                logs.add(currentPos);
            } else {
                break; // Stop once no log is found
            }
        }

        return logs;
    }

    private boolean isTreeLog(Block block) {
        return block == Blocks.ACACIA_LOG || block == Blocks.BIRCH_LOG || block == Blocks.DARK_OAK_LOG
                || block == Blocks.JUNGLE_LOG || block == Blocks.OAK_LOG || block == Blocks.SPRUCE_LOG;
    }

    public BlockPos findFirstLog() {
        BlockPos playerPos = mc.player.getPosition();
        return IntStream.rangeClosed(-25, 25)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(-25, 25)
                        .boxed()
                        .flatMap(z -> IntStream.range(0, mc.world.getHeight())
                                .mapToObj(y -> new BlockPos(playerPos.getX() + x, y, playerPos.getZ() + z))
                        )
                )
                .filter(pos -> isTreeLog(mc.world.getBlockState(pos).getBlock()))
                .min(Comparator.comparingDouble(pos -> pos.distanceSq(playerPos))) // Find the closest tree
                .orElse(null);
    }
}
