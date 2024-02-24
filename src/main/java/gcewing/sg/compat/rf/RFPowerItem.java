// ------------------------------------------------------------------------------------------------
//
// SG Craft - RF Stargate Power Unit Item
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.rf;

import net.minecraft.block.Block;

import gcewing.sg.items.PowerItem;

public class RFPowerItem extends PowerItem {

    public RFPowerItem(Block block) {
        super(block, "RF", RFPowerTE.maxEnergyBuffer);
    }

}
