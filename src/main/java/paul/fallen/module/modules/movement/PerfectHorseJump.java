package paul.fallen.module.modules.movement;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;

public class PerfectHorseJump extends Module {

    public PerfectHorseJump(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        try {
            if (mc.player.isRidingHorse()) {
                HorseEntity horseEntity = (HorseEntity) mc.player.getRidingEntity();
                horseEntity.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(0.7);
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || mc.player == null || mc.world == null)
            return;

        try {
            if (mc.player.isRidingHorse()) {
                HorseEntity horseEntity = (HorseEntity) mc.player.getRidingEntity();
                horseEntity.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(2);
            }
        } catch (Exception ignored) {
        }
    }
}
