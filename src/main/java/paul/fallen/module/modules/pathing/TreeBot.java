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

    private static final double BREAK_DISTANCE = 5.0; // Distance to start breaking logs
    private static final int MAX_LOG_HEIGHT = 5; // Max log height to break
    private BlockPos cachedTreePos = null; // Position of the current tree
    private List<BlockPos> logsToBreak = new ArrayList<>(); // Track the logs to be broken
    private boolean isWalkingToTree = false; // Track if the bot is already walking to the tree
    private boolean isTreeReached = false; // Track if the tree has been reached

    public TreeBot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        cachedTreePos = null; // Reset cache when enabled
        logsToBreak.clear(); // Clear the list when module is enabled
        isWalkingToTree = false; // Reset walking state
        isTreeReached = false; // Reset tree reached state
        Walker.getInstance().setActive(false); // Deactivate Walker
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.player == null || mc.world == null) return;

        // If there are no logs to break, find a new tree
        if (logsToBreak.isEmpty() && cachedTreePos == null) {
            cachedTreePos = findFirstLog(); // Find the nearest tree
            if (cachedTreePos != null) {
                logsToBreak = getEntireTree(cachedTreePos); // Get all logs from the tree
                isWalkingToTree = false; // Reset walking state when a new tree is found
                isTreeReached = false; // Reset tree reached state
            }
        }

        // Move towards the tree if not close enough
        if (cachedTreePos != null && !isTreeReached) {
            if (mc.player.getDistanceSq(Vector3d.copyCentered(cachedTreePos)) > BREAK_DISTANCE * BREAK_DISTANCE) {
                // Check if we are already walking
                if (!isWalkingToTree || !Walker.getInstance().isActive()) {
                    // If Walker is inactive and we are not within the break distance, keep walking
                    Walker.getInstance().walk(mc.player.getPosition(), cachedTreePos, 10); // Walk to tree again
                    isWalkingToTree = true; // Set walking flag
                }
            } else if (!Walker.getInstance().isActive()) {
                // Ensure the player is at the tree when Walker stops
                double distanceToTree = mc.player.getDistanceSq(Vector3d.copyCentered(cachedTreePos));
                if (distanceToTree <= BREAK_DISTANCE * BREAK_DISTANCE) {
                    isTreeReached = true; // Tree is reached, start breaking logs
                    isWalkingToTree = false; // Reset walking flag
                } else {
                    // If Walker stopped but we're not at the tree, start walking again
                    Walker.getInstance().walk(mc.player.getPosition(), cachedTreePos, 10);
                }
            }
        }

        // Start breaking logs only when tree is reached and Walker is inactive
        if (isTreeReached && !logsToBreak.isEmpty() && !Walker.getInstance().isActive()) {
            BlockPos logPos = logsToBreak.get(0); // Get the first log in the list

            if (mc.world.getBlockState(logPos).getBlock() == Blocks.AIR) {
                // If the block is already broken, remove it from the list
                logsToBreak.remove(0);
            } else {
                // Check if the log is within the breakable height
                if (logPos.getY() - cachedTreePos.getY() <= MAX_LOG_HEIGHT) {
                    // Continuously try to break the log until it's fully broken
                    BlockUtils.breakBlock(logPos, mc.player.inventory.currentItem, true, true);
                } else {
                    // Skip breaking this log as it's too high
                    logsToBreak.remove(0);
                }
            }
        }

        // If all logs are broken, find the next tree
        if (logsToBreak.isEmpty() && cachedTreePos != null) {
            cachedTreePos = null; // Clear the cached tree position to find a new one
            isWalkingToTree = false; // Reset walking state when the tree is done
            isTreeReached = false; // Reset tree reached state
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (cachedTreePos != null) {
            RenderUtils.drawOutlinedBox(cachedTreePos, 0, 1, 0, event); // Render cached tree position
        }
    }

    public List<BlockPos> getEntireTree(BlockPos stump) {
        List<BlockPos> logs = new ArrayList<>();

        // Check up to MAX_LOG_HEIGHT blocks above the stump for logs
        for (int y = 0; y < MAX_LOG_HEIGHT; y++) {
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
