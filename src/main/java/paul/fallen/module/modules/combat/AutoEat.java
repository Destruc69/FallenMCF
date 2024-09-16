package paul.fallen.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.client.Minecraft;
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
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

import java.util.ArrayList;
import java.util.Arrays;

public class AutoEat extends Module {
    private final Setting mode;
    private int oldSlot = -1;
    private int bestSlot = -1;

    public AutoEat(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
        mode = new Setting("Mode", this, "packet", new ArrayList<>(Arrays.asList("packet", "legit")));
        addSetting(mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        oldSlot = -1;
        bestSlot = -1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stop();
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        try {
            if (oldSlot == -1) {
                if (!canEat()) return;

                float bestSaturation = 0.0f;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (isFood(stack)) {
                        float saturation = stack.getItem().getFood().getSaturation();
                        if (saturation > bestSaturation) {
                            bestSaturation = saturation;
                            bestSlot = i;
                        }
                    }
                }

                if (bestSlot != -1) {
                    oldSlot = mc.player.inventory.currentItem;
                }
            } else {
                if (!canEat() || !isFood(mc.player.inventory.getStackInSlot(bestSlot))) {
                    stop();
                    return;
                }

                if ("legit".equals(mode.getValString())) {
                    mc.player.inventory.currentItem = bestSlot;
                    mc.gameSettings.keyBindUseItem.setPressed(true);
                } else {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(bestSlot));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(
                            Hand.MAIN_HAND,
                            new BlockRayTraceResult(new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), Direction.DOWN, mc.player.getPosition(), false)));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private boolean canEat() {
        if (!mc.player.canEat(false)) return false;
        if (Minecraft.getInstance().objectMouseOver != null) {
            BlockPos pos = new BlockPos(Minecraft.getInstance().objectMouseOver.getHitVec());
            Block block = mc.world.getBlockState(pos).getBlock();
            return !(block instanceof ContainerBlock || block instanceof CraftingTableBlock);
        }
        return true;
    }

    private boolean isFood(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().isFood();
    }

    private void stop() {
        try {
            if ("legit".equals(mode.getValString())) {
                mc.gameSettings.keyBindUseItem.setPressed(false);
                mc.player.inventory.currentItem = oldSlot;
            } else {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(oldSlot));
            }
            oldSlot = -1;
        } catch (Exception ignored) {
        }
    }
}