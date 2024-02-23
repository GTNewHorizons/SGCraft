// ------------------------------------------------------------------------------------------------
//
// SG Craft - IC2 Stargate Power Unit Block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.ic2;

import gcewing.sg.blocks.PowerBlock;
import gcewing.sg.blocks.orientation.Orient4WaysByState;

public class IC2PowerBlock extends PowerBlock<IC2PowerTE> {

    public IC2PowerBlock() {
        super(IC2PowerTE.class, Orient4WaysByState.orient4WaysByState);
        setModelAndTextures("block/power.smeg", "ic2PowerUnit-bottom", "ic2PowerUnit-top", "ic2PowerUnit-side");
    }

}
