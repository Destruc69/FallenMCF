package paul.fallen.module.modules.world;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

public class Nuker extends Module {

    Setting legit;
    Setting x;
    Setting yMax;
    Setting yMin;
    Setting z;

    private BlockPos targetPosition;

    public Nuker(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        legit = new Setting("Legit", this, false);
        x = new Setting("X", this, 2, 0, 5, true);
        yMax = new Setting("Y-Max", this, 2, 0, 5, true);
        yMin = new Setting("Y-Min", this, 0, 0, 5, true);
        z = new Setting("Z", this, 2, 0, 5, true);

        addSetting(legit);
        addSetting(x);
        addSetting(yMax);
        addSetting(yMin);
        addSetting(z);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase == TickEvent.Phase.START) {
                if (targetPosition == null || mc.world.getBlockState(targetPosition).getBlock().equals(Blocks.AIR)) {
                    targetPosition = getTargetPosition();
                    if (legit.getValBoolean()) {
                        mc.gameSettings.keyBindAttack.setPressed(false);
                    }
                } else {
                    if (!legit.getValBoolean()) {
                        BlockUtils.breakBlock(targetPosition, mc.player.inventory.currentItem, true, true);
                    } else {
                        float[] rot = getRotationsBlock(targetPosition, mc.player.getHorizontalFacing());
                        mc.player.rotationYaw = rot[0];
                        mc.player.rotationPitch = rot[1];
                        mc.gameSettings.keyBindAttack.setPressed(true);
                    }
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

    private BlockPos getTargetPosition() {
        double playerX = mc.player.lastTickPosX;
        double playerY = mc.player.lastTickPosY;
        double playerZ = mc.player.lastTickPosZ;

        ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();

        for (int xi = (int) -x.getValDouble(); xi < x.getValDouble(); xi++) {
            double posX = playerX + xi;
            for (int y = (int) -yMin.getValDouble(); y < yMax.getValDouble(); y++) {
                double posY = playerY + y;
                for (int zi = (int) -z.getValDouble(); zi < z.getValDouble(); zi++) {
                    double posZ = playerZ + zi;
                    BlockPos blockPos = new BlockPos(posX, posY, posZ);
                    blockPosArrayList.add(blockPos);
                }
            }
        }

        if (blockPosArrayList.size() > 0) {
            // Sort blockPosArrayList based on distance from player
            blockPosArrayList.sort(new Comparator<BlockPos>() {
                @Override
                public int compare(BlockPos blockPos1, BlockPos blockPos2) {
                    double distance1 = mc.player.getDistanceSq(Vector3d.copyCentered(blockPos1));
                    double distance2 = mc.player.getDistanceSq(Vector3d.copyCentered(blockPos2));
                    return Double.compare(distance1, distance2);
                }
            });


            blockPosArrayList.removeIf(blockPos -> mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR));

            return blockPosArrayList.get(0);
        } else {
            return new BlockPos(0, 0, 0);
        }
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