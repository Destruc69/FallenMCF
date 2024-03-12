package paul.fallen.module.modules.world;

import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.setting.Setting;

public class FastBreak extends Module {

    private Setting addition;
    private Setting bypass;

    public FastBreak(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);

        addition = new Setting("Addition", this, 2, 1, 20);
        bypass = new Setting("Bypass", this, false);
        addSetting(addition);
        addSetting(bypass);
    }

    @SubscribeEvent
    public void onTick(PlayerEvent.BreakSpeed event) {
        try {
            if (bypass.bval) {
                mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                        CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, event.getPos(), mc.player.getHorizontalFacing()));
                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK,
                        event.getPos(),  mc.player.getHorizontalFacing()));
            } else {
                event.setNewSpeed(mc.player.getActiveItemStack().getDestroySpeed(mc.world.getBlockState(event.getPos())) + addition.dval);
            }
        } catch (Exception ignored) {
        }
    }
}
