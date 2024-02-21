package paul.fallen.module.modules.world;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class OverKill extends Module {

    public OverKill(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onDisable() {
        if (Thread.currentThread().getPriority() != Thread.NORM_PRIORITY) {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (Thread.currentThread().getPriority() != Thread.MAX_PRIORITY) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        }
    }
}