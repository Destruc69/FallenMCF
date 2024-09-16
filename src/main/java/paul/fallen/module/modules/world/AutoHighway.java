package paul.fallen.module.modules.world;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AutoHighway extends Module {

    private final Setting delay;
    private List<ActionBlockPos> blockPosList;
    private long lastActionTime = 0L;

    public AutoHighway(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        delay = new Setting("Delay", this, 100, 0, 1000, true);
        addSetting(delay);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            blockPosList = getBlocksPositions();
            mc.player.rotationYaw = roundYaw();

            boolean shouldMoveForward = blockPosList.stream()
                    .allMatch(actionBlockPos -> (actionBlockPos.getAction() == Action.PLACE) != mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir());
            boolean shouldSneak = mc.player.isOnGround() && mc.world.getBlockState(mc.player.getPosition().down()).isAir();

            mc.gameSettings.keyBindForward.setPressed(shouldMoveForward);
            mc.gameSettings.keyBindSneak.setPressed(shouldSneak);

            if (System.currentTimeMillis() - lastActionTime < delay.getValDouble()) {
                return;
            }

            lastActionTime = System.currentTimeMillis();

            blockPosList.removeIf(actionBlockPos ->
                    (actionBlockPos.getAction() == Action.BREAK && mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir()) ||
                            (actionBlockPos.getAction() == Action.PLACE && !mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir())
            );

            if (!blockPosList.isEmpty()) {
                ActionBlockPos blockPos = blockPosList.get(0);
                if (blockPos.getAction() == Action.PLACE) {
                    BlockUtils.placeBlock(blockPos.getBlockPos(), mc.player.inventory.currentItem, true, true);
                } else if (blockPos.getAction() == Action.BREAK) {
                    BlockUtils.breakBlock(blockPos.getBlockPos(), mc.player.inventory.currentItem, true, true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        for (ActionBlockPos blockPos : blockPosList) {
            if (blockPos.getAction() == Action.BREAK) {
                RenderUtils.drawOutlinedBox(blockPos.getBlockPos(), 0, 1, 0, event);
            } else if (blockPos.getAction() == Action.PLACE) {
                RenderUtils.drawOutlinedBox(blockPos.getBlockPos(), 1, 0, 0, event);
            }
        }
    }

    private List<ActionBlockPos> getBlocksPositions() {
        List<ActionBlockPos> bpa = new ArrayList<>();
        BlockPos originPos = mc.player.getPosition().add(0, 0.5, 0);
        Direction facing = mc.player.getHorizontalFacing();

        int[][] positions = {
                {0, 0}, {1, 0}, {-1, 0}, {2, 0}, {-2, 0},
                {2, 1}, {-2, 1}, {0, 1}, {1, 1}, {-1, 1},
                {1, 1, 1}, {-1, 1, 1}, {1, -1, 1}, {-1, -1, 1}
        };

        for (int[] pos : positions) {
            BlockPos placePos = originPos.offset(facing.rotateY(), pos[0]).offset(facing.rotateYCCW(), pos[1]);
            bpa.add(new ActionBlockPos(placePos, Action.PLACE));
        }

        // Add breaking positions
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos breakPos = originPos.add(i, 0, j);
                bpa.add(new ActionBlockPos(breakPos, Action.BREAK));
            }
        }

        List<ActionBlockPos> sortedBlocks = bpa.stream()
                .sorted(Comparator.comparingDouble(actionBlockPos ->
                        mc.player.getDistanceSq(Vector3d.copyCentered(actionBlockPos.getBlockPos()))))
                .collect(Collectors.toList());

        return sortedBlocks;
    }

    private float roundYaw() {
        return (float) (Math.floor((mc.player.rotationYaw + 45) / 90) * 90);
    }

    private enum Action {
        BREAK,
        PLACE
    }

    private class ActionBlockPos {
        private final BlockPos blockPos;
        private final Action action;

        public ActionBlockPos(BlockPos blockPos, Action action) {
            this.blockPos = blockPos;
            this.action = action;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public Action getAction() {
            return action;
        }
    }
}