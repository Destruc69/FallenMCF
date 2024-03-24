package paul.fallen.module.modules.movement;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class Jesus extends Module {

    private static final double WATER_WALK_SPEED = 0.1;

    public Jesus(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = event.player;
            BlockPos blockPos = new BlockPos(player.getPosX(), player.getPosY() - 0.0000001, player.getPosZ());

            if (player.world.getBlockState(blockPos).getBlock().equals(Blocks.WATER)) {
                // Check if the block above is air, indicating the player is above the water surface
                if (player.world.getBlockState(blockPos.up()).isAir()) {
                    // Adjust player's position and motion to mimic walking on water
                    player.setMotion(player.getMotion().x, 0.0, player.getMotion().z);
                    player.jumpMovementFactor = 0.05f; // Adjust jump speed
                    player.setOnGround(true); // Trick the game into thinking the player is on the ground
                    player.collidedVertically = true;
                    player.setSwimming(false);
                }
            }
        } catch (Exception ignored) {
        }
    }
}