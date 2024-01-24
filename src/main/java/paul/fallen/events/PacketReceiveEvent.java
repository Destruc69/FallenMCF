package paul.fallen.events;

import net.minecraft.network.IPacket;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class PacketReceiveEvent extends Event {

    private IPacket packet;

    public PacketReceiveEvent(IPacket packet) {
        this.packet = packet;
    }

    public IPacket getPacket() {
        return this.packet;
    }

    public void setPacket(IPacket packet) {
        this.packet = packet;
    }

}