package paul.fallen.module.modules.world;

import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;
import paul.fallen.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Tunneler extends Module {

    private final Setting method;
    private final Setting auto;

    private List<BlockPos> tunnel;

    public Tunneler(int bind, String name, String displayName, Module.Category category) {
        super(bind, name, displayName, category);

        method = new Setting("Method", this, "legit", new ArrayList<>(Arrays.asList("legit", "packet")));
        auto = new Setting("Auto", this, false);
        addSetting(method);
        addSetting(auto);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (tunnel != null) {
            tunnel.clear();
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
                // Filter out air blocks from tunnel
                tunnel = tunnel.stream()
                        .filter(blockPos -> !mc.world.getBlockState(blockPos).isAir())
                        .collect(Collectors.toList());

                // Get target position
                BlockPos t = tunnel.get(0);

                // Break block
                if (method.getValString().equalsIgnoreCase("packet")) {
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, t, Direction.UP));
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, t, Direction.UP));
                } else {
                    BlockUtils.breakBlock(t, mc.player.inventory.currentItem, true, true);
                }

                if (auto.getValBoolean()) {
                    tunnel.stream()
                            .min(Comparator.comparingDouble(blockPos -> mc.player.getDistanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ())))
                            .ifPresent(closestBlock -> {
                                mc.gameSettings.keyBindForward.setPressed(mc.player.getDistanceSq(closestBlock.getX(), closestBlock.getY(), closestBlock.getZ()) > 4);

                                float[] r = RotationUtils.getYawAndPitch(new Vector3d(closestBlock.getX() + 0.5, closestBlock.getY() + 0.5, closestBlock.getZ() + 0.5));
                                mc.player.rotationYaw = r[0];
                            });
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (tunnel != null) {
                tunnel.forEach(blockPos -> RenderUtils.drawOutlinedBox(blockPos, 0, 1, 0, event));
            }
        } catch (Exception ignored) {
        }
    }

    private List<BlockPos> getTunnel() {
        Direction facing = mc.player.getHorizontalFacing();
        BlockPos playerPos = mc.player.getPosition();

        int xOffset = facing.getXOffset();
        int zOffset = facing.getZOffset();

        return Arrays.stream(new int[]{1, 2, 3, 4})
                .boxed()
                .flatMap(i -> Arrays.stream(new int[]{0, 1})
                        .mapToObj(y -> playerPos.add(xOffset * i, y, zOffset * i))
                        .filter(blockPos -> !mc.world.getBlockState(blockPos).isAir()))
                .collect(Collectors.toList());
    }
}