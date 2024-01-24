package paul.fallen.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class AutoEat extends Module {
    private final Setting mode;

    private int oldSlot;
    private int bestSlot;

    public AutoEat(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        setState(true);

        mode = new Setting("mode", "Mode", this, "packet", new ArrayList<>(Arrays.asList("packet", "legit")));
        addSetting(mode);

        this.oldSlot = -1;
        this.bestSlot = -1;
    }

    @Override
    public void onEnable() {
        this.oldSlot = -1;
        this.bestSlot = -1;
    }

    @Override
    public void onDisable() {
        stop();
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        try {
            if (this.oldSlot == -1) {
                if (!this.canEat()) {
                    return;
                }
                float bestSaturation = 0.0f;
                for (int i = 0; i < 9; ++i) {
                    final ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (this.isFood(stack)) {
                        Food food = stack.getItem().getFood();
                        assert food != null;
                        final float saturation = food.getSaturation();
                        if (saturation > bestSaturation) {
                            bestSaturation = saturation;
                            this.bestSlot = i;
                        }
                    }
                }
                if (this.bestSlot != -1) {
                    this.oldSlot = mc.player.inventory.currentItem;
                }
            } else {
                if (!this.canEat()) {
                    this.stop();
                    return;
                }
                if (!this.isFood(mc.player.inventory.getStackInSlot(this.bestSlot))) {
                    this.stop();
                    return;
                }

                if (mode.sval == "legit") {
                    mc.player.inventory.currentItem = this.bestSlot;
                    mc.gameSettings.keyBindUseItem.setPressed(true);
                } else {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(bestSlot));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND,
                            new BlockRayTraceResult(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), Direction.DOWN, mc.player.getPosition(), false)));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private boolean canEat() {
        if (!mc.player.canEat(false)) {
            return false;
        }
        if (Minecraft.getInstance().objectMouseOver != null) {
            final BlockPos pos = new BlockPos(Minecraft.getInstance().objectMouseOver.getHitVec().x, Minecraft.getInstance().objectMouseOver.getHitVec().y, Minecraft.getInstance().objectMouseOver.getHitVec().z);
            final Block block = mc.world.getBlockState(pos).getBlock();
            return !(block instanceof ContainerBlock) && !(block instanceof CraftingTableBlock);
        }
        return true;
    }

    private boolean isFood(final ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().isFood();
    }

    private void stop() {
        try {
            if (mode.sval == "legit") {
                mc.gameSettings.keyBindUseItem.setPressed(false);
                mc.player.inventory.currentItem = this.oldSlot;
            } else {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(oldSlot));
            }
            this.oldSlot = -1;
        } catch (Exception ignored) {
        }
    }
}
