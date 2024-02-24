// ------------------------------------------------------------------------------------------------
//
// SG Craft - RF Stargate Power Unit Item
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.rf;

import gcewing.sg.items.PowerItem;
import net.minecraft.block.Block;

public class RFPowerItem extends PowerItem {

    public RFPowerItem(Block block) {
        super(block, "RF", RFPowerTE.maxEnergyBuffer);
    }

}
