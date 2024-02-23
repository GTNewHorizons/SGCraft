// ------------------------------------------------------------------------------------------------
//
// SG Craft - Structure representing the location of a stargate
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static gcewing.sg.utils.BaseBlockUtils.getTileEntityPos;
import static gcewing.sg.utils.BaseBlockUtils.getTileEntityWorld;
import static gcewing.sg.utils.BaseBlockUtils.getWorldTileEntity;
import static gcewing.sg.utils.BaseUtils.getWorldDimensionId;

import gcewing.sg.tileentities.SGBaseTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.joml.Vector3i;

public class SGLocation {

    public int dimension;
    public Vector3i pos;

    public SGLocation(TileEntity te) {
        this(getWorldDimensionId(getTileEntityWorld(te)), getTileEntityPos(te));
    }

    public SGLocation(int dimension, Vector3i pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    public SGLocation(NBTTagCompound nbt) {
        dimension = nbt.getInteger("dimension");
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");
        pos = new Vector3i(x, y, z);
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("dimension", dimension);
        nbt.setInteger("x", pos.x);
        nbt.setInteger("y", pos.y);
        nbt.setInteger("z", pos.z);
        return nbt;
    }

    public SGBaseTE getStargateTE() {
        World world = /* DimensionManager. */SGAddressing.getWorld(dimension);
        if (world == null) {
            SGCraft.log.warn(
                    String.format(
                            "SGLocation.getStargateTE: Oh, noes! Dimension %d is not loaded. How can this be?",
                            dimension));
            return null;
        }
        TileEntity te = getWorldTileEntity(world, pos);
        if (te instanceof SGBaseTE) return (SGBaseTE) te;
        return null;
    }

}
