package paul.fallen.module.modules.movement;

import net.minecraft.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class BetterSwim extends Module {

    public BetterSwim(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        try {
            if (mc.player.world.getBlockState(mc.player.getPosition()).getBlock().equals(Blocks.WATER)) {
                mc.player.startFallFlying();
            }
        } catch (Exception ignored) {
        }
    }

}
