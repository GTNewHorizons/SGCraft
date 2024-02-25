// ------------------------------------------------------------------------------------------------
//
// SG Craft - Interface for stargate ring and base blocks
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.interfaces;

import net.minecraft.world.IBlockAccess;

import org.joml.Vector3i;

import gcewing.sg.tileentities.SGBaseTE;

public interface ISGBlock {

    SGBaseTE getBaseTE(IBlockAccess world, Vector3i pos);

    boolean isMerged(IBlockAccess world, Vector3i pos);

}
