// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Data Channel Networking
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import gcewing.sg.interfaces.ChannelInput;
import gcewing.sg.interfaces.ChannelOutput;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gcewing.sg.SGCraft;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BaseDataChannel {

    public String name;
    public List handlers = new ArrayList();
    protected EnumMap<Side, FMLEmbeddedChannel> pipes;

    public BaseDataChannel(String name, Object... handlers) {
        this.name = name;
        ChannelHandler handler = new DataHandler(this);
        pipes = NetworkRegistry.INSTANCE.newChannel(name, handler);
        this.handlers.add(this);
        for (Object h : handlers) this.handlers.add(h);
    }

    protected ChannelOutput openTarget(String message, Side fromSide, OutboundTarget target) {
        return openTarget(message, fromSide, target, null);
    }

    protected ChannelOutput openTarget(String message, Side fromSide, OutboundTarget target, Object arg) {
        ChannelOutput out = new DataPacket(this, fromSide, target, arg);
        out.writeUTF(message);
        return out;
    }

    public ChannelOutput openServer(String message) {
        return openTarget(message, Side.CLIENT, OutboundTarget.TOSERVER);
    }

    public ChannelOutput openPlayer(EntityPlayer player, String message) {
        return openTarget(message, Side.SERVER, OutboundTarget.PLAYER, player);
    }

    public ChannelOutput openAllPlayers(String message) {
        return openTarget(message, Side.SERVER, OutboundTarget.ALL);
    }

    public ChannelOutput openAllAround(NetworkRegistry.TargetPoint point, String message) {
        return openTarget(message, Side.SERVER, OutboundTarget.ALLAROUNDPOINT, point);
    }

    public ChannelOutput openDimension(int dimensionId, String message) {
        return openTarget(message, Side.SERVER, OutboundTarget.DIMENSION, dimensionId);
    }

    public ChannelOutput openServerContainer(String message) {
        ChannelOutput out = openServer(".container.");
        out.writeUTF(message);
        return out;
    }

    public ChannelOutput openClientContainer(EntityPlayer player, String message) {
        ChannelOutput out = openPlayer(player, ".container.");
        out.writeUTF(message);
        return out;
    }

    @ServerMessageHandler(".container.")
    public void onServerContainerMessage(EntityPlayer player, ChannelInput data) {
        String message = data.readUTF();
        doServerDispatch(player.openContainer, message, player, data);
    }

    @SideOnly(Side.CLIENT)
    @ClientMessageHandler(".container.")
    public void onClientContainerMessage(ChannelInput data) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        String message = data.readUTF();
        doClientDispatch(player.openContainer, message, data);
    }

    protected void onReceiveFromClient(EntityPlayer player, ChannelInput data) {
        String message = data.readUTF();
        for (Object h : handlers) if (serverDispatch(h, message, player, data)) return;
        SGCraft.log.warn(
                String.format("No ServerMessageHandler for '%s' found in registered handlers of %s", message, this));
    }

    public static void doServerDispatch(Object handler, String message, EntityPlayer player, ChannelInput data) {
        if (!serverDispatch(handler, message, player, data)) SGCraft.log.warn(
                String.format("No ServerMessageHandler for '%s' found in %s", message, handler.getClass().getName()));
    }

    public static boolean serverDispatch(Object handler, String message, EntityPlayer player, ChannelInput data) {
        if (handler != null) {
            // Method meth = findHandlerMethod(handler, message, HandlerAnnotation.SERVER, serverHandlerCaches);
            Method meth = HandlerMap.SERVER.get(handler, message);
            if (meth != null) {
                try {
                    meth.invoke(handler, player, data);
                } catch (Exception e) {
                    throw new RuntimeException(
                            String.format(
                                    "Exception while calling server-side handler %s.%s for message %s",
                                    handler.getClass().getName(),
                                    meth.getName(),
                                    message),
                            e);
                }
                return true;
            }
        }
        return false;
    }

    protected void onReceiveFromServer(ChannelInput data) {
        String message = data.readUTF();
        for (Object h : handlers) if (clientDispatch(h, message, data)) return;
        SGCraft.log.warn(String.format("No ClientMessageHandler for '%s' found in registered handlers", message));
    }

    public static void doClientDispatch(Object handler, String message, ChannelInput data) {
        if (!clientDispatch(handler, message, data)) SGCraft.log.warn(
                String.format("No ClientMessageHandler for '%s' found in %s", message, handler.getClass().getName()));
    }

    public static boolean clientDispatch(Object handler, String message, ChannelInput data) {
        if (handler != null) {
            // Method meth = findHandlerMethod(handler, message, HandlerAnnotation.CLIENT, clientHandlerCaches);
            Method meth = HandlerMap.CLIENT.get(handler, message);
            if (meth != null) {
                try {
                    meth.invoke(handler, data);
                } catch (Exception e) {
                    throw new RuntimeException(
                            String.format(
                                    "Exception while calling client-side handler %s.%s for message %s",
                                    handler.getClass().getName(),
                                    meth.getName(),
                                    message),
                            e);
                }
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------------------------------

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ServerMessageHandler {

        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ClientMessageHandler {

        String value();
    }
}
