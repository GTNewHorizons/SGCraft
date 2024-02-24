// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import gcewing.sg.interfaces.ISGBlock;
import gcewing.sg.blocks.base.BaseBlock;
import gcewing.sg.tileentities.SGBaseTE;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.joml.Vector3i;

public abstract class SGBlock<TE extends TileEntity> extends BaseBlock<TE> implements ISGBlock {

    public SGBlock(Material material, Class<TE> teClass) {
        super(material, teClass);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        if (player.capabilities.isCreativeMode && isConnected(world, new Vector3i(x, y, z))) {
            if (world.isRemote) SGBaseTE.sendChatMessage(player, "Disconnect stargate before breaking");
            return false;
        }
        return super.removedByPlayer(world, player, x, y, z);
    }

    boolean isConnected(World world, Vector3i pos) {
        SGBaseTE bte = getBaseTE(world, pos);
        return bte != null && bte.isConnected();
    }

}
