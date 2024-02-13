package paul.fallen.module.modules.world;

import net.minecraft.block.Block;
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
import paul.fallen.module.Module;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class AutoHighway extends Module {

    private int currentIndex = 0;
    private ArrayList<BlockPos> blockPosArrayList;
    private final long delay = 100; // Set your desired delay in milliseconds
    private long lastActionTime = 0;

    public AutoHighway(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (blockPosArrayList == null || blockPosArrayList.isEmpty()) {
                blockPosArrayList = getBlocksPositions();
                blockPosArrayList.sort(Comparator.comparingDouble(blockPos -> mc.player.getDistanceSq(Vector3d.copyCentered(blockPos))));
                currentIndex = 0;
            }

            mc.gameSettings.keyBindForward.setPressed(getBlocksPositions().stream().allMatch(pos -> !mc.world.getBlockState(pos).isAir()));
            mc.gameSettings.keyBindSneak.setPressed(mc.world.getBlockState(mc.player.getPosition().down()).isAir() && mc.player.isOnGround());

            if (currentIndex >= blockPosArrayList.size()) {
                // All blocks processed, reset and return
                currentIndex = 0;
                blockPosArrayList = null;
                return;
            }

            BlockPos blockPos = blockPosArrayList.get(currentIndex);

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastActionTime < delay) {
                return; // If the delay hasn't passed yet, return
            }

            lastActionTime = currentTime;

            if (mc.world.getBlockState(blockPos).isAir()) {
                place(blockPos);
            } else if (mc.world.getBlockState(blockPos).getBlock() != Block.getBlockFromItem(mc.player.getHeldItem(Hand.MAIN_HAND).getItem())) {
                breakPos(blockPos);
            } else {
                // Move to the next block if the current one matches
                currentIndex++;
            }
        } catch (Exception ignored) {
        }
    }

    private void place(BlockPos blockPos) {
        float[] angle = getRotationsBlock(blockPos, mc.player.getHorizontalFacing().getOpposite());

        mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0, 0, 0), mc.player.getHorizontalFacing(), blockPos, false));

        mc.player.swingArm(Hand.MAIN_HAND);

        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(angle[0], angle[1], mc.player.isOnGround()));

        // Move to the next block after placing
        currentIndex++;
    }

    private void breakPos(BlockPos blockPos) {
        mc.playerController.onPlayerDamageBlock(blockPos, Direction.fromAngle(mc.player.rotationYaw));

        mc.player.swingArm(Hand.MAIN_HAND);

        RotationUtils.rotateTo(new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5));

        // Move to the next block after breaking
        currentIndex++;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        for (BlockPos blockPos : getBlocksPositions()) {
            RenderUtils.drawOutlinedBox(blockPos, 0, 1, 0, event);
        }
    }

    private ArrayList<BlockPos> getBlocksPositions() {
        ArrayList<BlockPos> bpa = new ArrayList<>();
        final BlockPos orignPos = mc.player.getPosition().add(0, 0.5, 0);
        switch (mc.player.getHorizontalFacing()) {
            case EAST: {
                bpa.add(orignPos.down());
                bpa.add(orignPos.down());
                bpa.add(orignPos.down().north());
                bpa.add(orignPos.down().south());
                bpa.add(orignPos.down().north().north());
                bpa.add(orignPos.down().south().south());
                bpa.add(orignPos.down().north().north().up());
                bpa.add(orignPos.down().south().south().up());
                break;
            }
            case NORTH: {
                bpa.add(orignPos.down());
                bpa.add(orignPos.down());
                bpa.add(orignPos.down().east());
                bpa.add(orignPos.down().west());
                bpa.add(orignPos.down().east().east());
                bpa.add(orignPos.down().west().west());
                bpa.add(orignPos.down().east().east().up());
                bpa.add(orignPos.down().west().west().up());
                break;
            }
            case SOUTH: {
                bpa.add(orignPos.down());
                bpa.add(orignPos.down());
                bpa.add(orignPos.down().east());
                bpa.add(orignPos.down().west());
                bpa.add(orignPos.down().east().east());
                bpa.add(orignPos.down().west().west());
                bpa.add(orignPos.down().east().east().up());
                bpa.add(orignPos.down().west().west().up());
                break;
            }
            case WEST: {
                bpa.add(orignPos.down());
                bpa.add(orignPos.down());
                bpa.add(orignPos.down().north());
                bpa.add(orignPos.down().south());
                bpa.add(orignPos.down().north().north());
                bpa.add(orignPos.down().south().south());
                bpa.add(orignPos.down().north().north().up());
                bpa.add(orignPos.down().south().south().up());
                break;
            }
        }
        return bpa;
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
}

