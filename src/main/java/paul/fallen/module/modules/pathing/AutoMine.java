package paul.fallen.module.modules.pathing;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.ArrayList;

public class AutoMine extends Module {

    Setting x1;
    Setting y1;
    Setting z1;
    Setting x2;
    Setting y2;
    Setting z2;
    Setting setone;
    Setting setsecond;
    private ArrayList<BlockPos> blocksToDestroy = new ArrayList<>();
    private int initialSize = 0;

    public AutoMine(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        x1 = new Setting("X-1", this, 0, -32000000, 32000000, true);
        y1 = new Setting("Y-1", this, 64, 0, 255, true);
        z1 = new Setting("Z-1", this, 0, -32000000, 32000000, true);

        addSetting(x1);
        addSetting(y1);
        addSetting(z1);

        x2 = new Setting("X-2", this, 0, -32000000, 32000000, true);
        y2 = new Setting("Y-2", this, 64, 0, 255, true);
        z2 = new Setting("Z-2", this, 0, -32000000, 32000000, true);

        addSetting(x2);
        addSetting(y2);
        addSetting(z2);

        setone = new Setting("SetOne", this, false);
        addSetting(setone);

        setsecond = new Setting("SetSecond", this, false);
        addSetting(setsecond);
    }

    public static ArrayList<BlockPos> getAllBlocksBetween(BlockPos posA, BlockPos posB) {
        ArrayList<BlockPos> blockPosList = new ArrayList<>();

        int minX = Math.min(posA.getX(), posB.getX());
        int minY = Math.min(posA.getY(), posB.getY());
        int minZ = Math.min(posA.getZ(), posB.getZ());
        int maxX = Math.max(posA.getX(), posB.getX());
        int maxY = Math.max(posA.getY(), posB.getY());
        int maxZ = Math.max(posA.getZ(), posB.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blockPosList.add(new BlockPos(x, y, z));
                }
            }
        }

        return blockPosList;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (setone.getValBoolean() || setsecond.getValBoolean())
            return;

        blocksToDestroy = getAllBlocksBetween(new BlockPos(x1.getValDouble(), y1.getValDouble(), z1.getValDouble()), new BlockPos(x2.getValDouble(), y2.getValDouble(), z2.getValDouble()));

        blocksToDestroy.sort((pos1, pos2) -> {
            double distSq1 = mc.player.getDistanceSq(pos1.getX(), pos1.getY(), pos1.getZ());
            double distSq2 = mc.player.getDistanceSq(pos2.getX(), pos2.getY(), pos2.getZ());

            // If distances are equal, compare directions
            if (distSq1 == distSq2) {
                Direction dir1 = getDirection(mc.player.getPosition(), pos1);
                Direction dir2 = getDirection(mc.player.getPosition(), pos2);
                return dir1.compareTo(dir2);
            }

            // Otherwise, compare distances
            return Double.compare(distSq1, distSq2);
        });

        initialSize = blocksToDestroy.size();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (setone.getValBoolean()) {
                // Update position settings and reset path
                x1.setValDouble(mc.player.getPosX());
                y1.setValDouble(mc.player.getPosY());
                z1.setValDouble(mc.player.getPosZ());
                setone.setValBoolean(false);
                setState(false);
                return;
            }

            if (setsecond.getValBoolean()) {
                // Update position settings and reset path
                x2.setValDouble(mc.player.getPosX());
                y2.setValDouble(mc.player.getPosY());
                z2.setValDouble(mc.player.getPosZ());
                setsecond.setValBoolean(false);
                setState(false);
                return;
            }

            if (setone.getValBoolean() || setsecond.getValBoolean())
                return;

            blocksToDestroy.removeIf(blockPos -> mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR));

            if (!blocksToDestroy.isEmpty()) {
                BlockPos targetPos = blocksToDestroy.get(0);
                double[] rot = getYP(targetPos);

                mc.player.rotationYaw = (float) rot[0];
                mc.player.rotationPitch = (float) rot[1];

                if (mc.player.swingProgress <= 0) {
                    mc.gameSettings.keyBindForward.setPressed(mc.player.getDistanceSq(targetPos.getX(), targetPos.getY(), targetPos.getZ()) > 4);
                } else {
                    mc.gameSettings.keyBindForward.setPressed(false);
                }
                mc.gameSettings.keyBindAttack.setPressed(true);
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (!blocksToDestroy.isEmpty()) {
            int screenWidth = mc.getMainWindow().getScaledWidth();
            int screenHeight = mc.getMainWindow().getScaledHeight();

            // Inside your rendering method
            int destroyedBlocks = initialSize - blocksToDestroy.size(); // Calculate the number of destroyed blocks
            double percentage = Math.round((double) destroyedBlocks / initialSize * 100); // Calculate the percentage

            UIUtils.drawTextOnScreenWithShadow(blocksToDestroy.size() + "/" + initialSize + " [" + percentage + "%]", screenWidth / 3, screenHeight / 3, Color.WHITE.getRGB());
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (blocksToDestroy.size() > 0) {
            for (int i = 0; i < blocksToDestroy.size(); i++) {
                BlockPos blockPos = blocksToDestroy.get(i);

                // Calculate color components for a gradient from green to red
                float green = (float) Math.min(1.0, Math.max(0.0, i / (float) blocksToDestroy.size()));  // Green component
                float red = 1.0f - green; // Red component
                float blue = 0.0f; // Blue component

                // Render the box with the calculated color
                RenderUtils.drawOutlinedBox(blockPos, red, green, blue, event);
            }
        }
    }

    private Direction getDirection(BlockPos source, BlockPos destination) {
        int dx = destination.getX() - source.getX();
        int dy = destination.getY() - source.getY();
        int dz = destination.getZ() - source.getZ();

        if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else if (Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > Math.abs(dz)) {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private double[] getYP(BlockPos pos) {
        double var4 = pos.getX() + 0.25D - mc.player.getPosX();
        double var6 = pos.getZ() + 0.25D - mc.player.getPosZ();
        double var8 = pos.getY() + 0.25D - (mc.player.getPosY() + mc.player.getEyeHeight());
        double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
        double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
        double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
        return new double[]{yaw, pitch};
    }
}
