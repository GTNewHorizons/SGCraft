// ------------------------------------------------------------------------------------------------
//
// SG Craft - IC2 Stargate Power Unit Block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.ic2;

import gcewing.sg.blocks.PowerBlock;
import gcewing.sg.blocks.orientation.Orient4WaysByState;
import gcewing.sg.interfaces.IOrientationHandler;

public class IC2PowerBlock extends PowerBlock<IC2PowerTE> {

    protected static IOrientationHandler orient4WaysByState = new Orient4WaysByState();

    public IC2PowerBlock() {
        super(IC2PowerTE.class, orient4WaysByState);
        setModelAndTextures("block/power.smeg", "ic2PowerUnit-bottom", "ic2PowerUnit-top", "ic2PowerUnit-side");
    }

}
