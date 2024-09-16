package paul.fallen.module.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.InventoryUtils;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class AutoFarm extends Module {

    private final Set<BlockPos> checkedPositions = new HashSet<>();
    private BlockPos targetPosition;
    private boolean isHarvesting = true;

    public AutoFarm(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase == TickEvent.Phase.START) {
                if (targetPosition == null || !isValidTarget(targetPosition) || mc.player.ticksExisted % 20 == 0) {
                    targetPosition = findNextTargetPosition();
                }

                if (targetPosition != null) {
                    processTargetPosition();
                } else {
                    switchMode();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            RenderUtils.drawOutlinedBox(targetPosition, 0, 1, 0, event);
        } catch (Exception ignored) {
        }
    }

    private void processTargetPosition() {
        BlockState state = mc.world.getBlockState(targetPosition);
        Block block = state.getBlock();

        if (isHarvesting && block instanceof CropsBlock) {
            CropsBlock cropsBlock = (CropsBlock) block;
            if (BlockUtils.isCropFullyGrown(targetPosition.up())) {
                BlockUtils.breakBlock(targetPosition.up(), mc.player.inventory.currentItem, true, true);
                markAsChecked();
            }
        } else if (!isHarvesting && block instanceof FarmlandBlock) {
            int seedSlot = getSeedSlot();
            if (seedSlot != -1) {
                InventoryUtils.setSlot(seedSlot);
                BlockUtils.placeBlock(targetPosition, mc.player.inventory.currentItem, true, true);
                markAsChecked();
            }
        } else {
            targetPosition = null;
        }
    }

    private void markAsChecked() {
        checkedPositions.add(targetPosition);
        targetPosition = null;
    }

    private void switchMode() {
        isHarvesting = !isHarvesting;
        checkedPositions.clear();
    }

    private boolean isValidTarget(BlockPos position) {
        BlockState state = mc.world.getBlockState(position);
        return !state.isAir() && !checkedPositions.contains(position);
    }

    private BlockPos findNextTargetPosition() {
        return IntStream.rangeClosed(-3, 3)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(-3, 3)
                        .boxed()
                        .flatMap(y -> IntStream.rangeClosed(-3, 3)
                                .boxed()
                                .map(z -> mc.player.getPosition().add(x, y, z))
                        ))
                .filter(b -> {
                    BlockState state = mc.world.getBlockState(b);
                    Block block = state.getBlock();
                    return isHarvesting ? block instanceof CropsBlock : block instanceof FarmlandBlock;
                })
                .filter(b -> !checkedPositions.contains(b))
                .findFirst()
                .orElse(null);
    }

    private int getSeedSlot() {
        return IntStream.range(0, 9)
                .mapToObj(i -> mc.player.inventory.getStackInSlot(i))
                .filter(stack -> stack.getItem() instanceof BlockItem)
                .filter(stack -> ((BlockItem) stack.getItem()).getBlock() instanceof CropsBlock)
                .mapToInt(stack -> mc.player.inventory.getSlotFor(stack))
                .findFirst()
                .orElse(-1);
    }
}
