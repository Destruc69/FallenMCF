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

import java.util.ArrayList;
import java.util.Arrays;

public final class Scaffold extends Module {

    private final Setting mode;
    private final Setting swing;
    private final Setting tower;

    private float initialYaw = 0;
    private boolean isYawChanged = false;
    private float currentYaw = 0;
    private float currentPitch = 0;

    public Scaffold(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        mode = new Setting("Mode", this, "blatant", new ArrayList<>(Arrays.asList("blatant", "legit")));
        swing = new Setting("Swing", this, true);
        tower = new Setting("Tower", this, true);
        addSetting(mode);
        addSetting(swing);
        addSetting(tower);
    }

    private static boolean isValidBlock(BlockPos blockPos) {
        return mc.world != null && !mc.world.isAirBlock(blockPos);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        initialYaw = mc.player.rotationYaw;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetControls();
    }

    private void resetControls() {
        mc.gameSettings.keyBindSneak.setPressed(false);
        mc.gameSettings.keyBindUseItem.setPressed(false);
        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindBack.setPressed(false);
        mc.gameSettings.keyBindSprint.setPressed(false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if ("blatant".equals(mode.getValString())) {
                handleBlatantMode();
            } else if ("legit".equals(mode.getValString())) {
                handleLegitMode();
            }
        }
    }

    private void handleBlatantMode() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.world == null) return;

        BlockPos playerBlock = new BlockPos(mc.player.getPosX(), mc.player.getBoundingBox().minY, mc.player.getPosZ());
        if (mc.world.isAirBlock(playerBlock.down()) && isValidBlock(playerBlock.down(2))) {
            place(playerBlock.down(), Direction.UP);
        } else {
            placeAdjacentBlocks(playerBlock);
        }

        if (tower.getValBoolean() && mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnGround()
                && mc.player.getPosY() - Math.floor(mc.player.getPosY()) <= 0.1) {
            EntityUtils.setMotionY(0.42);
        }

        mc.gameSettings.keyBindSneak.setPressed(mc.player.isOnGround() && mc.world.isAirBlock(mc.player.getPosition().down()));
    }

    private void handleLegitMode() {
        if (mc.player.isOnGround() && mc.world.getBlockState(mc.player.getPosition().down()).getBlock().equals(Blocks.AIR)) {
            mc.gameSettings.keyBindSneak.setPressed(true);
            mc.gameSettings.keyBindUseItem.setPressed(true);
        } else {
            mc.gameSettings.keyBindSneak.setPressed(false);
            mc.gameSettings.keyBindUseItem.setPressed(false);
        }
        mc.player.rotationPitch = 80;
        mc.gameSettings.keyBindBack.setPressed(true);
    }

    private void placeAdjacentBlocks(BlockPos playerBlock) {
        Direction[] directions = {Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH};
        for (Direction direction : directions) {
            if (isValidBlock(playerBlock.offset(direction))) {
                place(playerBlock.down(), direction);
                return;
            }
        }

        Direction[] diagonalDirections = {
                Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH
        };
        for (Direction dir1 : diagonalDirections) {
            for (Direction dir2 : diagonalDirections) {
                if (isValidBlock(playerBlock.offset(dir1).offset(dir2))) {
                    place(playerBlock.down().offset(dir1), dir2);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onInput(InputEvent.KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_RIGHT && !isYawChanged) {
            initialYaw += 90;
            isYawChanged = true;
        } else if (event.getKey() == GLFW.GLFW_KEY_LEFT && !isYawChanged) {
            initialYaw -= 90;
            isYawChanged = true;
        } else {
            isYawChanged = false;
        }
    }

    private void place(BlockPos pos, Direction face) {
        pos = adjustPositionForFace(pos, face);

        if (!(mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof BlockItem)) {
            selectBlockItem();
        }

        if (mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof BlockItem) {
            performBlockPlacement(pos, face);
        }
        mc.player.renderYawOffset = mc.player.rotationYaw + 180;
    }

    private BlockPos adjustPositionForFace(BlockPos pos, Direction face) {
        switch (face) {
            case UP:
                return pos.down();
            case NORTH:
                return pos.add(0, 0, 1);
            case EAST:
                return pos.add(-1, 0, 0);
            case SOUTH:
                return pos.add(0, 0, -1);
            case WEST:
                return pos.add(1, 0, 0);
            default:
                return pos;
        }
    }

    private void selectBlockItem() {
        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.player.inventory.getStackInSlot(i);
            if (item.getItem() instanceof BlockItem) {
                int last = mc.player.inventory.currentItem;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                mc.player.inventory.currentItem = i;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                mc.player.inventory.currentItem = last;
                return;
            }
        }
    }

    private void performBlockPlacement(BlockPos pos, Direction face) {
        mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND,
                new BlockRayTraceResult(new Vector3d(0.5, 0.5, 0.5), face, pos, false));
        if (swing.getValBoolean()) {
            mc.player.swingArm(Hand.MAIN_HAND);
        } else {
            mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
        }
        sendRotationPacket(pos);
    }

    private void sendRotationPacket(BlockPos pos) {
        double var4 = pos.getX() + 0.25 - mc.player.getPosX();
        double var6 = pos.getZ() + 0.25 - mc.player.getPosZ();
        double var8 = pos.getY() + 0.25 - (mc.player.getPosY() + mc.player.getEyeHeight());
        double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
        float yaw = (float) (Math.atan2(var6, var4) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(var8, var14) * 180.0 / Math.PI);
        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(yaw, pitch, mc.player.isOnGround()));
    }

    private float roundYaw() {
        return (float) (Math.floor((mc.player.rotationYaw + 45) / 90) * 90);
    }

    private void interpolateRotation(float targetYaw, float targetPitch) {
        float diffYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float diffPitch = targetPitch - currentPitch;
        float stepYaw = Math.signum(diffYaw) * Math.min(5.0f, Math.abs(diffYaw));
        float stepPitch = Math.signum(diffPitch) * Math.min(5.0f, Math.abs(diffPitch));

        currentYaw = MathHelper.wrapDegrees(currentYaw + stepYaw);
        currentPitch += stepPitch;
    }
}