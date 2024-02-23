// ------------------------------------------------------------------------------------------------
//
// SG Craft - Naquadah alloy block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

import gcewing.sg.blocks.base.BaseBlock;

public class NaquadahBlock extends BaseBlock {

    public NaquadahBlock() {
        super(Material.rock);
        mapColor = MapColor.greenColor;
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
    }

}
