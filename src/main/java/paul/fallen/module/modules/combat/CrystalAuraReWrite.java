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
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.PlayerUtils;

import java.util.List;

public class CrystalAuraReWrite extends Module {

    private final Setting breakTicks;
    private final Setting placeTicks;

    private final Setting maxDistance;
    private final Setting minDamage;
    private final Setting maxDamageSelf;

    public CrystalAuraReWrite(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        breakTicks = new Setting("BreakTicks", this, 10, 1, 20, true);
        placeTicks = new Setting("PlaceTicks", this, 10, 1, 20, true);

        maxDistance = new Setting("MaxDistance", this, 5, 3, 6, true);
        minDamage = new Setting("MinDamage", this, 2, 10, 0, true);
        maxDamageSelf = new Setting("MaxDamageSelf", this, 6, 0, 10, true);

        addSetting(breakTicks);
        addSetting(placeTicks);

        addSetting(maxDistance);
        addSetting(minDamage);
        addSetting(maxDamageSelf);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase == TickEvent.Phase.START) {
                if (mc.player.ticksExisted % placeTicks.getValDouble() == 0) {
                    placeCrystal();
                }
                if (mc.player.ticksExisted % breakTicks.getValDouble() == 0) {
                    breakCrystal();
                }
            }
        } catch (Exception ignored) {
        }
    }

    public EnderCrystalEntity getBestCrystal() {
        double bestDamage = 0;
        EnderCrystalEntity bestCrystal = null;
        for(Entity e: mc.world.getAllEntities()) {
            if(!(e instanceof EnderCrystalEntity)) continue;
            EnderCrystalEntity c = (EnderCrystalEntity) e;
            if(mc.player.getDistance(e) > maxDistance.getValDouble()) continue;
            if(!c.isAlive()) continue;
            for(Entity e2: mc.world.getAllEntities()) {
                if(!(e2 instanceof PlayerEntity) || e2 == mc.player) continue;
                PlayerEntity pe = (PlayerEntity) e2;
                if(mc.player.getDistance(pe) > maxDistance.getValDouble()) continue;
                if(!pe.isAlive() || pe.getHealth() <= 0) continue;
                double targetDamage = PlayerUtils.calculateCrystalDamage(c, pe);
                if(targetDamage < minDamage.getValDouble()) continue;
                double selfDamage = PlayerUtils.calculateCrystalDamage(c, mc.player);
                if(selfDamage > maxDamageSelf.getValDouble()) continue;
                if(targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    bestCrystal = c;
                }
            }
        }
        return bestCrystal;
    }

    public BlockPos getBestBlock() {
        double bestDamage = 0;
        BlockPos bestBlock = null;
        List<BlockPos> blocks = PlayerUtils.possiblePlacePositions((float) maxDistance.getValDouble(), true, true);
        for(Entity e: mc.world.getAllEntities()) {
            if(!(e instanceof PlayerEntity) || e == mc.player) continue;
            PlayerEntity pe = (PlayerEntity) e;
            for(BlockPos block: blocks) {
                if(pe.getDistance(mc.player) > maxDistance.getValDouble()) continue;
                double targetDamage = PlayerUtils.calculateCrystalDamage(block.getX() + 0.5, block.getY() + 1, block.getZ() + 0.5, pe);
                if(targetDamage < minDamage.getValDouble()) continue;
                double selfDamage = PlayerUtils.calculateCrystalDamage(block.getX() + 0.5, block.getY() + 1, block.getZ() + 0.5, mc.player);
                if(selfDamage > maxDamageSelf.getValDouble()) continue;
                if(targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    bestBlock = block;
                }
            }
        }
        return bestBlock;
    }

    public void placeCrystal() {
        BlockPos targetBlock = getBestBlock();
        if(targetBlock == null) return;
        boolean offhandCheck = false;
        if(mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
                return;
            }
        } else offhandCheck = true;
        float[] rotations = getRotations(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rotations[0], rotations[1], mc.player.isOnGround()));

        mc.playerController.func_217292_a(mc.player, mc.world, offhandCheck ? Hand.OFF_HAND : Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()), Direction.UP, targetBlock, false));
        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(offhandCheck ? Hand.OFF_HAND : Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()), Direction.UP, targetBlock, false)));
        mc.player.swingArm(offhandCheck ? Hand.OFF_HAND : Hand.MAIN_HAND);
    }

    public void breakCrystal() {
        EnderCrystalEntity c = getBestCrystal();
        if(c == null) return;
        float[] rotations = getRotations(c);
        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rotations[0], rotations[1], mc.player.isOnGround()));
        if(mc.player.getCooledAttackStrength(0.0f) >= 1.0f) {
            mc.getConnection().sendPacket(new CUseEntityPacket(c, true));
            mc.playerController.attackEntity(mc.player, c);
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
}