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

import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.IRenderTarget;
import gcewing.sg.renderers.BaseWorldRenderTarget;

public class BaseRenderingUtils {

    private static final AltBlockAccess altBlockAccess = new AltBlockAccess();
    private static final RenderBlocks altRenderBlocks = new RenderBlocks(altBlockAccess);

    public static void renderAlternateBlock(IBlockAccess world, Vector3i pos, IBlockState state, IRenderTarget target) {
        final Block block = state.getBlock();
        final int meta = getMetaFromBlockState(state);
        renderAlternateBlock(world, pos.x, pos.y, pos.z, block, meta, target);
    }

    public static void renderAlternateBlock(IBlockAccess world, int x, int y, int z, Block block, int meta,
            IRenderTarget target) {
        if (block.hasTileEntity(meta)) {
            return;
        }
        try {
            altBlockAccess.setup(world, x, y, z, meta);
            altRenderBlocks.renderBlockAllFaces(block, x, y, z);
            ((BaseWorldRenderTarget) target).setRenderingOccurred();
        } finally {
            altBlockAccess.base = null;
        }
    }

}
