package gcewing.sg.guis.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class CamouflageSlot extends Slot {

    public CamouflageSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

}
