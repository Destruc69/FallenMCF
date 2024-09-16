package paul.fallen.module.modules.world;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AutoHighway extends Module {

    private final Setting delay;

    private ArrayList<ActionBlockPos> blockPosArrayList;
    private long lastActionTime = 0L;

    public AutoHighway(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        delay = new Setting("Delay", this, 100, 0, 1000, true);
        addSetting(delay);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase == TickEvent.Phase.START) {
                blockPosArrayList = getBlocksPositions();

                mc.player.rotationYaw = roundYaw();

                mc.gameSettings.keyBindForward.setPressed(blockPosArrayList.stream().allMatch(actionBlockPos -> (actionBlockPos.getAction() == Action.PLACE) != mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir()));
                mc.gameSettings.keyBindSneak.setPressed(mc.player.isOnGround() && mc.world.getBlockState(mc.player.getPosition().down()).isAir());

                // Skip if less than 1 second has passed since the last action
                if (System.currentTimeMillis() - lastActionTime < delay.getValDouble()) {
                    return;
                }

                // Set the last action time to the current time
                lastActionTime = System.currentTimeMillis();

                blockPosArrayList.removeIf(actionBlockPos ->
                        (actionBlockPos.getAction() == Action.BREAK && mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir()) ||
                                (actionBlockPos.getAction() == Action.PLACE && !mc.world.getBlockState(actionBlockPos.getBlockPos()).isAir())
                );

                ActionBlockPos blockPos = blockPosArrayList.get(0);

                if (blockPos.action == Action.PLACE) {
                    place(blockPos.blockPos);
                } else if (blockPos.action == Action.BREAK) {
                    breakPos(blockPos.blockPos);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void place(BlockPos blockPos) {
        float[] angle = getRotationsBlock(blockPos, mc.player.getHorizontalFacing());

        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(angle[0], angle[1], mc.player.isOnGround()));

        mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0, 0, 0), mc.player.getHorizontalFacing(), blockPos, false));

        mc.player.swingArm(Hand.MAIN_HAND);
    }

    private void breakPos(BlockPos blockPos) {
        RotationUtils.rotateTo(new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5));

        mc.playerController.onPlayerDamageBlock(blockPos, mc.player.getHorizontalFacing());

        mc.player.swingArm(Hand.MAIN_HAND);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        for (ActionBlockPos blockPos : getBlocksPositions()) {
            if (blockPos.action == Action.BREAK) {
                RenderUtils.drawOutlinedBox(blockPos.blockPos, 0, 1, 0, event);
            } else if (blockPos.action == Action.PLACE) {
                RenderUtils.drawOutlinedBox(blockPos.blockPos, 1, 0, 0, event);
            }
        }
    }

    private ArrayList<ActionBlockPos> getBlocksPositions() {
        ArrayList<ActionBlockPos> bpa = new ArrayList<>();
        final BlockPos orignPos = mc.player.getPosition().add(0, 0.5, 0);
        switch (mc.player.getHorizontalFacing()) {
            case EAST: {
                bpa.add(new ActionBlockPos(orignPos.down(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().north(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().south(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().north().north(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().south().south(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().north().north().up(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().south().south().up(), Action.PLACE));

                bpa.add(new ActionBlockPos(orignPos.down().north().north().up().up(), Action.BREAK));
                bpa.add(new ActionBlockPos(orignPos.down().south().south().up().up(), Action.BREAK));

                break;
            }
            case NORTH: {
                bpa.add(new ActionBlockPos(orignPos.down(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().east(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().west(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().east().east(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().west().west(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().east().east().up(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().west().west().up(), Action.PLACE));

                bpa.add(new ActionBlockPos(orignPos.down().east().east().up().up(), Action.BREAK));
                bpa.add(new ActionBlockPos(orignPos.down().west().west().up().up(), Action.BREAK));

                break;
            }
            case SOUTH: {
                bpa.add(new ActionBlockPos(orignPos.down(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().east(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().west(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().east().east(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().west().west(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().east().east().up(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().west().west().up(), Action.PLACE));

                bpa.add(new ActionBlockPos(orignPos.down().east().east().up().up(), Action.BREAK));
                bpa.add(new ActionBlockPos(orignPos.down().west().west().up().up(), Action.BREAK));

                break;
            }
            case WEST: {
                bpa.add(new ActionBlockPos(orignPos.down(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().north(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().south(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().north().north(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().south().south(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().north().north().up(), Action.PLACE));
                bpa.add(new ActionBlockPos(orignPos.down().south().south().up(), Action.PLACE));

                bpa.add(new ActionBlockPos(orignPos.down().north().north().up().up(), Action.BREAK));
                bpa.add(new ActionBlockPos(orignPos.down().south().south().up().up(), Action.BREAK));

                break;
            }
        }
        bpa.add(new ActionBlockPos(orignPos, Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.north(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.north().east(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.north().west(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.east(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.south(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.south().east(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.south().west(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.west(), Action.BREAK));

        bpa.add(new ActionBlockPos(orignPos.up(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.up().north(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.up().north().east(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.up().north().west(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.up().east(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.up().south(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.up().south().east(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.up().south().west(), Action.BREAK));
        bpa.add(new ActionBlockPos(orignPos.up().west(), Action.BREAK));


        // Calculate distances for breaking positions and sort them
        List<ActionBlockPos> breakingPositions = bpa.stream()
                .filter(actionBlockPos -> actionBlockPos.getAction() == Action.BREAK)
                .sorted(Comparator.comparingDouble(actionBlockPos -> mc.player.getDistanceSq(Vector3d.copyCentered(actionBlockPos.getBlockPos()))))
                .collect(Collectors.toList());

        // Add placing positions to the end of the list
        breakingPositions.addAll(bpa.stream().filter(actionBlockPos -> actionBlockPos.getAction() == Action.PLACE).collect(Collectors.toList()));

        return new ArrayList<>(breakingPositions);
        //return bpa;
    }

    private float roundYaw() {
        return (float) (Math.floor((mc.player.rotationYaw + 45) / 90) * 90);
    }

    private enum Action {
        BREAK,
        PLACE
    }

    private float[] getRotationsBlock(BlockPos block, Direction face) {
        assert mc.player != null;
        double x = (double) block.getX() + 0.5 - mc.player.getPosX() + (double) face.getXOffset() / 2.0;
        double z = (double) block.getZ() + 0.5 - mc.player.getPosZ() + (double) face.getZOffset() / 2.0;
        double y = (double) block.getY() + 0.5;
        double d1 = mc.player.getPosY() + (double) mc.player.getEyeHeight() - y;
        double d3 = MathHelper.sqrt(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
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