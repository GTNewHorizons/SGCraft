// ------------------------------------------------------------------------------------------------
//
// SG Craft - Chevron upgrade item
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.items;

import gcewing.sg.SGCraft;
import gcewing.sg.interfaces.ISGBlock;
import gcewing.sg.tileentities.SGBaseTE;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.joml.Vector3i;

import static gcewing.sg.utils.BaseBlockUtils.getWorldBlock;

public class SGChevronUpgradeItem extends BaseItem {

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, Vector3i pos, EnumFacing side,
                             float hitX, float hitY, float hitZ) {
        SGCraft.log.trace(String.format("SGChevronUpgradeItem.onItemUse: at %s", pos));
        Block block = getWorldBlock(world, pos);
        if (block instanceof ISGBlock) {
            SGBaseTE te = ((ISGBlock) block).getBaseTE(world, pos);
            if (te != null) return te.applyChevronUpgrade(stack, player);
        }
        return false;
    }

}
