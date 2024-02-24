// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Block orientation handlers
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static gcewing.sg.utils.BaseUtils.horizontalFacings;
import static gcewing.sg.utils.BaseUtils.iround;

import gcewing.sg.blocks.orientation.Orient1Way;
import gcewing.sg.blocks.orientation.Orient4WaysByState;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.joml.Vector3d;
import org.joml.Vector3i;

import gcewing.sg.blocks.base.BaseBlock;
import gcewing.sg.interfaces.IOrientationHandler;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.IProperty;
import gcewing.sg.tileentities.BaseTileEntity;
import gcewing.sg.utils.PropertyTurn;
import gcewing.sg.utils.Trans3;

public class BaseOrientation {

    public static boolean debugPlacement = false;
    public static boolean debugOrientation = false;

    public static IOrientationHandler orient4WaysByState = new Orient4WaysByState();
}
