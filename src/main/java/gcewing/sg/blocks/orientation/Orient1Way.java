package gcewing.sg.blocks.orientation;

import gcewing.sg.blocks.base.BaseBlock;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.IOrientationHandler;
import gcewing.sg.utils.Trans3;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class Orient1Way implements IOrientationHandler {

    public void defineProperties(BaseBlock block) {}

    public IBlockState onBlockPlaced(Block block, World world, Vector3i pos, EnumFacing side, float hitX,
                                     float hitY, float hitZ, IBlockState baseState, EntityLivingBase placer) {
        return baseState;
    }

    public Trans3 localToGlobalTransformation(IBlockAccess world, Vector3i pos, IBlockState state,
                                              Vector3d origin) {
        return new Trans3(origin);
    }

}