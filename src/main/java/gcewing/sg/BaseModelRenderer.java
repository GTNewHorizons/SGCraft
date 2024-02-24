// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Render block using model + textures
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

// import net.minecraft.block.state.IBlockState;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import gcewing.sg.BaseMod.IBlock;
import gcewing.sg.BaseModClient.ICustomRenderer;
import gcewing.sg.BaseModClient.IModel;
import gcewing.sg.BaseModClient.IRenderTarget;
import gcewing.sg.BaseModClient.ITexture;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class BaseModelRenderer implements ICustomRenderer {

    public static boolean debugRenderModel = false;

    protected IModel model;
    protected ITexture[] textures;
    protected Vector3d origin;

    // private static Trans3 itemTrans = Trans3.blockCenterSideTurn(0, 0);

    public BaseModelRenderer(IModel model, ITexture... textures) {
        this(model, new Vector3d(), textures);
    }

    public BaseModelRenderer(IModel model, Vector3d origin, ITexture... textures) {
        this.model = model;
        this.textures = textures;
        this.origin = origin;
    }

    public void renderBlock(IBlockAccess world, Vector3i pos, IBlockState state, IRenderTarget target,
                            EnumWorldBlockLayer layer, Trans3 t) {
        IBlock block = (IBlock) state.getBlock();
        Trans3 t2 = t.t(block.localToGlobalTransformation(world, pos, state, new Vector3d())).translate(origin);
        model.render(t2, target, textures);
    }

    public void renderItemStack(ItemStack stack, IRenderTarget target, Trans3 t) {
        if (debugRenderModel) {
            SGCraft.log.debug(String.format("BaseModelRenderer.renderItemStack: %s", stack));
            SGCraft.log.debug(String.format("BaseModelRenderer.renderItemStack: model = %s", model));
            for (int i = 0; i < textures.length; i++) SGCraft.log
                    .debug(String.format("BaseModelRenderer.renderItemStack: textures[%s] = %s", i, textures[i]));
        }
        model.render(t.translate(origin), target, textures);
    }

}
