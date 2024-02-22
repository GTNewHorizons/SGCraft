// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate Controller Tile Entity
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static gcewing.sg.BaseBlockUtils.getTileEntityPos;
import static gcewing.sg.BaseBlockUtils.getWorldTileEntity;
import static java.lang.Math.min;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import org.joml.Vector3i;

public class DHDTE extends BaseTileInventory implements ISGEnergySource {

    // Debug options
    public static boolean debugLink = false;

    // Configuration options
    public static int linkRangeX = 5; // either side
    public static int linkRangeY = 1; // up or down
    public static int linkRangeZ = 6; // in front

    // Inventory slots
    public static final int firstFuelSlot = 0;
    public static final int numFuelSlots = 4;
    public static final int numSlots = numFuelSlots;

    // Persisted fields
    public boolean isLinkedToStargate;
    public Vector3i linkedPos = new Vector3i(0, 0, 0);
    public String enteredAddress = "";
    IInventory inventory = new InventoryBasic("DHD", false, numSlots);

    static AxisAlignedBB bounds;
    static double maxEnergyBuffer;

    double energyInBuffer;

    public static void configure(BaseConfiguration cfg) {
        linkRangeX = cfg.getInteger("dhd", "linkRangeX", linkRangeX);
        linkRangeY = cfg.getInteger("dhd", "linkRangeY", linkRangeY);
        linkRangeZ = cfg.getInteger("dhd", "linkRangeZ", linkRangeZ);
        maxEnergyBuffer = SGBaseTE.energyPerFuelItem;
    }

    public static DHDTE at(IBlockAccess world, Vector3i pos) {
        TileEntity te = getWorldTileEntity(world, pos);
        if (te instanceof DHDTE) return (DHDTE) te;
        return null;
    }

    public static DHDTE at(IBlockAccess world, NBTTagCompound nbt) {
        Vector3i pos = new Vector3i(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
        return DHDTE.at(world, pos);
    }

    public void setEnteredAddress(String address) {
        enteredAddress = address;
        markChanged();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return bounds.addCoord(getX() + 0.5, getY(), getZ() + 0.5);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 32768.0;
    }

    @Override
    protected IInventory getInventory() {
        return inventory;
    }

    public DHDBlock getBlock() {
        return (DHDBlock) getBlockType();
    }

    @Override
    public void readContentsFromNBT(NBTTagCompound nbt) {
        super.readContentsFromNBT(nbt);
        isLinkedToStargate = nbt.getBoolean("isLinkedToStargate");
        energyInBuffer = nbt.getDouble("energyInBuffer");
        int x = nbt.getInteger("linkedX");
        int y = nbt.getInteger("linkedY");
        int z = nbt.getInteger("linkedZ");
        linkedPos = new Vector3i(x, y, z);
        enteredAddress = nbt.getString("enteredAddress");
    }

    @Override
    public void writeContentsToNBT(NBTTagCompound nbt) {
        super.writeContentsToNBT(nbt);
        nbt.setBoolean("isLinkedToStargate", isLinkedToStargate);
        nbt.setDouble("energyInBuffer", energyInBuffer);
        nbt.setInteger("linkedX", linkedPos.x);
        nbt.setInteger("linkedY", linkedPos.y);
        nbt.setInteger("linkedZ", linkedPos.z);
        nbt.setString("enteredAddress", enteredAddress);
    }

    SGBaseTE getLinkedStargateTE() {
        if (isLinkedToStargate) {
            TileEntity gte = getWorldTileEntity(worldObj, linkedPos);
            if (gte instanceof SGBaseTE) return (SGBaseTE) gte;
        }
        return null;
    }

    void checkForLink() {
        if (debugLink) SGCraft.log.debug(
                String.format("DHDTE.checkForLink at %s: isLinkedToStargate = %s", getPos(), isLinkedToStargate));
        if (isLinkedToStargate) {
            return;
        }

        Trans3 t = localToGlobalTransformation();
        for (int i = -linkRangeX; i <= linkRangeX; i++) {
            for (int j = -linkRangeY; j <= linkRangeY; j++) for (int k = 1; k <= linkRangeZ; k++) {
                Vector3 p = t.p(i, j, -k);
                Vector3i blockPos = new Vector3i(p.floorX(), p.floorY(), p.floorZ());

                if (debugLink) SGCraft.log.debug(String.format("DHDTE.checkForLink: probing %s", blockPos));

                TileEntity te = getWorldTileEntity(worldObj, blockPos);
                if (!(te instanceof SGBaseTE)) {
                    continue;
                }

                if (debugLink)
                    SGCraft.log.debug(String.format("DHDTE.checkForLink: Found stargate at %s", getTileEntityPos(te)));

                if (linkToStargate((SGBaseTE) te)) {
                    return;
                }
            }
        }
    }

    boolean linkToStargate(SGBaseTE gte) {
        if (isLinkedToStargate || gte.isLinkedToController || !gte.isMerged) {
            return false;
        }

        if (debugLink) SGCraft.log.debug(
                String.format(
                        "DHDTE.linkToStargate: Linking controller at %s with stargate at %s",
                        getPos(),
                        getTileEntityPos(gte)));
        linkedPos = gte.getPos();
        isLinkedToStargate = true;
        markChanged();
        gte.linkedPos = getPos();
        gte.isLinkedToController = true;
        gte.markChanged();
        return true;
    }

    public void clearLinkToStargate() {
        if (debugLink) SGCraft.log.debug(String.format("DHDTE: Unlinking controller at %s from stargate", getPos()));
        isLinkedToStargate = false;
        markChanged();
    }

    @Override
    public double availableEnergy() {
        double energy = energyInBuffer;
        for (int i = 0; i < numFuelSlots; i++) {
            ItemStack stack = fuelStackInSlot(i);
            if (stack != null) energy += stack.stackSize * SGBaseTE.energyPerFuelItem;
        }
        return energy;
    }

    @Override
    public double drawEnergy(double amount) {
        double energyDrawn = 0;
        while (energyDrawn < amount) {
            if (energyInBuffer == 0) {
                if (!useFuelItem()) break;
            }
            double e = min(amount, energyInBuffer);
            energyDrawn += e;
            energyInBuffer -= e;
        }
        if (SGBaseTE.debugEnergyUse) SGCraft.log.debug(
                String.format("DHDTE.drawEnergy: %s; supplied: %s; buffered: %s", amount, energyDrawn, energyInBuffer));
        markChanged();
        return energyDrawn;
    }

    boolean useFuelItem() {
        for (int i = numFuelSlots - 1; i >= 0; i--) {
            ItemStack stack = fuelStackInSlot(i);
            if (stack != null) {
                decrStackSize(i, 1);
                energyInBuffer += SGBaseTE.energyPerFuelItem;
                return true;
            }
        }
        return false;
    }

    ItemStack fuelStackInSlot(int i) {
        ItemStack stack = getStackInSlot(firstFuelSlot + i);
        if (isValidFuelItem(stack)) return stack;
        return null;
    }

    public static boolean isValidFuelItem(ItemStack stack) {
        return stack != null && stack.getItem() == SGCraft.naquadah && stack.stackSize > 0;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return isValidFuelItem(stack);
    }

}
