package paul.fallen.module.modules.movement;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class YawLock extends Module {

    public double yawthis;

    public YawLock(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @Override
    public void onEnable() {
        try {
            super.onEnable();
            assert mc.player != null;
            yawthis = mc.player.rotationYaw;
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            assert mc.player != null;
            mc.player.rotationYaw = (float) yawthis;
        } catch (Exception ignored) {
        }
    }
}