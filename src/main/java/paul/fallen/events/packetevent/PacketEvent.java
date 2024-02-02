package paul.fallen.events.packetevent;

import net.minecraft.network.protocol.Packet;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/*
 * By TheAlphaEpsilon
 * 28JAN2020
 *
 */


@Cancelable
public class PacketEvent extends Event {

	private Packet<?> packet;

	public PacketEvent(Packet<?> packet) {
		this.packet = packet;
	}

	public Packet<?> getPacket() {
		return packet;
	}

	public void setPacket(Packet<?> packet) {
		this.packet = packet;
	}

	public static class Outgoing extends PacketEvent {

		public Outgoing(Packet<?> packetIn) {
			super(packetIn);
		}

	}

	public static class Incoming extends PacketEvent {

		public Incoming(Packet<?> packetIn) {
			super(packetIn);
		}

	}
}
