// ------------------------------------------------------------------------------------------------
//
// SG Craft - RF Stargate Power Unit Block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.rf;

import gcewing.sg.blocks.PowerBlock;

public class RFPowerBlock extends PowerBlock<RFPowerTE> {

    public RFPowerBlock() {
        super(RFPowerTE.class, null);
        setModelAndTextures("block/power.smeg", "rfPowerUnit-bottom", "rfPowerUnit-top", "rfPowerUnit-side");
    }

}
