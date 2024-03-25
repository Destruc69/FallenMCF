package paul.fallen.module.modules.movement;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.setting.Setting;

public class Jesus extends Module {

    private final Setting bypass;

    private boolean increasing = false;
    private double i = 0;

    public Jesus(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        bypass = new Setting("Bypass", this, false);
        addSetting(bypass);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = event.player;
            BlockPos blockPos = new BlockPos(player.getPosX(), player.getPosY() - 0.000000001, player.getPosZ());

            if (player.world.getBlockState(blockPos).getBlock().equals(Blocks.WATER)) {
                // Check if the block above is air, indicating the player is above the water surface
                if (player.world.getBlockState(blockPos.up()).isAir()) {
                    // Adjust player's position and motion to mimic walking on water
                    player.setMotion(player.getMotion().x, 0.0, player.getMotion().z);
                    player.jumpMovementFactor = 0.05f; // Adjust jump speed
                    player.setOnGround(true); // Trick the game into thinking the player is on the ground
                    player.collidedHorizontally = true;
                    player.isAirBorne = false;
                } else {
                    mc.player.setMotion(player.getMotion().x, 0.001, player.getMotion().z);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        BlockPos blockPos = new BlockPos(player.getPosX(), player.getPosY() - 0.0000001, player.getPosZ());

        if (player.world.getBlockState(blockPos).getBlock().equals(Blocks.WATER)) {
            // Check if the block above is air, indicating the player is above the water surface
            if (player.world.getBlockState(blockPos.up()).isAir()) {
                if (bypass.bval) {
                    if (event.getPacket() instanceof CPlayerPacket.PositionRotationPacket) {
                        CPlayerPacket.PositionRotationPacket cPlayerPacket = (CPlayerPacket.PositionRotationPacket) event.getPacket();
                        CPlayerPacket.PositionRotationPacket newCPlayerPacket = new CPlayerPacket.PositionRotationPacket(cPlayerPacket.getX(0), cPlayerPacket.getY(0) - i, cPlayerPacket.getZ(0), cPlayerPacket.getYaw(0), cPlayerPacket.getPitch(0), true);
                        event.setPacket(newCPlayerPacket);
                    }
                    if (event.getPacket() instanceof CPlayerPacket.PositionPacket) {
                        CPlayerPacket.PositionPacket cPlayerPacket = (CPlayerPacket.PositionPacket) event.getPacket();
                        CPlayerPacket.PositionPacket newCPlayerPacket = new CPlayerPacket.PositionPacket(cPlayerPacket.getX(0), cPlayerPacket.getY(0) - i, cPlayerPacket.getZ(0), true);
                        event.setPacket(newCPlayerPacket);
                    }
                    if (event.getPacket() instanceof CPlayerPacket) {
                        event.setPacket(new CPlayerPacket(true));
                    }
                    if (event.getPacket() instanceof CPlayerPacket.RotationPacket) {
                        CPlayerPacket.RotationPacket cPlayerPacket = (CPlayerPacket.RotationPacket) event.getPacket();
                        CPlayerPacket.RotationPacket newCPlayerPacket = new CPlayerPacket.RotationPacket(cPlayerPacket.getYaw(0), cPlayerPacket.getPitch(0), true);
                        event.setPacket(newCPlayerPacket);
                    }
                    if (increasing) {
                        if (i < 0.5) {
                            i += 0.01f;
                        } else {
                            increasing = false;
                        }
                    } else {
                        if (i > 0.1) {
                            i -= 0.01f;
                        } else {
                            increasing = true;
                        }
                    }
                }
            }
        }
    }
}