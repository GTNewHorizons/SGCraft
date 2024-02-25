// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.8 - Inventory Utilities
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.utils;

import net.minecraft.inventory.IInventory;

public class BaseInventoryUtils {

    public static void clearInventory(IInventory inv) {
        int n = inv.getSizeInventory();
        for (int i = 0; i < n; i++) inv.setInventorySlotContents(i, null);
    }

}
