// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate ring tile entity
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static gcewing.sg.utils.BaseBlockUtils.getWorldTileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.joml.Vector3i;

public class SGRingTE extends BaseTileEntity {

    public boolean isMerged;
    public Vector3i basePos = new Vector3i(0, 0, 0);

    @Override
    public void readContentsFromNBT(NBTTagCompound nbt) {
        super.readContentsFromNBT(nbt);
        isMerged = nbt.getBoolean("isMerged");
        int baseX = nbt.getInteger("baseX");
        int baseY = nbt.getInteger("baseY");
        int baseZ = nbt.getInteger("baseZ");
        basePos = new Vector3i(baseX, baseY, baseZ);
    }

    @Override
    public void writeContentsToNBT(NBTTagCompound nbt) {
        super.writeContentsToNBT(nbt);
        nbt.setBoolean("isMerged", isMerged);
        nbt.setInteger("baseX", basePos.x);
        nbt.setInteger("baseY", basePos.y);
        nbt.setInteger("baseZ", basePos.z);
    }

    public SGBaseTE getBaseTE() {
        if (isMerged) {
            TileEntity bte = getWorldTileEntity(worldObj, basePos);
            if (bte instanceof SGBaseTE) return (SGBaseTE) bte;
        }
        return null;
    }

}
