package paul.fallen.module.modules.world;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class OverKill extends Module {

    public OverKill(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (Thread.currentThread().getPriority() != Thread.MAX_PRIORITY) {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            }
        } catch (Exception ignored) {
        }
    }
}
