// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate controller fuelling gui container
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.joml.Vector3i;

import gcewing.sg.guis.DHDTE;
import gcewing.sg.guis.containers.slots.FuelSlot;
import gcewing.sg.guis.screens.DHDFuelScreen;

public class DHDFuelContainer extends BaseContainer {

    static final int numFuelSlotColumns = 2;
    static final int fuelSlotsX = 174;
    static final int fuelSlotsY = 84;
    static final int playerSlotsX = 48;
    static final int playerSlotsY = 124;

    DHDTE te;

    public static DHDFuelContainer create(EntityPlayer player, World world, Vector3i pos) {
        DHDTE te = DHDTE.at(world, pos);
        if (te != null) return new DHDFuelContainer(player, te);
        else return null;
    }

    public DHDFuelContainer(EntityPlayer player, DHDTE te) {
        super(DHDFuelScreen.guiWidth, DHDFuelScreen.guiHeight);
        this.te = te;
        addFuelSlots();
        addPlayerSlots(player, playerSlotsX, playerSlotsY);
    }

    void addFuelSlots() {
        int b = DHDTE.firstFuelSlot;
        int n = DHDTE.numFuelSlots;
        for (int i = 0; i < n; i++) {
            int row = i / numFuelSlotColumns;
            int col = i % numFuelSlotColumns;
            int x = fuelSlotsX + col * 18;
            int y = fuelSlotsY + row * 18;
            addSlotToContainer(new FuelSlot(te, b + i, x, y));
        }
    }

}
