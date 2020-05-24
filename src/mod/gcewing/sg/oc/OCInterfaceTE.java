//------------------------------------------------------------------------------------------------
//
//   SG Craft - Open Computers Interface Tile Entity
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.oc;

import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Value;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.Visibility;

import gcewing.sg.*;

public class OCInterfaceTE extends SGInterfaceTE
    implements IComputerInterface, Environment, IInventory, ITickable
{

    static boolean debugConnection = false;
    static boolean debugNetworking = false;
    
    final static int numUpgradeSlots = 1;
    
    IInventory inventory = new InventoryBasic("", false, numUpgradeSlots);

    public OCInterfaceTE() {
        node = Network.newNode(this, Visibility.Network)
            .withComponent("stargate", Visibility.Network)
            .create();
        //System.out.printf("OCInterfaceTE: Created node %s\n", node);
    }

    //@Override 
    protected IInventory getInventory() {
        return inventory;
    }
    
    boolean hasNetworkCard() {
        return isNetworkCard(getStackInSlot(0));
        //return false;
    }
    
    static boolean isNetworkCard(ItemStack stack) {
        //System.out.printf("OCInterfaceTE.isNetworkCard: comparing %s with %s\n",
        //  stack, OCIntegration.networkCard);
        return stack != null && OCIntegration.networkCard.isItemEqual(stack);
    }
    
    void forwardNetworkPacket(Packet packet) {
        if (packet.ttl() > 0) {
            SGBaseTE te = getBaseTE();
            if (te != null)
                te.forwardNetworkPacket(packet.hop());
        }
    }

    @Override   
    public void rebroadcastNetworkPacket(Object packet) {
        if (packet instanceof Packet && hasNetworkCard()) {
            if (node != null) {
                if (debugNetworking)
                    System.out.printf("OCInterfaceTE.rebroadcastNetworkPacket\n");
                node.sendToReachable("network.message", packet);
            }
        }
    }

    // -------------------------- Methods --------------------------
    
    protected static Object[] success = {true};
    
    protected static Object[] failure(Exception e) {
        return new Object[] {null, e.getMessage()};
    }
    
    @Callback
    public Object[] stargateState(Context ctx, Arguments args) {
        try {
            CIStargateState result = ciStargateState();
            return new Object[]{result.state, result.chevrons, result.direction};
        }
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] energyAvailable(Context ctx, Arguments args) {
        try {return new Object[]{ciEnergyAvailable()};}
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] energyToDial(Context ctx, Arguments args) {
        try {return new Object[]{ciEnergyToDial(args.checkString(0))};}
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] localAddress(Context ctx, Arguments args) {
        try {return new Object[]{ciLocalAddress()};}
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] remoteAddress(Context ctx, Arguments args) {
        try {return new Object[]{ciRemoteAddress()};}
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] dial(Context ctx, Arguments args) {
        try {ciDial(args.checkString(0)); return success;}
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] disconnect(Context ctx, Arguments args) {
        try {ciDisconnect(); return success;}
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] irisState(Context ctx, Arguments args) {
        try {return new Object[]{ciIrisState()};}
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] openIris(Context ctx, Arguments args) {
        try {ciOpenIris(); return success;}
        catch (Exception e) {return failure(e);}
    }
    
    @Callback
    public Object[] closeIris(Context ctx, Arguments args) {
        try {ciCloseIris(); return success;}
        catch (Exception e) {return failure(e);}
    }

    @Callback
    public Object[] sendMessage(Context ctx, Arguments args) {
        try {
            int n = args.count();
            Object[] objs = new Object[n];
            for (int i = 0; i < n; i++)
                objs[i] = args.checkAny(i);
            ciSendMessage(objs);
            return success;
        }
        catch (Exception e) {return failure(e);}
    }       
        
    // -------------------------- Environment --------------------------

    /**
     * This must be set in subclasses to the node that is used to represent
     * this tile entity.
     * <p/>
     * You must only create new nodes using the factory method in the network
     * API, {@link li.cil.oc.api.Network#newNode(Environment, Visibility)}.
     * <p/>
     * For example:
     * <pre>
     * // The first parameters to newNode is the host() of the node, which will
     * // usually be this tile entity. The second one is it's reachability,
     * // which determines how other nodes in the same network can query this
     * // node. See {@link li.cil.oc.api.network.Network#nodes(li.cil.oc.api.network.Node)}.
     * node = Network.newNode(this, Visibility.Network)
     *       // This call allows the node to consume energy from the
     *       // component network it is in and act as a consumer, or to
     *       // inject energy into that network and act as a producer.
     *       // If you do not need energy remove this call.
     *       .withConnector()
     *       // This call marks the tile entity as a component. This means you
     *       // can mark methods in it using the {@link li.cil.oc.api.network.Callback}
     *       // annotation, making them callable from user code. The first
     *       // parameter is the name by which the component will be known in
     *       // the computer, in this case it could be accessed as
     *       // <tt>component.example</tt>. The second parameter is the
     *       // component's visibility. This is like the node's reachability,
     *       // but only applies to computers. For example, network cards can
     *       // only be <em>seen</em> by the computer they're installed in, but
     *       // can be <em>reached</em> by all other network cards in the same
     *       // network. If you do not need callbacks remove this call.
     *       .withComponent("example", Visibility.Neighbors)
     *       // Finalizes the construction of the node and returns it.
     *       .create();
     * </pre>
     */
    protected Node node;

    protected boolean addedToNetwork = false;

    @Override
    public Node node() {
        return node;
    }

    @Override
    public void onConnect(final Node node) {
        // This is called when the call to Network.joinOrCreateNetwork(this) in
        // updateEntity was successful, in which case `node == this`.
        // This is also called for any other node that gets connected to the
        // network our node is in, in which case `node` is the added node.
        // If our node is added to an existing network, this is called for each
        // node already in said network.
        if (debugConnection)
            System.out.printf("OCInterfaceTE.onConnect: %s\n", node);
    }

    @Override
    public void onDisconnect(final Node node) {
        // This is called when this node is removed from its network when the
        // tile entity is removed from the world (see onChunkUnload() and
        // invalidate()), in which case `node == this`.
        // This is also called for each other node that gets removed from the
        // network our node is in, in which case `node` is the removed node.
        // If a net-split occurs this is called for each node that is no longer
        // connected to our node.
        if (debugConnection)
            System.out.printf("OCInterfaceTE.onDisconnect: %s\n", node);
    }

    @Override
    public void onMessage(final Message msg) {
        // This is used to deliver messages sent via node.sendToXYZ. Handle
        // messages at your own discretion. If you do not wish to handle a
        // message you should *not* throw an exception, though.
        if (msg.name().equals("network.message") && hasNetworkCard()) {
            if (debugNetworking) {
                System.out.printf("OCInterfaceTE.onMessage from %s: %s", msg.source(), msg.name());
                for (Object obj : msg.data())
                    System.out.printf(" %s", obj);
                System.out.printf("\n");
            }
                forwardNetworkPacket((Packet)msg.data()[0]);
        }
    }

    // ----------------------------------------------------------------------- //

    @Override
    public void update() {
        // On the first update, try to add our node to nearby networks. We do
        // this in the update logic, not in validate() because we need to access
        // neighboring tile entities, which isn't possible in validate().
        // We could alternatively check node != null && node.network() == null,
        // but this has somewhat better performance, and makes it clearer.
        if (!addedToNetwork) {
            addedToNetwork = true;
            Network.joinOrCreateNetwork(this);
//          Network.joinWirelessNetwork(this);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        // Make sure to remove the node from its network when its environment,
        // meaning this tile entity, gets unloaded.
        onRemoved();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // Make sure to remove the node from its network when its environment,
        // meaning this tile entity, gets unloaded.
        onRemoved();
    }
    
    void onRemoved() {
        if (node != null)
            node.remove();
//      Network.leaveWirelessNetwork(this);
    }
    
    // ----------------------------------------------------------------------- //

    @Override
    public void readContentsFromNBT(final NBTTagCompound nbt) {
        super.readContentsFromNBT(nbt);
        // The host check may be superfluous for you. It's just there to allow
        // some special cases, where getNode() returns some node managed by
        // some other instance (for example when you have multiple internal
        // nodes in this tile entity).
        if (node != null && node.host() == this) {
            // This restores the node's address, which is required for networks
            // to continue working without interruption across loads. If the
            // node is a power connector this is also required to restore the
            // internal energy buffer of the node.
            node.load(nbt.getCompoundTag("oc:node"));
        }
    }

    @Override
    public void writeContentsToNBT(final NBTTagCompound nbt) {
        super.writeContentsToNBT(nbt);
        // See readFromNBT() regarding host check.
        if (node != null && node.host() == this) {
            final NBTTagCompound nodeNbt = new NBTTagCompound();
            node.save(nodeNbt);
            nbt.setTag("oc:node", nodeNbt);
        }
    }

    // -------------------------- IComputerInterface --------------------------

    public void postEvent(TileEntity source, String name, Object... args) {
        //System.out.printf("OCInterfaceTE.postEvent: %s to %s\n", name, node);
        if (node != null)
            node.sendToReachable("computer.signal", prependArgs(name, args));
    }
    
//------------------------------------- IInventory -----------------------------------------

    void onInventoryChanged(int slot) {
        markDirty();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        IInventory inventory = getInventory();
        return (inventory != null) ? inventory.getSizeInventory() : 0;
    }   

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int slot) {
        IInventory inventory = getInventory();
        return (inventory != null) ? inventory.getStackInSlot(slot) : null;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int slot, int amount) {
        IInventory inventory = getInventory();
        if (inventory != null) {
            ItemStack result = inventory.decrStackSize(slot, amount);
            onInventoryChanged(slot);
            return result;
        }
        else
            return null;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int slot) {
        IInventory inventory = getInventory();
        if (inventory != null) {
            ItemStack result = inventory.getStackInSlotOnClosing(slot);
            onInventoryChanged(slot);
            return result;
        }
        else
            return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int slot, ItemStack stack) {
        IInventory inventory = getInventory();
        if (inventory != null) {
            inventory.setInventorySlotContents(slot, stack);
            onInventoryChanged(slot);
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInventoryName() {
        IInventory inventory = getInventory();
        return (inventory != null) ? inventory.getInventoryName() : "";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit() {
        IInventory inventory = getInventory();
        return (inventory != null) ? inventory.getInventoryStackLimit() : 0;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player) {
        IInventory inventory = getInventory();
        return (inventory != null) ? inventory.isUseableByPlayer(player) : true;
    }

    public void openInventory() {
        IInventory inventory = getInventory();
        if (inventory != null)
            inventory.openInventory();
    }

    public void closeInventory() {
        IInventory inventory = getInventory();
        if (inventory != null)
            inventory.closeInventory();
    }
    
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        IInventory inventory = getInventory();
        if (inventory != null)
            return inventory.isItemValidForSlot(slot, stack);
        else
            return false;
    }
    
    public boolean hasCustomInventoryName() {
        IInventory inventory = getInventory();
        if (inventory != null)
            return inventory.hasCustomInventoryName();
        else
            return false;
    }

}
