// ------------------------------------------------------------------------------------------------
//
// SG Craft - Iris upgrade item
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static gcewing.sg.utils.BaseBlockUtils.getWorldBlock;

import gcewing.sg.tileentities.SGBaseTE;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.joml.Vector3i;

public class SGIrisUpgradeItem extends BaseItem {

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, Vector3i pos, EnumFacing side,
                             float hitX, float hitY, float hitZ) {
        Block block = getWorldBlock(world, pos);
        if (block instanceof ISGBlock) {
            SGBaseTE te = ((ISGBlock) block).getBaseTE(world, pos);
            if (te != null) return te.applyIrisUpgrade(stack, player);
        }
        return false;
    }

}
