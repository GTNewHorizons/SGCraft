// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate Controller Block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import gcewing.sg.BaseOrientation;
import gcewing.sg.guis.DHDTE;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.SGCraft;
import gcewing.sg.guis.SGGui;
import gcewing.sg.blocks.base.BaseBlock;
import gcewing.sg.tileentities.SGBaseTE;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import gcewing.sg.utils.ModelSpec;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class DHDBlock extends BaseBlock<DHDTE> {

    protected static String[] textures = { "dhd_top", "dhd_side", "stargateBlock", "dhd_button_dim", };
    protected static ModelSpec model = new ModelSpec("dhd.smeg", new Vector3d(0, -0.5, 0), textures);

    public DHDBlock() {
        super(Material.rock /* SGRingBlock.ringMaterial */, DHDTE.class);
        setHardness(1.5F);
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public String[] getTextureNames() {
        return textures;
    }

    @Override
    public ModelSpec getModelSpec(IBlockState state) {
        return model;
    }

    @Override
    public IOrientationHandler getOrientationHandler() {
        return BaseOrientation.orient4WaysByState;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void onBlockAdded(World world, Vector3i pos, IBlockState state) {
        if (SGBaseBlock.debugMerge) SGCraft.log.debug(String.format("DHDBlock.onBlockAdded: at %s", pos));
        checkForLink(world, pos);
    }

    @Override
    public void onBlockPlacedBy(World world, Vector3i pos, IBlockState state, EntityLivingBase player,
            ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, player, stack);
        checkForLink(world, pos);
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public void breakBlock(World world, Vector3i pos, IBlockState state) {
        TileEntity cte = getTileEntity(world, pos);
        super.breakBlock(world, pos, state);
        if (cte == null) {
            SGCraft.log.debug(String.format("DHDBlock.breakBlock: No tile entity at %s", pos));
        } else if (cte instanceof DHDTE && ((DHDTE) cte).isLinkedToStargate) {
            SGBaseTE gte = ((DHDTE) cte).getLinkedStargateTE();
            if (gte != null) gte.clearLinkToController();
        }
    }

    @Override
    public boolean onBlockActivated(World world, Vector3i pos, IBlockState state, EntityPlayer player, EnumFacing side,
            float cx, float cy, float cz) {
        SGGui id = cy > 0.5 ? SGGui.SGController : SGGui.DHDFuel;
        SGCraft.mod.openGui(player, id, world, pos);
        return true;
    }

    public void checkForLink(World world, Vector3i pos) {
        TileEntity te = getTileEntity(world, pos);
        if (te instanceof DHDTE) {
            ((DHDTE) te).checkForLink();
        } else {
            SGCraft.log.debug(String.format("DHDBlock.breakBlock: No tile entity at %s", pos));
        }
    }

}
