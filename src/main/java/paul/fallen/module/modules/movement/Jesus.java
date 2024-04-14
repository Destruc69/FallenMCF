package paul.fallen.module.modules.movement;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

import java.util.HashMap;

public class Jesus extends Module {

    private Setting grim;

    public Jesus(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        grim = new Setting("GrimAC", this, false);
        addSetting(grim);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = event.player;
            BlockPos blockPos = new BlockPos(player.getPosX(), player.getPosY() - 0.000000000000000000000000000000000000000001, player.getPosZ());

            if (!grim.getValBoolean()) {
                if (player.world.getBlockState(blockPos).getBlock().equals(Blocks.WATER)) {
                    if (player.world.getBlockState(blockPos.up()).isAir()) {
                        player.setMotion(player.getMotion().x, 0.0, player.getMotion().z);
                        player.jumpMovementFactor = 0.05f;
                        player.setOnGround(true);
                        player.collidedHorizontally = true;
                        player.isAirBorne = false;
                    }
                }
            } else {
                if (mc.world.getBlockState(mc.player.getPosition().down()).getBlock().equals(Blocks.WATER)) {
                    mc.world.setBlockState(mc.player.getPosition().down(), Blocks.STONE.getDefaultState());
                }
                mc.gameSettings.keyBindSneak.setPressed(mc.player.isOnGround() && mc.world.getBlockState(mc.player.getPosition().down()).getBlock().getBlock().equals(Blocks.WATER));
            }
        } catch (Exception ignored) {
        }
    }
}