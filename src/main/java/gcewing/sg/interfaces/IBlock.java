package gcewing.sg.interfaces;

import gcewing.sg.utils.Trans3;
import gcewing.sg.utils.Vector3;
import gcewing.sg.utils.ModelSpec;
import net.minecraft.world.IBlockAccess;
import org.joml.Vector3i;

public interface IBlock extends ITextureConsumer{
    void setRenderType(int id);

    String getQualifiedRendererClassName();

    ModelSpec getModelSpec(IBlockState state);

    int getNumSubtypes();

    Trans3 localToGlobalTransformation(IBlockAccess world, Vector3i pos, IBlockState state, Vector3 origin);

    Class getDefaultItemClass();
}
