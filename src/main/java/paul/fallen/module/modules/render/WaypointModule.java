package paul.fallen.module.modules.render;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.FALLENClient;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.waypoint.Waypoint;

public final class WaypointModule extends Module {

    public WaypointModule(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        try {
            if (FALLENClient.INSTANCE.getWaypointManager().getWaypoints().size() > 0) {
                for (Waypoint waypoint : FALLENClient.INSTANCE.getWaypointManager().getWaypoints()) {
                    RenderUtils.drawLine(mc.player.getPosition(), getClosestSolidBlock(new BlockPos(waypoint.getX(), mc.player.lastTickPosY, waypoint.getZ())), 0, 1, 0, event);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private BlockPos getClosestSolidBlock(BlockPos targetPos) {
        int renderDistanceChunks = mc.gameSettings.renderDistanceChunks;

        assert targetPos != null;

        double closestDistance = Double.MAX_VALUE;
        BlockPos closestBlock = null;
        for (int x = mc.player.chunkCoordX - renderDistanceChunks; x <= mc.player.chunkCoordX + renderDistanceChunks; x++) {
            for (int z = mc.player.chunkCoordZ - renderDistanceChunks; z <= mc.player.chunkCoordZ + renderDistanceChunks; z++) {
                for (int y = 0; y <= 256; y++) {
                    BlockPos blockPos = new BlockPos(x * 16, y, z * 16);
                    double distance = blockPos.distanceSq(targetPos);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestBlock = blockPos;
                    }
                }
            }
        }
        assert closestBlock != null;
        return closestBlock;
    }
}