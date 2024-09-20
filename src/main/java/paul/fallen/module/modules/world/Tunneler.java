package paul.fallen.module.modules.world;

import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class Tunneler extends Module {

    private final Setting method;

    private ArrayList<BlockPos> tunnel;

    public Tunneler(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        method = new Setting("Method", this, "legit", new ArrayList<>(Arrays.asList("legit", "packet")));
        addSetting(method);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            if (tunnel != null) {
                tunnel.clear();
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        try {
            if (tunnel == null || tunnel.isEmpty()) {
                tunnel = getTunnel();
            } else {
                // Clear positions in tunnel that are broken or air
                tunnel.removeIf(blockPos -> mc.world.getBlockState(blockPos).isAir());

                // Get target position
                BlockPos t = tunnel.get(0);

                // Break block
                if (method.getValString().equalsIgnoreCase("packet")) {
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, t, Direction.UP));
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, t, Direction.UP));
                } else {
                    BlockUtils.breakBlock(t, mc.player.inventory.currentItem, true, true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (tunnel != null) {
                for (BlockPos blockPos : tunnel) {
                    RenderUtils.drawOutlinedBox(blockPos, 0, 1, 0, event);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private ArrayList<BlockPos> getTunnel() {
        Direction facing = mc.player.getHorizontalFacing();
        BlockPos playerPos = mc.player.getPosition();

        int xOffset = facing.getXOffset();
        int zOffset = facing.getZOffset();

        ArrayList<BlockPos> tunnelBlocks = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            for (int y = 0; y < 2; y++) {
                BlockPos blockPos = playerPos.add(xOffset * i, y, zOffset * i);
                if (!mc.world.getBlockState(blockPos).isAir()) {
                    tunnelBlocks.add(blockPos);
                }
            }
        }

        return tunnelBlocks;
    }
}
