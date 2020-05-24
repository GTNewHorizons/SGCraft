//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.block.*;
// import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.*;

import gcewing.sg.BaseModClient.*;
import static gcewing.sg.BaseBlockUtils.*;

public class SGRingBlockRenderer implements ICustomRenderer {

    public SGRingBlockRenderer() {
//         System.out.printf("SGRingBlockRenderer: Creating\n");
    }

    public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, IRenderTarget target,
        EnumWorldBlockLayer layer, Trans3 t)
    {
        ISGBlock ringBlock = (ISGBlock)state.getBlock();
        if (target.isRenderingBreakEffects() || (layer == EnumWorldBlockLayer.SOLID && !ringBlock.isMerged(world, pos)))
            SGCraft.mod.client.renderBlockUsingModelSpec(world, pos, state, target, layer, t);
        else {
            SGBaseTE te = ringBlock.getBaseTE(world, pos);
            if (te != null) {
                ItemStack stack = te.getCamouflageStack(pos);
                if (stack != null) {
                    Item item = stack.getItem();
                    if (item instanceof ItemBlock) {
                        IBlockState camoState = BaseBlockUtils.getBlockStateFromItemStack(stack);
                        if (blockCanRenderInLayer(camoState.getBlock(), layer)) {
                            //System.out.printf("SGRingBlockRenderer: Rendering camouflage block %s at %s in layer %s\n",
                            //    camoState, pos, layer);
                            BaseRenderingUtils.renderAlternateBlock(SGCraft.mod, world, pos, camoState, target);
                        }
                    }
                }
            }
        }
    }
    
    public void renderItemStack(ItemStack stack, IRenderTarget target, Trans3 t) {
        if (BaseModClient.debugRenderItem)
            System.out.printf("SGRingBlockRenderer.renderItemStack: %s\n", stack);
        SGCraft.mod.client.renderItemStackUsingModelSpec(stack, target, t);
    }

}
