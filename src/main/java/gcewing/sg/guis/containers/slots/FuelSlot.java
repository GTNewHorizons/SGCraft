package gcewing.sg.guis.containers.slots;

import gcewing.sg.guis.DHDTE;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FuelSlot extends Slot {

    public FuelSlot(IInventory inv, int i, int x, int y) {
        super(inv, i, x, y);
    }

    public boolean isItemValid(ItemStack stack) {
        return DHDTE.isValidFuelItem(stack);
    }

}
