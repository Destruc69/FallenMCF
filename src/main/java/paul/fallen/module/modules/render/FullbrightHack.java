package paul.fallen.module.modules.render;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class FullbrightHack extends Module {

    public FullbrightHack(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.gamma = 1;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            mc.gameSettings.gamma = 500;
        } catch (Exception ignored) {
        }
    }
}