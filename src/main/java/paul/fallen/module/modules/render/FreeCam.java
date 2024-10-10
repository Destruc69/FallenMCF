package paul.fallen.module.modules.render;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.utils.entity.PlayerUtils;

import java.util.ArrayList;
import java.util.UUID;

public final class FreeCam extends Module {

    private final ArrayList<BlockPos> changedBlocks = new ArrayList<>();

    public FreeCam(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    private static PlayerEntity freecamEntity;

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || mc.player == null || mc.world == null)
            return;

        try {
            if (mc.player.movementInput.jump) {
                mc.player.setVelocity(mc.player.getMotion().x, 0.95, mc.player.getMotion().z);
            } else if (mc.player.movementInput.sneaking) {
                mc.player.setVelocity(mc.player.getMotion().x, -0.95, mc.player.getMotion().z);
            } else {
                mc.player.setVelocity(mc.player.getMotion().x, 0.0, mc.player.getMotion().z);
            }
            PlayerUtils.setMoveSpeed(0.65);
            mc.player.renderArmPitch = 5000f;

            for (int x = -1; x < 1; x++) {
                for (int y = -1; y < 1; y++) {
                    for (int z = -1; z < 1; z++) {
                        BlockPos blockPos = mc.player.getPosition().add(x, y, z);
                        if (!mc.world.getBlockState(blockPos).isAir()) {
                            mc.world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                            if (!changedBlocks.contains(blockPos)) {
                                changedBlocks.add(blockPos);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < changedBlocks.size() - 1; i++) {
                BlockPos blockPos = changedBlocks.get(i);


            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.world != null) {
            freecamEntity = new RemoteClientPlayerEntity(mc.world, new GameProfile(new UUID(69L, 96L), "Freecam"));
            freecamEntity.inventory.copyInventory(mc.player.inventory);
            freecamEntity.setPositionAndRotation(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch);
            freecamEntity.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntity(freecamEntity.getEntityId(), freecamEntity);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world != null && freecamEntity != null) {
            mc.player.setPositionAndRotation(freecamEntity.getPosX(), freecamEntity.getPosY(), freecamEntity.getPosZ(), freecamEntity.rotationYaw, freecamEntity.rotationPitch);
            mc.world.removeEntityFromWorld(freecamEntity.getEntityId());
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CPlayerPacket) {
            event.setCanceled(true);
        }
    }
}