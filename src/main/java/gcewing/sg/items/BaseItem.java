// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Generic Item
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.items;

import static gcewing.sg.utils.BaseUtils.facings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import gcewing.sg.BaseMod.IItem;
import gcewing.sg.BaseMod.ModelSpec;
import org.joml.Vector3i;

public class BaseItem extends Item implements IItem {

    public String[] getTextureNames() {
        return null;
    }

    public ModelSpec getModelSpec(ItemStack stack) {
        return null;
    }

    public int getNumSubtypes() {
        return 1;
    }

    @Override
    public boolean getHasSubtypes() {
        return getNumSubtypes() > 1;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        return onItemUse(stack, player, world, new Vector3i(x, y, z), facings[side], hitX, hitY, hitZ);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, Vector3i pos, EnumFacing side,
            float hitX, float hitY, float hitZ) {
        return false;
    }

}
