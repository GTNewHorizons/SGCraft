//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate Power Unit Item
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class PowerItem extends BaseItemBlock {

    final String unitName;
    final double maxEnergy;

    public PowerItem(Block block, String unitName, double maxEnergy) {
        super(block);
        this.unitName = unitName;
        this.maxEnergy = maxEnergy;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            double eu = nbt.getDouble("energyBuffer");
            list.add(String.format("%.0f %s / %.0f", eu, unitName, maxEnergy));
        }
    }

}
