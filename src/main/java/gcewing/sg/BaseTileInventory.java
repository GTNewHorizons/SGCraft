// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base - Generic Tile Entity
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class BaseTileInventory extends BaseTileEntity implements IInventory, ISidedInventory {

    int[] allSlots;

    protected IInventory getInventory() {
        return null;
    }

    @Override
    public void readContentsFromNBT(NBTTagCompound nbt) {
        super.readContentsFromNBT(nbt);
        IInventory inventory = getInventory();
        if (inventory == null) {
            return;
        }

        NBTTagList list = nbt.getTagList("inventory", 10);
        int n = list.tagCount();
        for (int i = 0; i < n; i++) {
            NBTTagCompound item = (NBTTagCompound) list.getCompoundTagAt(i);
            int slot = item.getInteger("slot");
            ItemStack stack = ItemStack.loadItemStackFromNBT(item);
            inventory.setInventorySlotContents(slot, stack);
        }
    }

    @Override
    public void writeContentsToNBT(NBTTagCompound nbt) {
        super.writeContentsToNBT(nbt);
        IInventory inventory = getInventory();
        if (inventory == null) {
            return;
        }
        NBTTagList list = new NBTTagList();
        int n = inventory.getSizeInventory();
        for (int i = 0; i < n; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }

            NBTTagCompound item = new NBTTagCompound();
            item.setInteger("slot", i);
            stack.writeToNBT(item);
            list.appendTag(item);
        }
        nbt.setTag("inventory", list);

    }

    // ------------------------------------- IInventory -----------------------------------------

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
        return null;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int slot) {
        IInventory inventory = getInventory();
        if (inventory == null) {
            return null;
        }

        ItemStack result = inventory.getStackInSlotOnClosing(slot);
        onInventoryChanged(slot);
        return result;
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
        return inventory == null || inventory.isUseableByPlayer(player);
    }

    public void openInventory() {
        IInventory inventory = getInventory();
        if (inventory != null) inventory.openInventory();
    }

    public void closeInventory() {
        IInventory inventory = getInventory();
        if (inventory != null) inventory.closeInventory();
    }

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        IInventory inventory = getInventory();
        if (inventory != null) return inventory.isItemValidForSlot(slot, stack);
        return false;
    }

    public boolean hasCustomInventoryName() {
        IInventory inventory = getInventory();
        if (inventory != null) return inventory.hasCustomInventoryName();
        return false;
    }

    // ------------------------------------- ISidedInventory -----------------------------------------

    /**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     */
    public int[] getAccessibleSlotsFromSide(int side) {
        IInventory inventory = getInventory();
        if (inventory instanceof ISidedInventory) return ((ISidedInventory) inventory).getAccessibleSlotsFromSide(side);

        if (allSlots == null) {
            int n = getSizeInventory();
            allSlots = new int[n];
            for (int i = 0; i < n; i++) allSlots[i] = i;
        }
        return allSlots;
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        IInventory inventory = getInventory();
        if (inventory instanceof ISidedInventory) return ((ISidedInventory) inventory).canInsertItem(slot, stack, side);
        return true;
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        IInventory inventory = getInventory();
        if (inventory instanceof ISidedInventory)
            return ((ISidedInventory) inventory).canExtractItem(slot, stack, side);
        return true;
    }

}
