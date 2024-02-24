// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate Computer Interface Block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.tileentities.SGBaseTE;
import gcewing.sg.tileentities.SGRingTE;
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

    SGBaseTE getBaseTE(World world, int x, int y, int z) {
        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = world.getTileEntity(x + d.offsetX, y + d.offsetY, z + d.offsetZ);
            if (te instanceof SGRingTE) return ((SGRingTE) te).getBaseTE();
        }
        return null;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, Vector3i pos, EnumFacing side) {
        return true;
    }

}
