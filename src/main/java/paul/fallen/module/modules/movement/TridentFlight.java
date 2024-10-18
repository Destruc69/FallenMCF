package paul.fallen.module.modules.movement;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class TridentFlight extends Module {
    public TridentFlight(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (mc.player.isSpinAttacking()) {
                mc.player.setMotion(mc.player.getMotion().x * 1.025, mc.player.getMotion().y, mc.player.getMotion().z * 1.025);
            }
        } catch (Exception ignored) {
        }
    }
}
