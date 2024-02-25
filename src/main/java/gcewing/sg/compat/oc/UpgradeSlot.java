package gcewing.sg.compat.oc;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class UpgradeSlot extends Slot {

    public UpgradeSlot(IInventory inv, int i, int x, int y) {
        super(inv, i, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return OCInterfaceTE.isNetworkCard(stack);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

}
