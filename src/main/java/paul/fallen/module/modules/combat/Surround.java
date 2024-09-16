package paul.fallen.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.InventoryUtils;
import paul.fallen.utils.world.BlockUtils;

public class Surround extends Module {

	private int slot = 0;

	public Surround(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
	}

	@Override
	public void onEnable() {
		super.onEnable();

		// Save held slot
		slot = Minecraft.getInstance().player.inventory.currentItem;

		// Center the player
		centerPlayer();

		// Switch to new slot
		InventoryUtils.setSlot(getSlot());

		// Try all positions around the player
		BlockUtils.placeBlock(Minecraft.getInstance().player.getPosition().north(), Minecraft.getInstance().player.inventory.currentItem, true, true);
		BlockUtils.placeBlock(Minecraft.getInstance().player.getPosition().east(), Minecraft.getInstance().player.inventory.currentItem, true, true);
		BlockUtils.placeBlock(Minecraft.getInstance().player.getPosition().south(), Minecraft.getInstance().player.inventory.currentItem, true, true);
		BlockUtils.placeBlock(Minecraft.getInstance().player.getPosition().west(), Minecraft.getInstance().player.inventory.currentItem, true, true);

		// Switch back
		InventoryUtils.setSlot(slot);

		// Disable module
		setState(false);
		onDisable();
	}

	private void centerPlayer() {
		if (mc.player != null) {
			BlockPos pos = mc.player.getPosition();
			// Calculate the offset to move the player to the center of the block
			double offsetX = 0.5 - (mc.player.getPosX() - pos.getX());
			double offsetZ = 0.5 - (mc.player.getPosZ() - pos.getZ());

			// Apply the offset as motion
			mc.player.setMotion(mc.player.getMotion().add(offsetX * 0.1, 0, offsetZ * 0.1));
		}
	}

	private int getSlot() {
		for (int i = 0; i < 9; i++) {
			if (Minecraft.getInstance().player.inventory.getStackInSlot(i).getItem() instanceof BlockItem) {
				return i;
			}
		}

		return 0;
	}
}