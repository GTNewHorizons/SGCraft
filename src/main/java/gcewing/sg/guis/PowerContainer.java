// ------------------------------------------------------------------------------------------------
//
// SG Craft - Power unit gui container
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis;

import static gcewing.sg.utils.BaseBlockUtils.getWorldTileEntity;

import gcewing.sg.BaseContainer;
import gcewing.sg.tileentities.PowerTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.joml.Vector3i;

public class PowerContainer extends BaseContainer {

    PowerTE te;

    public static PowerContainer create(EntityPlayer player, World world, Vector3i pos) {
        TileEntity te = getWorldTileEntity(world, pos);
        if (te instanceof PowerTE) return new PowerContainer(player, (PowerTE) te);
        return null;
    }

    public PowerContainer(EntityPlayer player, PowerTE te) {
        super(PowerScreen.guiWidth, PowerScreen.guiHeight);
        this.te = te;
    }

}
