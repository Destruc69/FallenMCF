package paul.fallen.module.modules.player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

import java.lang.reflect.Field;

public class AntiForge extends Module {

    public AntiForge(int bind, String name, String displayName, Category category) {
        super(bind, name, displayName, category);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!mc.isIntegratedServerRunning()) {
            if (event.getPacket() instanceof CCustomPayloadPacket) {
                CCustomPayloadPacket customPayload = (CCustomPayloadPacket) event.getPacket();
                if (customPayload.getInternalData().equals("MC|Brand")) {
                    try {
                        Field dataField = CCustomPayloadPacket.class.getDeclaredField("data");
                        dataField.setAccessible(true);
                        ByteBuf newData = Unpooled.buffer();
                        newData.writeBytes("vanilla".getBytes());
                        dataField.set(customPayload, newData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
