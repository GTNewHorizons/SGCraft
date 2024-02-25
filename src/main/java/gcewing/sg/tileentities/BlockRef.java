package gcewing.sg.tileentities;

import static gcewing.sg.utils.BaseBlockUtils.getTileEntityPos;
import static gcewing.sg.utils.BaseBlockUtils.getTileEntityWorld;
import static gcewing.sg.utils.BaseBlockUtils.getWorldTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import org.joml.Vector3i;

class BlockRef {

    public IBlockAccess worldObj;
    Vector3i pos;

    public BlockRef(TileEntity te) {
        this(getTileEntityWorld(te), getTileEntityPos(te));
    }

    public BlockRef(IBlockAccess world, Vector3i pos) {
        worldObj = world;
        this.pos = pos;
    }

    public TileEntity getTileEntity() {
        return getWorldTileEntity(worldObj, pos);
    }

}
