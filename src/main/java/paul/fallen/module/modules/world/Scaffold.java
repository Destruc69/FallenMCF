package paul.fallen.module.modules.world;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.EntityUtils;
import paul.fallen.utils.entity.PlayerControllerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public final class Scaffold extends Module {

    Setting mode;
    Setting swing;
    Setting tower;

    private float yaw = 0;
    private boolean a = false;

    public Scaffold(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", this, "blatant", new ArrayList<>(Arrays.asList("blatant", "legit")));
        swing = new Setting("Swing", this, true);
        tower = new Setting("Tower", this, true);
        addSetting(mode);
        addSetting(swing);
        addSetting(tower);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        roundYaw();
        yaw = mc.player.rotationYaw;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.gameSettings.keyBindSneak.setPressed(false);
        mc.gameSettings.keyBindUseItem.setPressed(false);
        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindBack.setPressed(false);
        mc.gameSettings.keyBindSneak.setPressed(false);
        mc.gameSettings.keyBindSprint.setPressed(false);
    }

    private static boolean isValidBlock(BlockPos blockPos) {
        assert mc.world != null;
        return !(mc.world.isAirBlock(blockPos));
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (mode.getValString().equals("blatant")) {
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

                if (tower.getValBoolean()) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        EntityUtils.setMotionX(0);
                        EntityUtils.setMotionZ(0);
                        if (!mc.player.isOnGround() && mc.player.getPosY() - Math.floor(mc.player.getPosY()) <= 0.1) {
                            EntityUtils.setMotionY(0.41999998688697815);
                        }
                    }
                }

                mc.gameSettings.keyBindSneak.setPressed(mc.player.isOnGround() && mc.world.isAirBlock(mc.player.getPosition().down()));
            } else if (mode.getValString().equals("legit")) {
                if (mc.player.isOnGround() && mc.world.getBlockState(mc.player.getPosition().down()).getBlock().getBlock().equals(Blocks.AIR)) {
                    mc.player.rotationYaw = yaw + 180;
                    mc.player.rotationPitch = 78;
                    mc.gameSettings.keyBindSneak.setPressed(true);
                    mc.gameSettings.keyBindUseItem.setPressed(true);
                    mc.gameSettings.keyBindForward.setPressed(false);
                    mc.gameSettings.keyBindBack.setPressed(true);
                    mc.gameSettings.keyBindSneak.setPressed(true);
                    mc.gameSettings.keyBindSprint.setPressed(false);
                } else {
                    mc.player.rotationYaw = yaw;
                    mc.player.rotationPitch = 0;
                    mc.gameSettings.keyBindSneak.setPressed(false);
                    mc.gameSettings.keyBindUseItem.setPressed(false);
                    mc.gameSettings.keyBindForward.setPressed(true);
                    mc.gameSettings.keyBindBack.setPressed(false);
                    mc.gameSettings.keyBindSneak.setPressed(false);
                    mc.gameSettings.keyBindSprint.setPressed(true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onInput(InputEvent.KeyInputEvent event) {
        try {
            if (event.getKey() == GLFW.GLFW_KEY_RIGHT) {
                if (!a) {
                    yaw = yaw + 90;
                    a = true;
                }
            } else if (event.getKey() == GLFW.GLFW_KEY_LEFT) {
                if (!a) {
                    yaw = yaw - 90;
                    a = true;
                }
            } else {
                a = false;
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
                    mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0.5, 0.5, 0.5), face, pos, false));
                    if (swing.getValBoolean()) {
                        mc.player.swingArm(Hand.MAIN_HAND);
                    } else {
                        mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
                    }
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    mc.player.inventory.currentItem = last;
                }
            }

            double var4 = pos.getX() + 0.25D - mc.player.getPosX();
            double var6 = pos.getZ() + 0.25D - mc.player.getPosZ();
            double var8 = pos.getY() + 0.25D - (mc.player.getPosY() + mc.player.getEyeHeight());
            double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
            double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
            double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket((float) yaw, (float) pitch, mc.player.isOnGround()));
        }

        if (mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof BlockItem) {
            //mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
            PlayerControllerUtils.rightClickBlock(new Vector3d(0.5, 0.5, 0.5), face, pos);
            mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0.5, 0.5, 0.5), face, pos, false));
            if (swing.getValBoolean()) {
                mc.player.swingArm(Hand.MAIN_HAND);
            } else {
                mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
            }

            double var4 = pos.getX() + 0.25D - mc.player.getPosX();
            double var6 = pos.getZ() + 0.25D - mc.player.getPosZ();
            double var8 = pos.getY() + 0.25D - (mc.player.getPosY() + mc.player.getEyeHeight());
            double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
            double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
            double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket((float) yaw, (float) pitch, mc.player.isOnGround()));
        }

        mc.player.renderYawOffset = mc.player.rotationYaw + 180;
    }

    private float roundYaw() {
        return (float) (Math.floor((mc.player.rotationYaw + 45) / 90) * 90);
    }
}