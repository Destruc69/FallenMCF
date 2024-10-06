package paul.fallen.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.world.BlockUtils;

public class Surround extends Module {

	public Surround(int bind, String name, String displayName, Category category) {
		super(bind, name, displayName, category);
	}

	@Override
	public void onEnable() {
		super.onEnable();

		try {
			centerPlayer();
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END || mc.player == null)
			return;

		try {
			if (!isPlayerCentered()) {
				centerPlayer();
			} else {
				BlockPos a = mc.player.getPosition().add(1, 0, 0);
				BlockPos b = mc.player.getPosition().add(-1, 0, 0);
				BlockPos c = mc.player.getPosition().add(0, 0, 1);
				BlockPos d = mc.player.getPosition().add(0, 0, -1);

				if (canPlace(a)) {
					BlockUtils.placeBlock(a.down(), getSlot(), true, true);
				}
				if (canPlace(b)) {
					BlockUtils.placeBlock(b.down(), getSlot(), true, true);
				}
				if (canPlace(c)) {
					BlockUtils.placeBlock(c.down(), getSlot(), true, true);
				}
				if (canPlace(d)) {
					BlockUtils.placeBlock(d.down(), getSlot(), true, true);
				}
			}
		} catch (Exception ignored) {
		}
	}

	private boolean canPlace(BlockPos blockPos) {
		return mc.world.getBlockState(blockPos).isAir() && !mc.world.getBlockState(blockPos.down()).isAir();
	}

	private void centerPlayer() {
		BlockPos pos = mc.player.getPosition();
		double offsetX = 0.5 - (mc.player.getPosX() - pos.getX());
		double offsetZ = 0.5 - (mc.player.getPosZ() - pos.getZ());

		// Adjust motion towards the center if needed
		mc.player.setMotion(mc.player.getMotion().add(offsetX * 0.1, 0, offsetZ * 0.1));
	}

	private boolean isPlayerCentered() {
		BlockPos pos = mc.player.getPosition();
		double distanceX = Math.abs(mc.player.getPosX() - (pos.getX() + 0.5));
		double distanceZ = Math.abs(mc.player.getPosZ() - (pos.getZ() + 0.5));

		// Return true if the player is within a small threshold of the center
		return distanceX < 0.01 && distanceZ < 0.01;
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