//------------------------------------------------------------------------------------------------
//
//   SG Craft - Structure representing the location of a stargate
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
// import net.minecraft.util.BlockPos;
import net.minecraft.world.*;

import net.minecraftforge.common.*;

import static gcewing.sg.BaseUtils.*;
import static gcewing.sg.BaseBlockUtils.*;

public class SGLocation {

    public int dimension;
    public BlockPos pos;
    
    public SGLocation(TileEntity te) {
        this(getWorldDimensionId(getTileEntityWorld(te)), getTileEntityPos(te));
    }
    
    public SGLocation(int dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }
    
    public SGLocation(NBTTagCompound nbt) {
        dimension = nbt.getInteger("dimension");
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");
        pos = new BlockPos(x, y, z);
    }
    
    NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("dimension", dimension);
        nbt.setInteger("x", pos.getX());
        nbt.setInteger("y", pos.getY());
        nbt.setInteger("z", pos.getZ());
        return nbt;
    }
    
    SGBaseTE getStargateTE() {
        World world = /*DimensionManager.*/SGAddressing.getWorld(dimension);
        if (world == null) {
            System.out.printf(
                "SGCraft: SGLocation.getStargateTE: Oh, noes! Dimension %d is not loaded. How can this be?",
                dimension);
                return null;
        }
        TileEntity te = getWorldTileEntity(world, pos);
        if (te instanceof SGBaseTE)
            return (SGBaseTE)te;
        else
            return null;
    }

}
