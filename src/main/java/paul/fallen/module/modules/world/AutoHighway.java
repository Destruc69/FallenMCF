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

    private final Setting width;
    private final Setting height;

    private List<ActionBlockPos> blockPosList;
    private long lastActionTime = 0L;

    public AutoHighway(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        width = new Setting("Width", this, 3, 1, 5, true);
        height = new Setting("Height", this, 3, 2, 5, true);

        addSetting(width);
        addSetting(height);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        try {
            blockPosList = getBlocksPositions();
            mc.player.rotationYaw = roundYaw();

            boolean allPlaced = blockPosList.stream()
                    .allMatch(actionBlockPos -> (actionBlockPos.getAction() == Action.PLACE) != mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir());

            mc.gameSettings.keyBindForward.setPressed(allPlaced);
            mc.gameSettings.keyBindSneak.setPressed(mc.player.isOnGround() && mc.world.getBlockState(mc.player.getPosition().down()).isAir());

            // Throttle actions based on delay
            if (System.currentTimeMillis() - lastActionTime < 100) return;
            lastActionTime = System.currentTimeMillis();

            // Remove invalid positions
            blockPosList.removeIf(actionBlockPos ->
                    (actionBlockPos.getAction() == Action.BREAK && mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir()) ||
                            (actionBlockPos.getAction() == Action.PLACE && !mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir())
            );

            // Process the first action
            ActionBlockPos actionBlockPos = blockPosList.get(0);
            if (actionBlockPos.getAction() == Action.PLACE) {
                BlockUtils.placeBlock(actionBlockPos.getBlockPos(), mc.player.inventory.currentItem, true, true);
            } else if (actionBlockPos.getAction() == Action.BREAK) {
                BlockUtils.breakBlock(actionBlockPos.getBlockPos(), mc.player.inventory.currentItem, true, true);
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        getBlocksPositions().forEach(blockPos -> {
            float red = blockPos.getAction() == Action.PLACE ? 1 : 0;
            float green = blockPos.getAction() == Action.BREAK ? 1 : 0;
            RenderUtils.drawOutlinedBox(blockPos.getBlockPos(), red, green, 0, event);
        });
    }

    private List<ActionBlockPos> getBlocksPositions() {
        List<ActionBlockPos> positions = new ArrayList<>();
        BlockPos origin = mc.player.getPosition().add(0, 0.5, 0);
        Direction facing = mc.player.getHorizontalFacing();

        // Add block positions based on player's facing direction and configurable width/height
        addFacingPositions(origin, facing, positions);

        // Add positions for breaking surrounding blocks
        addSurroundingBreakPositions(origin, positions);

        // Sort break positions by distance and append placement actions
        return positions.stream()
                .sorted(Comparator.comparingDouble(p -> mc.player.getDistanceSq(Vector3d.copyCentered(p.getBlockPos()))))
                .collect(Collectors.toList());
    }

    private void addFacingPositions(BlockPos origin, Direction facing, List<ActionBlockPos> positions) {
        BlockPos down = origin.down();

        int w = (int) width.getValDouble();
        int h = (int) height.getValDouble();

        int halfWidth = w / 2;

        for (int i = -halfWidth; i <= halfWidth; i++) {
            for (int j = 0; j < h; j++) {
                if (facing == Direction.EAST || facing == Direction.WEST) {
                    BlockPos pos = down.north(i).up(j);
                    positions.add(new ActionBlockPos(pos, Action.PLACE));
                } else if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    BlockPos pos = down.east(i).up(j);
                    positions.add(new ActionBlockPos(pos, Action.PLACE));
                }
            }
        }
    }

    private void addSurroundingBreakPositions(BlockPos origin, List<ActionBlockPos> positions) {
        // Add positions around the player for breaking
        BlockPos[] surrounding = {
                origin, origin.north(), origin.south(), origin.east(), origin.west(),
                origin.up(), origin.up().north(), origin.up().south(), origin.up().east(), origin.up().west()
        };
        for (BlockPos pos : surrounding) {
            positions.add(new ActionBlockPos(pos, Action.BREAK));
        }
    }

    private float roundYaw() {
        return (float) (Math.floor((mc.player.rotationYaw + 45) / 90) * 90);
    }

    private enum Action {
        BREAK, PLACE
    }

    private static class ActionBlockPos {
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