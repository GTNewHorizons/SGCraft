// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate ring block renderer
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.renderers;

import static gcewing.sg.utils.BaseBlockUtils.blockCanRenderInLayer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import org.joml.Vector3i;

import gcewing.sg.BaseModClient;
import gcewing.sg.SGCraft;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.ICustomRenderer;
import gcewing.sg.interfaces.IRenderTarget;
import gcewing.sg.interfaces.ISGBlock;
import gcewing.sg.tileentities.SGBaseTE;
import gcewing.sg.utils.BaseBlockUtils;
import gcewing.sg.utils.BaseRenderingUtils;
import gcewing.sg.utils.EnumWorldBlockLayer;
import gcewing.sg.utils.Trans3;

public class SGRingBlockRenderer implements ICustomRenderer {

    public SGRingBlockRenderer() {}

    public void renderBlock(IBlockAccess world, Vector3i pos, IBlockState state, IRenderTarget target,
            EnumWorldBlockLayer layer, Trans3 t) {
        ISGBlock ringBlock = (ISGBlock) state.getBlock();
        if (target.isRenderingBreakEffects()
                || (layer == EnumWorldBlockLayer.SOLID && !ringBlock.isMerged(world, pos))) {
            SGCraft.mod.client.renderBlockUsingModelSpec(world, pos, state, target, layer, t);
            return;
        }

        SGBaseTE te = ringBlock.getBaseTE(world, pos);
        if (te == null) {
            return;
        }

        ItemStack stack = te.getCamouflageStack(pos);
        if (stack == null) {
            return;
        }

        Item item = stack.getItem();
        if (!(item instanceof ItemBlock)) {
            return;
        }

        IBlockState camoState = BaseBlockUtils.getBlockStateFromItemStack(stack);
        if (blockCanRenderInLayer(camoState.getBlock(), layer)) {
            BaseRenderingUtils.renderAlternateBlock(SGCraft.mod, world, pos, camoState, target);
        }
    }

    public void renderItemStack(ItemStack stack, IRenderTarget target, Trans3 t) {
        if (BaseModClient.debugRenderItem)
            SGCraft.log.debug(String.format("SGRingBlockRenderer.renderItemStack: %s", stack));
        SGCraft.mod.client.renderItemStackUsingModelSpec(stack, target, t);
    }

}
