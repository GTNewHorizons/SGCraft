package gcewing.sg.interfaces;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.joml.Vector3d;
import org.joml.Vector3i;

import gcewing.sg.blocks.base.BaseBlock;
import gcewing.sg.utils.Trans3;

public interface IOrientationHandler {

    void defineProperties(BaseBlock block);

    IBlockState onBlockPlaced(Block block, World world, Vector3i pos, EnumFacing side, float hitX, float hitY,
            float hitZ, IBlockState baseState, EntityLivingBase placer);

    Trans3 localToGlobalTransformation(IBlockAccess world, Vector3i pos, IBlockState state, Vector3d origin);
}
