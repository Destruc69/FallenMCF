package paul.fallen.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.world.BlockUtils;

public class Untrap extends Module {

	public Untrap(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			return;

		BlockPos above = new BlockPos(mc.player.getPosX(), mc.player.getPosY() + 2, mc.player.getPosZ());
		double oldPosY = mc.player.getPosY();
		if(!BlockUtils.isBlockEmpty(above)) {
			mc.playerController.onPlayerDestroyBlock(above);
			mc.player.setMotion(mc.player.getMotion().getX(), 0.42d, mc.player.getMotion().getZ());
			if(mc.player.getPosY() >= (oldPosY + 1)) {
				BlockUtils.placeBlock(new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), findInHotbar(), true, true);
				oldPosY = mc.player.getPosY();
			}
		}
	}
	
	private int findInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof BlockItem) {
                final Block block = ((BlockItem) stack.getItem()).getBlock();
                if (block == Blocks.ENDER_CHEST)
                    return i;
                else if (block == Blocks.OBSIDIAN)
                    return i;
            }
        }
        return -1;
	}
}
