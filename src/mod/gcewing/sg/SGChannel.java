//------------------------------------------------------------------------------------------------
//
//   SG Craft - Packet Handling
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import static gcewing.sg.BaseBlockUtils.getTileEntityPos;


public class SGChannel extends BaseDataChannel {

    protected static BaseDataChannel channel;

    public SGChannel(String name) {
        super(name);
        channel = this;
    }

    public static void sendConnectOrDisconnectToServer(SGBaseTE te, String address) {
        ChannelOutput data = channel.openServer("ConnectOrDisconnect");
        writeCoords(data, te);
        data.writeUTF(address);
        data.close();
    }

    public static void sendEnteredAddressToServer(DHDTE te, String address) {
        ChannelOutput data = channel.openServer("EnteredAddress");
        writeCoords(data, te);
        data.writeUTF(address);
        data.close();
    }

    public static void writeCoords(ChannelOutput data, TileEntity te) {
        BaseUtils.writeBlockPos(data, getTileEntityPos(te));
    }

    @ServerMessageHandler("ConnectOrDisconnect")
    public void handleConnectOrDisconnectFromClient(EntityPlayer player, ChannelInput data) {
        BlockPos pos = readCoords(data);
        String address = data.readUTF();
        SGBaseTE te = SGBaseTE.at(player.worldObj, pos);
        System.out.printf("SGChannel.handleConnectOrDisconnectFromClient: %s %s %s\n",
                pos, address, te);
        if (te != null)
            te.connectOrDisconnect(address, player);
    }

    @ServerMessageHandler("EnteredAddress")
    public void handleEnteredAddressFromClient(EntityPlayer player, ChannelInput data) {
        BlockPos pos = readCoords(data);
        String address = data.readUTF();
        DHDTE te = DHDTE.at(player.worldObj, pos);
        if (te != null)
            te.setEnteredAddress(address);
    }

    public BlockPos readCoords(ChannelInput data) {
        return BaseUtils.readBlockPos(data);
    }

}
