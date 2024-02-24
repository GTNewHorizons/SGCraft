// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate base gui container
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.joml.Vector3i;

import gcewing.sg.guis.containers.slots.CamouflageSlot;
import gcewing.sg.tileentities.SGBaseTE;

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
