// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Block Utilities
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.utils.rendering;

import static gcewing.sg.utils.BaseBlockUtils.getMetaFromBlockState;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.joml.Vector3i;

import gcewing.sg.BaseMod;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.IRenderTarget;
import gcewing.sg.renderers.BaseWorldRenderTarget;

public class BaseRenderingUtils {

    public static void renderAlternateBlock(BaseMod mod, IBlockAccess world, Vector3i pos, IBlockState state,
            IRenderTarget target) {
        Block block = state.getBlock();
        int meta = getMetaFromBlockState(state);
        renderAlternateBlock(world, pos.x, pos.y, pos.z, block, meta, target);
    }

    public static void renderAlternateBlock(IBlockAccess world, int x, int y, int z, Block block, int meta,
            IRenderTarget target) {
        if (block.hasTileEntity(meta)) {
            return;
        }

        altBlockAccess.setup(world, x, y, z, meta);
        altRenderBlocks.renderBlockAllFaces(block, x, y, z);
        ((BaseWorldRenderTarget) target).setRenderingOccurred();
    }

    // ------------------------------------------------------------------------------------------------

    protected static AltBlockAccess altBlockAccess = new AltBlockAccess();
    protected static RenderBlocks altRenderBlocks = new RenderBlocks(altBlockAccess);

}
