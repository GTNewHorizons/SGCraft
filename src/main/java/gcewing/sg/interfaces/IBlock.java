package gcewing.sg.interfaces;

import net.minecraft.world.IBlockAccess;

import org.joml.Vector3d;
import org.joml.Vector3i;

import gcewing.sg.utils.ModelSpec;
import gcewing.sg.utils.Trans3;

public interface IBlock extends ITextureConsumer {

    void setRenderType(int id);

    String getQualifiedRendererClassName();

    ModelSpec getModelSpec(IBlockState state);

    Trans3 localToGlobalTransformation(IBlockAccess world, Vector3i pos, IBlockState state, Vector3d origin);

    Class getDefaultItemClass();
}
