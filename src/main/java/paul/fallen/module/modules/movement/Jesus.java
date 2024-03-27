package paul.fallen.module.modules.movement;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

public class Jesus extends Module {

    private boolean increasing = false;
    private double i = 0;

    public Jesus(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = event.player;
            BlockPos blockPos = new BlockPos(player.getPosX(), player.getPosY() - 0.000000001, player.getPosZ());

            if (player.world.getBlockState(blockPos).getBlock().equals(Blocks.WATER)) {
                if (player.world.getBlockState(blockPos.up()).isAir()) {
                    player.setMotion(player.getMotion().x, 0.0, player.getMotion().z);
                    player.jumpMovementFactor = 0.05f;
                    player.setOnGround(true);
                    player.collidedHorizontally = true;
                    player.isAirBorne = false;
                } else {
                    player.setMotion(player.getMotion().x, 0.1, player.getMotion().z);
                }
            }

            if (increasing) {
                if (i < 0.3) {
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
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        PlayerEntity player = mc.player;
        BlockPos blockPos = new BlockPos(player.getPosX(), player.getPosY() - 0.000000001, player.getPosZ());

        if (player.world.getBlockState(blockPos).getBlock().equals(Blocks.WATER)) {
            if (player.world.getBlockState(blockPos.up()).isAir()) {
                if (event.getPacket() instanceof CPlayerPacket) {
                    event.setPacket(new CPlayerPacket(true));
                }
                if (event.getPacket() instanceof CPlayerPacket.RotationPacket) {
                    CPlayerPacket.RotationPacket cPlayerPacket = (CPlayerPacket.RotationPacket) event.getPacket();
                    event.setPacket(new CPlayerPacket.RotationPacket(cPlayerPacket.getYaw(0), cPlayerPacket.getPitch(0), true));
                }
                if (event.getPacket() instanceof CPlayerPacket.PositionPacket) {
                    CPlayerPacket.PositionPacket cPlayerPacket = (CPlayerPacket.PositionPacket) event.getPacket();
                    event.setPacket(new CPlayerPacket.PositionPacket(cPlayerPacket.getX(0), cPlayerPacket.getY(0) - i, cPlayerPacket.getZ(0), true));
                }
                if (event.getPacket() instanceof CPlayerPacket.PositionRotationPacket) {
                    CPlayerPacket.PositionRotationPacket cPlayerPacket = (CPlayerPacket.PositionRotationPacket) event.getPacket();
                    event.setPacket(new CPlayerPacket.PositionRotationPacket(cPlayerPacket.getX(0), cPlayerPacket.getY(0) - i, cPlayerPacket.getZ(0), cPlayerPacket.getYaw(0), cPlayerPacket.getPitch(0), true));
                }
            }
        }
    }
}