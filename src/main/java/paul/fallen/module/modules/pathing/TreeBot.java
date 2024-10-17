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

    private static final double BREAK_DISTANCE = 5.0;
    private static final int MAX_LOG_HEIGHT = 5;
    private BlockPos cachedTreePos = null;
    private List<BlockPos> logsToBreak = new ArrayList<>();
    private boolean isWalkingToTree = false;
    private boolean isTreeReached = false;

    public TreeBot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        try {
            cachedTreePos = null;
            logsToBreak.clear();
            isWalkingToTree = false;
            isTreeReached = false;
            Walker.getInstance().setActive(false);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.player == null || mc.world == null) return;

        try {
            if (logsToBreak.isEmpty() && cachedTreePos == null) {
                cachedTreePos = findFirstLog();
                if (cachedTreePos != null) {
                    logsToBreak = getEntireTree(cachedTreePos);
                    isWalkingToTree = false;
                    isTreeReached = false;
                }
            }

            if (cachedTreePos != null && !isTreeReached) {
                double distanceToTree = mc.player.getDistanceSq(Vector3d.copyCentered(cachedTreePos));
                if (distanceToTree > BREAK_DISTANCE * BREAK_DISTANCE) {
                    if (!isWalkingToTree || !Walker.getInstance().isActive()) {
                        Walker.getInstance().walk(mc.player.getPosition(), cachedTreePos, 10);
                        isWalkingToTree = true;
                    }
                } else if (!Walker.getInstance().isActive()) {
                    if (distanceToTree <= BREAK_DISTANCE * BREAK_DISTANCE) {
                        isTreeReached = true;
                        isWalkingToTree = false;
                    } else {
                        Walker.getInstance().walk(mc.player.getPosition(), cachedTreePos, 10);
                    }
                }
            }

            if (isTreeReached && !logsToBreak.isEmpty() && !Walker.getInstance().isActive()) {
                BlockPos logPos = logsToBreak.get(0);

                if (mc.world.getBlockState(logPos).getBlock() == Blocks.AIR) {
                    logsToBreak.remove(0);
                } else {
                    if (logPos.getY() - cachedTreePos.getY() <= MAX_LOG_HEIGHT) {
                        BlockUtils.breakBlock(logPos, mc.player.inventory.currentItem, true, true);
                    } else {
                        logsToBreak.remove(0);
                    }
                }
            }

            if (logsToBreak.isEmpty() && cachedTreePos != null) {
                cachedTreePos = null;
                isWalkingToTree = false;
                isTreeReached = false;
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (cachedTreePos != null) {
            RenderUtils.drawOutlinedBox(cachedTreePos, 0, 1, 0, event);
        }
    }

    public List<BlockPos> getEntireTree(BlockPos stump) {
        List<BlockPos> logs = new ArrayList<>();

        for (int y = 0; y < MAX_LOG_HEIGHT; y++) {
            BlockPos currentPos = stump.add(0, y, 0);
            Block block = mc.world.getBlockState(currentPos).getBlock();

            if (isTreeLog(block)) {
                logs.add(currentPos);
            } else {
                break;
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
                .min(Comparator.comparingDouble(pos -> pos.distanceSq(playerPos)))
                .orElse(null);
    }
}