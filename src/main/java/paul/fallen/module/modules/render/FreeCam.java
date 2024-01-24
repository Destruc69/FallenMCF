package paul.fallen.module.modules.render;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.events.PacketSendEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.EntityUtils;

public final class FreeCam extends Module {

    public FreeCam(int bind, String name, String displayName, Module.Category category) {
        super(bind, name, displayName, category);
        setState(true);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = event.player;

            assert mc.player != null;
            mc.player.setMotion(0, 0, 0);

            player.setOnGround(false);
            player.jumpMovementFactor = 0.6f;

            if (mc.gameSettings.keyBindJump.isKeyDown())
                EntityUtils.setMotionY(0.3);

            if (mc.gameSettings.keyBindSneak.isKeyDown())
                EntityUtils.setMotionY(-0.3);

            player.noClip = true;
            player.setOnGround(false);
            player.collidedHorizontally = false;
            player.collidedVertically = false;
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacketOutput(PacketSendEvent event) {
        if (event.getPacket() instanceof CPlayerPacket)
            event.setCanceled(true);
    }
}