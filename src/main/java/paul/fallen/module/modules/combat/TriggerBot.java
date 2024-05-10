package paul.fallen.module.modules.combat;

import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public class TriggerBot extends Module {

    private Setting delay;
    private boolean a = true;

    public TriggerBot(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        delay = new Setting("Delay", this, 10, 0, 20, true);
        addSetting(delay);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (mc.pointedEntity != null) {
                if (mc.player.ticksExisted % delay.getValDouble() == 0) {
                    if (a) {
                        mc.playerController.attackEntity(mc.player, mc.pointedEntity);
                        mc.player.swingArm(Hand.MAIN_HAND);
                        a = false;
                    }
                } else {
                    a = true;
                }
            }
        } catch (Exception ignored) {
        }
    }
}
