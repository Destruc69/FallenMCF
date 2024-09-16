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

    private final Setting x1;
    private final Setting y1;
    private final Setting z1;
    private final Setting x2;
    private final Setting y2;
    private final Setting z2;
    private final Setting setone;
    private final Setting setsecond;
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

        if (setone.getValBoolean() || setsecond.getValBoolean()) {
            return;
        }

        blocksToDestroy = getAllBlocksBetween(
                new BlockPos(x1.getValDouble(), y1.getValDouble(), z1.getValDouble()),
                new BlockPos(x2.getValDouble(), y2.getValDouble(), z2.getValDouble())
        );

        blocksToDestroy.sort((pos1, pos2) -> {
            double distSq1 = mc.player.getDistanceSq(pos1.getX(), pos1.getY(), pos1.getZ());
            double distSq2 = mc.player.getDistanceSq(pos2.getX(), pos2.getY(), pos2.getZ());

            if (distSq1 == distSq2) {
                Direction dir1 = getDirection(mc.player.getPosition(), pos1);
                Direction dir2 = getDirection(mc.player.getPosition(), pos2);
                return dir1.compareTo(dir2);
            }

            return Double.compare(distSq1, distSq2);
        });

        initialSize = blocksToDestroy.size();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        try {
            if (setone.getValBoolean()) {
                x1.setValDouble(mc.player.getPosX());
                y1.setValDouble(mc.player.getPosY());
                z1.setValDouble(mc.player.getPosZ());
                setone.setValBoolean(false);
                setState(false);
                return;
            }

            if (setsecond.getValBoolean()) {
                x2.setValDouble(mc.player.getPosX());
                y2.setValDouble(mc.player.getPosY());
                z2.setValDouble(mc.player.getPosZ());
                setsecond.setValBoolean(false);
                setState(false);
                return;
            }

            if (setone.getValBoolean() || setsecond.getValBoolean()) return;

            blocksToDestroy.removeIf(blockPos -> mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR);

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

            int destroyedBlocks = initialSize - blocksToDestroy.size();
            double percentage = Math.round((double) destroyedBlocks / initialSize * 100);

            UIUtils.drawTextOnScreenWithShadow(
                    blocksToDestroy.size() + "/" + initialSize + " [" + percentage + "%]",
                    screenWidth / 3,
                    screenHeight / 3,
                    Color.WHITE.getRGB()
            );
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

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (blocksToDestroy.size() > 0) {
            for (int i = 0; i < blocksToDestroy.size(); i++) {
                BlockPos blockPos = blocksToDestroy.get(i);

                float green = (float) Math.min(1.0, Math.max(0.0, i / (float) blocksToDestroy.size()));
                float red = 1.0f - green;
                float blue = 0.0f;

                RenderUtils.drawOutlinedBox(blockPos, red, green, blue, event);
            }
        }
    }

    private double[] getYP(BlockPos pos) {
        double dx = pos.getX() + 0.25D - mc.player.getPosX();
        double dz = pos.getZ() + 0.25D - mc.player.getPosZ();
        double dy = pos.getY() + 0.25D - (mc.player.getPosY() + mc.player.getEyeHeight());
        double distance = MathHelper.sqrt(dx * dx + dz * dz);

        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0D;
        double pitch = -Math.toDegrees(Math.atan2(dy, distance));

        return new double[]{yaw, pitch};
    }
}