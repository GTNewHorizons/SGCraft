// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate Computer Interface Block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import org.joml.Vector3i;

import gcewing.sg.blocks.base.BaseBlock;
import gcewing.sg.blocks.orientation.Orient4WaysByState;
import gcewing.sg.interfaces.IOrientationHandler;

public class SGInterfaceBlock<TE extends TileEntity> extends BaseBlock<TE> {

    protected static IOrientationHandler orient4WaysByState = new Orient4WaysByState();

    public SGInterfaceBlock(Material material, Class<TE> teClass) {
        super(material, orient4WaysByState, teClass);
    }

    @Override
    public IOrientationHandler getOrientationHandler() {
        return orient4WaysByState;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, Vector3i pos, EnumFacing side) {
        return true;
    }

}
