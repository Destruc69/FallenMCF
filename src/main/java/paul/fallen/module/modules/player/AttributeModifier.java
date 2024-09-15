package paul.fallen.module.modules.player;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public class AttributeModifier extends Module {

    private Setting g;
    private Setting gVal;


    public AttributeModifier(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        g = new Setting("Gravity", this, false);
        gVal = new Setting("G-Value", this, 0.08, 0.01, 2, false);

        addSetting(g);
        addSetting(gVal);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        // net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()
        // Attributes.

        if (g.getValBoolean()) {
            mc.player.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(gVal.getValDouble());
        } else {
            mc.player.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(0.08);
        }
    }
}
