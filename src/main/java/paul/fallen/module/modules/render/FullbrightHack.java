package paul.fallen.module.modules.render;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class FullbrightHack extends Module {

    public FullbrightHack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @Override
    public void onEnable() {
        mc.gameSettings.gamma = 500;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gamma = 1;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {

    }
}