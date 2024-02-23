// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate base gui container
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.world.World;
import org.joml.Vector3i;

public class SGBaseContainer extends BaseContainer {

    SGBaseTE te;

    public static SGBaseContainer create(EntityPlayer player, World world, Vector3i pos) {
        SGBaseTE te = SGBaseTE.at(world, pos);
        if (te != null) return new SGBaseContainer(player, te);
        return null;
    }

    public SGBaseContainer(EntityPlayer player, SGBaseTE te) {
        super(256, 208);
        this.te = te;
        addCamouflageSlots();
        addPlayerSlots(player); // (player, playerSlotsX, playerSlotsY);
    }

    void addCamouflageSlots() {
        addSlots(te, 0, SGBaseTE.numCamouflageSlots, 48, 104, 1, CamouflageSlot.class);
    }

}

class CamouflageSlot extends Slot {

    public CamouflageSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

}
