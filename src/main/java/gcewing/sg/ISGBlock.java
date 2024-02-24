// ------------------------------------------------------------------------------------------------
//
// SG Craft - Interface for stargate ring and base blocks
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.tileentities.SGBaseTE;
import net.minecraft.world.IBlockAccess;
import org.joml.Vector3i;

public interface ISGBlock {

    SGBaseTE getBaseTE(IBlockAccess world, Vector3i pos);

    boolean isMerged(IBlockAccess world, Vector3i pos);

}
