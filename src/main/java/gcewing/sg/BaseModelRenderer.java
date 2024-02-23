// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Render block using model + textures
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.interfaces.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import gcewing.sg.interfaces.IBlock;
import gcewing.sg.interfaces.ICustomRenderer;
import gcewing.sg.interfaces.IModel;
import gcewing.sg.interfaces.IRenderTarget;
import gcewing.sg.interfaces.ITexture;
import org.joml.Vector3i;

public class BaseModelRenderer implements ICustomRenderer {

    public static boolean debugRenderModel = false;

    protected IModel model;
    protected ITexture[] textures;
    protected Vector3 origin;

    public BaseModelRenderer(IModel model, Vector3 origin, ITexture... textures) {
        this.model = model;
        this.textures = textures;
        this.origin = origin;
    }

    public void renderBlock(IBlockAccess world, Vector3i pos, IBlockState state, IRenderTarget target,
                            EnumWorldBlockLayer layer, Trans3 t) {
        IBlock block = (IBlock) state.getBlock();
        Trans3 t2 = t.t(block.localToGlobalTransformation(world, pos, state, Vector3.zero)).translate(origin);
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
