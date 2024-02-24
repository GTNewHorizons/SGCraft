// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - 1.8 compatibility
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.tileentities;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class InventoryHelper {

    private static final Random RANDOM = new Random();

    public static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory p_180175_2_) {
        func_180174_a(worldIn, x, y, z, p_180175_2_);
    }

    private static void func_180174_a(World worldIn, double x, double y, double z, IInventory p_180174_7_) {
        for (int i = 0; i < p_180174_7_.getSizeInventory(); ++i) {
            ItemStack itemstack = p_180174_7_.getStackInSlot(i);

            if (itemstack != null) {
                spawnItemStack(worldIn, x, y, z, itemstack);
            }
        }
    }

    private static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
        float f = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (stack.stackSize > 0) {
            int i = RANDOM.nextInt(21) + 10;

            if (i > stack.stackSize) {
                i = stack.stackSize;
            }

            stack.stackSize -= i;
            Item item = stack.getItem();
            int damage = stack.getItemDamage();
            EntityItem entityitem = new EntityItem(worldIn, x + f, y + f1, z + f2, new ItemStack(item, i, damage));

            if (stack.hasTagCompound()) {
                entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
            }

            float f3 = 0.05F;
            entityitem.motionX = RANDOM.nextGaussian() * (double) f3;
            entityitem.motionY = RANDOM.nextGaussian() * (double) f3 + 0.20000000298023224D;
            entityitem.motionZ = RANDOM.nextGaussian() * (double) f3;
            worldIn.spawnEntityInWorld(entityitem);
        }
    }
}
