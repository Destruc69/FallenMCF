package paul.fallen.module.modules.render;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public final class FullbrightHack extends Module {

    public FullbrightHack(int bind, String name, String displayName, Module.Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {

        if (getState()) {
            if (mc.gameSettings.gamma < 16)
                mc.gameSettings.gamma =
                        Math.min(mc.gameSettings.gamma + 0.5F, 16);

            return;
        }

        if (mc.gameSettings.gamma > 0.5F)
            mc.gameSettings.gamma =
                    Math.max(mc.gameSettings.gamma - 0.5F, 0.5F);
        else
            setState(false);


    }

}