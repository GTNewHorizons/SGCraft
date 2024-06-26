// ------------------------------------------------------------------------------------------------
//
// SG Craft - Computercraft Interface Block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.cc;

import gcewing.sg.SGCraft;
import gcewing.sg.blocks.SGInterfaceBlock;

public class CCInterfaceBlock extends SGInterfaceBlock<CCInterfaceTE> {

    public CCInterfaceBlock() {
        super(SGCraft.machineMaterial, CCInterfaceTE.class);
        setModelAndTextures(
                "block/interface.smeg",
                "ccInterface-bottom",
                "ccInterface-top",
                "ccInterface-front",
                "ccInterface-side");
    }

}
