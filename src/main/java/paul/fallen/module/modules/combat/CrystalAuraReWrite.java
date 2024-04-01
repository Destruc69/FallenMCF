package paul.fallen.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.PlayerUtils;

public class CrystalAuraReWrite extends Module {

    private Setting breakTicks;
    private Setting placeTicks;

    private Setting maxDistance;
    private Setting minDamage;
    private Setting maxDamageSelf;

    public CrystalAuraReWrite(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        breakTicks = new Setting("BreakTicks", this, 10, 1, 20);
        placeTicks = new Setting("PlaceTicks", this, 10, 1, 20);

        maxDistance = new Setting("MaxDistance", this, 5, 3, 6);
        minDamage = new Setting("MinDamage", this, 2, 10, 0);
        maxDamageSelf = new Setting("MaxDamageSelf", this, 6, 0, 10);

        addSetting(breakTicks);
        addSetting(placeTicks);

        addSetting(maxDistance);
        addSetting(minDamage);
        addSetting(maxDamageSelf);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) throws Exception {
        try {
            if (event.phase != TickEvent.Phase.START) return;

            CrystalTarget target = findTarget();
            if (target == null) return;

            lookAtTarget(target);

            if (mc.player.ticksExisted % placeTicks.dval == 0) {
                placeCrystal(target.blockPos);
            }

            if (mc.player.ticksExisted % breakTicks.dval == 0) {
                breakCrystal(target.crystalEntity);
            }
        } catch (Exception ignored) {
        }
    }

    private CrystalTarget findTarget() {
        double bestDamage = 0;
        BlockPos bestBlock = null;
        EnderCrystalEntity bestCrystal = null;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                PlayerEntity player = (PlayerEntity) entity;
                for (BlockPos block : PlayerUtils.possiblePlacePositions((float) maxDistance.dval, true, true)) {
                    double targetDamage = PlayerUtils.calculateCrystalDamage(block.getX() + 0.5, block.getY() + 1, block.getZ() + 0.5, player);
                    double selfDamage = PlayerUtils.calculateCrystalDamage(block.getX() + 0.5, block.getY() + 1, block.getZ() + 0.5, mc.player);
                    if (targetDamage > bestDamage && targetDamage >= minDamage.dval && selfDamage <= maxDamageSelf.dval) {
                        bestDamage = targetDamage;
                        bestBlock = block;
                    }
                }

                for (Entity crystalEntity : mc.world.getAllEntities()) {
                    if (crystalEntity instanceof EnderCrystalEntity && mc.player.getDistance(crystalEntity) <= maxDistance.dval) {
                        double damage = PlayerUtils.calculateCrystalDamage((EnderCrystalEntity) crystalEntity, player);
                        if (damage > bestDamage && damage >= minDamage.dval) {
                            bestDamage = damage;
                            bestCrystal = (EnderCrystalEntity) crystalEntity;
                        }
                    }
                }
            }
        }

        return new CrystalTarget(bestBlock, bestCrystal);
    }

    private void lookAtTarget(CrystalTarget target) {
        float[] rotations;
        if (target.blockPos != null) {
            rotations = getRotations(target.blockPos.getX(), target.blockPos.getY(), target.blockPos.getZ());
        } else {
            rotations = getRotations(target.crystalEntity);
        }
        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rotations[0], rotations[1], mc.player.isOnGround()));
    }

    private void placeCrystal(BlockPos targetBlock) {
        boolean offhandCheck = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        if (!offhandCheck && mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
            return;
        }

        mc.playerController.func_217292_a(mc.player, mc.world, offhandCheck ? Hand.OFF_HAND : Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()), Direction.UP, targetBlock, false));
        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(offhandCheck ? Hand.OFF_HAND : Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()), Direction.UP, targetBlock, false)));
        mc.player.swingArm(offhandCheck ? Hand.OFF_HAND : Hand.MAIN_HAND);
    }

    private void breakCrystal(EnderCrystalEntity crystal) {
        if (mc.player.getCooledAttackStrength(0.0f) >= 1.0f) {
            mc.getConnection().sendPacket(new CUseEntityPacket(crystal, true));
            mc.playerController.attackEntity(mc.player, crystal);
            mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    public static float[] getRotations(EnderCrystalEntity e) {
        double x = e.getPosition().getX() - mc.player.getPosition().getX(), y = e.getPosition().getY() + e.getEyeHeight() / 2 - mc.player.getPosition().getY() - 1.2, z = e.getPosition().getZ() - mc.player.getPosition().getZ();

        return new float[]{MathHelper.wrapDegrees((float) (Math.atan2(z, x) * 180 / Math.PI) - 90), (float) -(Math.atan2(y, MathHelper.sqrt(x * x + z * z)) * 180 / Math.PI)};
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        double x = posX - mc.player.getPosition().getX(), y = posY - mc.player.getPosition().getY() - 1.2, z = posZ - mc.player.getPosition().getZ();

        return new float[]{MathHelper.wrapDegrees((float) (Math.atan2(z, x) * 180 / Math.PI) - 90), (float) -(Math.atan2(y, MathHelper.sqrt(x * x + z * z)) * 180 / Math.PI)};
    }

    private static class CrystalTarget {
        public final BlockPos blockPos;
        public final EnderCrystalEntity crystalEntity;

        public CrystalTarget(BlockPos blockPos, EnderCrystalEntity crystalEntity) {
            this.blockPos = blockPos;
            this.crystalEntity = crystalEntity;
        }
    }
}