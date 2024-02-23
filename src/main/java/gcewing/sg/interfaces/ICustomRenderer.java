package gcewing.sg.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import org.joml.Vector3i;

import gcewing.sg.utils.Trans3;
import gcewing.sg.worldgen.EnumWorldBlockLayer;

public interface ICustomRenderer {

    void renderBlock(IBlockAccess world, Vector3i pos, IBlockState state, IRenderTarget target,
            EnumWorldBlockLayer layer, Trans3 t);

    void renderItemStack(ItemStack stack, IRenderTarget target, Trans3 t);
}
