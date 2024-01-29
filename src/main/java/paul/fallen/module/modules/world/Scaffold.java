package paul.fallen.module.modules.world;

import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;
import paul.fallen.utils.entity.EntityUtils;
import paul.fallen.utils.entity.PlayerControllerUtils;

import java.util.ArrayList;
import java.util.Arrays;

public final class Scaffold extends Module {

    Setting rotationMode;
    Setting swing;
    Setting tower;

    public Scaffold(int bind, String name, String displayName, Module.Category category) {
        super(bind, name, displayName, category);

        rotationMode = new Setting("RoationMode", this, "a", new ArrayList<>(Arrays.asList("a", "b")));
        swing = new Setting("Swing", this, true);
        tower = new Setting("Tower", this, true);
        addSetting(swing);
        addSetting(tower);
    }

    private static boolean isValidBlock(BlockPos blockPos) {
        assert mc.world != null;
        return !(mc.world.isAirBlock(blockPos));
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            BlockPos playerBlock = new BlockPos(mc.player.getPosX(), mc.player.getBoundingBox().minY, mc.player.getPosZ());
            assert mc.world != null;
            if (mc.world.isAirBlock(playerBlock.add(0, -1, 0))) {
                if (isValidBlock(playerBlock.add(0, -2, 0))) {
                    place(playerBlock.add(0, -1, 0), Direction.UP);
                } else if (isValidBlock(playerBlock.add(-1, -1, 0))) {
                    place(playerBlock.add(0, -1, 0), Direction.EAST);
                } else if (isValidBlock(playerBlock.add(1, -1, 0))) {
                    place(playerBlock.add(0, -1, 0), Direction.WEST);
                } else if (isValidBlock(playerBlock.add(0, -1, -1))) {
                    place(playerBlock.add(0, -1, 0), Direction.SOUTH);
                } else if (isValidBlock(playerBlock.add(0, -1, 1))) {
                    place(playerBlock.add(0, -1, 0), Direction.NORTH);
                } else if (isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (isValidBlock(playerBlock.add(0, -1, 1))) {
                        place(playerBlock.add(0, -1, 1), Direction.NORTH);
                    }
                    place(playerBlock.add(1, -1, 1), Direction.EAST);
                } else if (isValidBlock(playerBlock.add(-1, -1, 1))) {
                    if (isValidBlock(playerBlock.add(-1, -1, 0))) {
                        place(playerBlock.add(0, -1, 1), Direction.WEST);
                    }
                    place(playerBlock.add(-1, -1, 1), Direction.SOUTH);
                } else if (isValidBlock(playerBlock.add(-1, -1, -1))) {
                    if (isValidBlock(playerBlock.add(0, -1, -1))) {
                        place(playerBlock.add(0, -1, -1), Direction.SOUTH);
                    }
                    place(playerBlock.add(-1, -1, -1), Direction.WEST);
                } else if (isValidBlock(playerBlock.add(1, -1, -1))) {
                    if (isValidBlock(playerBlock.add(1, -1, 0))) {
                        place(playerBlock.add(1, -1, 0), Direction.EAST);
                    }
                    place(playerBlock.add(1, -1, -1), Direction.NORTH);
                }
            }

            if (tower.bval) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    EntityUtils.setMotionX(0);
                    EntityUtils.setMotionZ(0);
                    if (!mc.player.isOnGround() && mc.player.getPosY() - Math.floor(mc.player.getPosY()) <= 0.1) {
                        EntityUtils.setMotionY(0.41999998688697815);
                    }
                }
            }

            if (mc.player.isOnGround() && mc.world.isAirBlock(mc.player.getPosition().down())) {
                mc.player.setMotion(0, mc.player.getMotion().y, 0);
            }
        } catch (Exception ignored) {
        }
    }

    private void place(BlockPos pos, Direction face) {
        if (face == Direction.UP) {
            pos = pos.add(0, -1, 0);
        } else if (face == Direction.NORTH) {
            pos = pos.add(0, 0, 1);
        } else if (face == Direction.EAST) {
            pos = pos.add(-1, 0, 0);
        } else if (face == Direction.SOUTH) {
            pos = pos.add(0, 0, -1);
        } else if (face == Direction.WEST) {
            pos = pos.add(1, 0, 0);
        }

        assert mc.player != null;
        if (!(mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof BlockItem)) {
            for (int i = 0; i < 9; i++) {
                ItemStack item = mc.player.inventory.getStackInSlot(i);
                if (item.getItem() instanceof BlockItem) {
                    int last = mc.player.inventory.currentItem;
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                    mc.player.inventory.currentItem = i;
                    //mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
                    PlayerControllerUtils.rightClickBlock(new Vector3d(0.5, 0.5, 0.5), face, pos);
                    if (swing.bval) {
                        mc.player.swingArm(Hand.MAIN_HAND);
                    } else {
                        mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
                    }
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    mc.player.inventory.currentItem = last;
                }
            }

            if (rotationMode.sval == "a") {
                double var4 = pos.getX() + 0.25D - mc.player.getPosX();
                double var6 = pos.getZ() + 0.25D - mc.player.getPosZ();
                double var8 = pos.getY() + 0.25D - (mc.player.getPosY() + mc.player.getEyeHeight());
                double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
                double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
                double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket((float) yaw, (float) pitch, mc.player.isOnGround()));
            } else if (rotationMode.sval == "b") {
                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(getRotationsBlock(pos, face)[0], getRotationsBlock(pos, face)[1], mc.player.isOnGround()));
            }
        }

        if (mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof BlockItem) {
            //mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
            PlayerControllerUtils.rightClickBlock(new Vector3d(0.5, 0.5, 0.5), face, pos);
            if (swing.bval) {
                mc.player.swingArm(Hand.MAIN_HAND);
            } else {
                mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
            }

            if (rotationMode.sval == "a") {
                double var4 = pos.getX() + 0.25D - mc.player.getPosX();
                double var6 = pos.getZ() + 0.25D - mc.player.getPosZ();
                double var8 = pos.getY() + 0.25D - (mc.player.getPosY() + mc.player.getEyeHeight());
                double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
                double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
                double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket((float) yaw, (float) pitch, mc.player.isOnGround()));
            } else if (rotationMode.sval == "b") {
                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(getRotationsBlock(pos, face)[0], getRotationsBlock(pos, face)[1], mc.player.isOnGround()));
            }
        }
    }

    public float[] getRotationsBlock(BlockPos block, Direction face) {
        assert mc.player != null;
        double x = (double) block.getX() + 0.5 - mc.player.getPosX() + (double) face.getXOffset() / 2.0;
        double z = (double) block.getZ() + 0.5 - mc.player.getPosZ() + (double) face.getZOffset() / 2.0;
        double y = (double) block.getY() + 0.5;
        double d1 = mc.player.getPosY() + (double) mc.player.getEyeHeight() - y;
        double d3 = MathHelper.sqrt(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }
}