package paul.fallen.module.modules.render;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;

public class BodySpin extends Module {

    private Setting backward;

    public BodySpin(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        backward = new Setting("Backward", this, false);
        addSetting(backward);
    }

    private float yaw = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (!backward.getValBoolean()) {
                mc.player.renderYawOffset = yaw;
                if (yaw + 1 <= 360) {
                    yaw++;
                } else {
                    yaw = 0;
                }
            } else {
                mc.player.renderYawOffset = mc.player.rotationYaw + 180;
            }
        } catch (Exception ignored) {
        }
    }
}
