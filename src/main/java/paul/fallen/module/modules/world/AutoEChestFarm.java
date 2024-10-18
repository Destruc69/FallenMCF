package paul.fallen.module.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.InventoryUtils;
import paul.fallen.utils.entity.PlayerUtils;
import paul.fallen.utils.world.BlockUtils;

public class AutoEChestFarm extends Module {

    private BlockPos targetPosition;

    public AutoEChestFarm(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        try {
            targetPosition = getTargetPosition();
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase != TickEvent.Phase.START || targetPosition == null) return;

            Block blockAtTarget = mc.world.getBlockState(targetPosition).getBlock();

            if (blockAtTarget != Blocks.ENDER_CHEST) {
                // Place the ender chest
                InventoryUtils.setSlot(PlayerUtils.geSlotHotbar(Items.ENDER_CHEST));
                BlockUtils.placeBlock(targetPosition.down(), mc.player.inventory.currentItem, true, true);
            } else {
                // Break the ender chest
                InventoryUtils.setSlot(PlayerUtils.getSlotHotbarBestPickaxe());
                BlockUtils.breakBlock(targetPosition, mc.player.inventory.currentItem, true, true);
            }
        } catch (Exception ignored) {
        }
    }

    private BlockPos getTargetPosition() {
        BlockPos[] possiblePositions = new BlockPos[]{
                mc.player.getPosition().add(1, 0, 0),
                mc.player.getPosition().add(-1, 0, 0),
                mc.player.getPosition().add(0, 0, 1),
                mc.player.getPosition().add(0, 0, -1)
        };

        for (BlockPos pos : possiblePositions) {
            if (isValid(pos)) {
                return pos;
            }
        }

        return new BlockPos(0, 0, 0);
    }

    private boolean isValid(BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).isAir() && !mc.world.getBlockState(blockPos.down()).isAir();
    }
}
