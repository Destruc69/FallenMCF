package paul.fallen.events.packetevent;

/*
 * By TheAlphaEpsilon
 * 28JAN2020
 *
 */

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@ChannelHandler.Sharable
public class ChannelHandlerInput extends SimpleChannelInboundHandler<Packet> {

	public ChannelHandlerInput() {
		super(false);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) {

	}

	@SubscribeEvent
	public void connect(ClientPlayerNetworkEvent event) {
		ChannelPipeline pipeline = event.getConnection().channel().pipeline();

		pipeline.addBefore("packet_handler", "listener", new PacketListener());
	}
}
