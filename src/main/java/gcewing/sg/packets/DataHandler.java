package gcewing.sg.packets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import gcewing.sg.SGCraft;
import gcewing.sg.interfaces.ChannelInput;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class DataHandler extends ChannelInboundHandlerAdapter {

    BaseDataChannel channel;

    DataHandler(BaseDataChannel channel) {
        this.channel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof FMLProxyPacket) handleProxyPacket(ctx, (FMLProxyPacket) obj);
        else SGCraft.log.warn(
                String.format(
                        "BaseDataChannel.DataHandler: Received unexpected message type %s",
                        obj.getClass().getName()));
    }

    protected void handleProxyPacket(ChannelHandlerContext ctx, FMLProxyPacket msg) {
        ChannelInput data = new ChannelInputStream(msg.payload());
        if (ctx.channel() == channel.pipes.get(Side.SERVER)) {
            INetHandler net = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            EntityPlayer player = ((NetHandlerPlayServer) net).playerEntity;
            channel.onReceiveFromClient(player, data);
        } else channel.onReceiveFromServer(data);
    }

}
