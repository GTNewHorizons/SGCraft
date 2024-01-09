// ------------------------------------------------------------------------------------------------
//
// SG Craft - Interface for stargate ring and base blocks
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.world.IBlockAccess;

public interface ISGBlock {

    public SGBaseTE getBaseTE(IBlockAccess world, BlockPos pos);

    public boolean isMerged(IBlockAccess world, BlockPos pos);

}
