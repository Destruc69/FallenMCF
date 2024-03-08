package paul.fallen.module.modules.combat;

import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public class ComboAttack extends Module {

    private Setting strength;

    public ComboAttack(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        strength = new Setting("Strength", this, 1, 1, 20);
        addSetting(strength);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        try {
            for (int i = 0; i < strength.dval; i++) {
                mc.player.connection.sendPacket(new CUseEntityPacket(event.getEntity(), Hand.MAIN_HAND, mc.objectMouseOver.getHitVec(), mc.gameSettings.keyBindSneak.isKeyDown() || mc.player.isSneaking()));
            }
        } catch (Exception ignored) {
        }
    }
}
