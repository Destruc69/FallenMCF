package paul.fallen.module.modules.movement;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public class FastSwim extends Module {

    private final Setting speed;

    public FastSwim(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        speed = new Setting("Speed", this, 1, 0, 5, true);
        addSetting(speed);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || mc.player == null || mc.world == null)
            return;

        try {
            mc.player.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(speed.getValDouble());
        } catch (Exception ignored) {
        }
    }

}
