package paul.fallen.module.modules.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class Nuker extends Module {

    Setting x;
    Setting yMax;
    Setting yMin;
    Setting z;

    private BlockPos targetPosition;

    public Nuker(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        x = new Setting("X", this, 2, 0, 5);
        yMax = new Setting("Y-Max", this, 2, 0, 5);
        yMin = new Setting("Y-Min", this, 0, 0, 5);
        z = new Setting("Z", this, 2, 0, 5);

        addSetting(x);
        addSetting(yMax);
        addSetting(yMin);
        addSetting(z);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            double playerX = mc.player.lastTickPosX;
            double playerY = mc.player.lastTickPosY;
            double playerZ = mc.player.lastTickPosZ;

            ArrayList<BlockPos> blockPosArrayList = new ArrayList<>(); // Create list outside loops

            for (int xi = (int) -x.dval; xi < x.dval; xi++) {
                double posX = playerX + xi;
                for (int y = (int) -yMin.dval; y < yMax.dval; y++) {
                    double posY = playerY + y;
                    for (int zi = (int) -z.dval; zi < z.dval; zi++) {
                        double posZ = playerZ + zi;
                        BlockPos blockPos = new BlockPos(posX, posY, posZ);
                        blockPosArrayList.add(blockPos);
                    }
                }
            }

            // Sort blockPosArrayList based on distance from player
            blockPosArrayList.sort(new Comparator<BlockPos>() {
                @Override
                public int compare(BlockPos blockPos1, BlockPos blockPos2) {
                    double distance1 = mc.player.getDistanceSq(Vector3d.copyCentered(blockPos1));
                    double distance2 = mc.player.getDistanceSq(Vector3d.copyCentered(blockPos2));
                    return Double.compare(distance1, distance2);
                }
            });

            for (BlockPos blockPos : blockPosArrayList) {
                BlockState blockState = mc.world.getBlockState(blockPos);
                if (!blockState.isAir()) {

                    RotationUtils.rotateTo(new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5));

                    mc.player.swingArm(Hand.MAIN_HAND);

                    mc.playerController.onPlayerDamageBlock(blockPos, Direction.fromAngle(mc.player.rotationYaw));

                    targetPosition = blockPos;
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (targetPosition != null) {
                RenderUtils.drawOutlinedBox(targetPosition, 1, 0, 0, event);
            }
        } catch (Exception ignored) {
        }
    }
}
