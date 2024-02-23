// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate Computer Interface Block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.joml.Vector3i;

public class SGInterfaceBlock<TE extends TileEntity> extends BaseBlock<TE> {

    public SGInterfaceBlock(Material material, Class<TE> teClass) {
        super(material, BaseOrientation.orient4WaysByState, teClass);
    }

    @Override
    public IOrientationHandler getOrientationHandler() {
        return BaseOrientation.orient4WaysByState;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, Vector3i pos, EnumFacing side) {
        return true;
    }

}
