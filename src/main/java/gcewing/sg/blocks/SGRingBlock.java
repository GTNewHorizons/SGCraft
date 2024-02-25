// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate ring block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import static gcewing.sg.utils.BaseBlockUtils.getWorldBlock;
import static gcewing.sg.utils.BaseBlockUtils.getWorldBlockState;
import static gcewing.sg.utils.BaseBlockUtils.markWorldBlockForUpdate;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.joml.Vector3i;

import gcewing.sg.SGCraft;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.IProperty;
import gcewing.sg.tileentities.SGBaseTE;
import gcewing.sg.tileentities.SGRingTE;
import gcewing.sg.utils.EnumWorldBlockLayer;
import gcewing.sg.utils.ModelSpec;
import gcewing.sg.utils.PropertyInteger;

public class SGRingBlock extends SGBlock<SGRingTE> {

    static final int numSubBlocks = 2;

    public static IProperty<Integer> VARIANT = PropertyInteger.create("variant", 0, 1);

    static String[] textures = { "stargateBlock", "stargateRing", "stargateChevron" };
    static ModelSpec[] models = { new ModelSpec("block/sg_ring_block.smeg", "stargateBlock", "stargateRing"),
            new ModelSpec("block/sg_ring_block.smeg", "stargateBlock", "stargateChevron") };

    static String[] subBlockTitles = { "Stargate Ring Block", "Stargate Chevron Block", };

    public SGRingBlock() {
        super(Material.rock, SGRingTE.class);
        setHardness(1.5F);
        setCreativeTab(CreativeTabs.tabMisc);
    }

    protected void defineProperties() {
        super.defineProperties();
        addProperty(VARIANT);
    }

    @Override
    public String[] getTextureNames() {
        return textures;
    }

    @Override
    public ModelSpec getModelSpec(IBlockState state) {
        return models[state.getValue(VARIANT)];
    }

    @Override
    protected String getRendererClassName() {
        return "SGRingBlockRenderer";
    }

    @Override
    public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
        return true; // So that translucent camouflage blocks render correctly
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, Vector3i pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, Vector3i pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean onBlockActivated(World world, Vector3i pos, IBlockState state, EntityPlayer player, EnumFacing side,
            float cx, float cy, float cz) {
        SGRingTE te = getRingTE(world, pos);
        if (te.isMerged) {
            IBlockState baseState = getWorldBlockState(world, te.basePos);
            Block block = baseState.getBlock();
            if (block instanceof SGBaseBlock)
                ((SGBaseBlock) block).onBlockActivated(world, te.basePos, baseState, player, side, cx, cy, cz);
            return true;
        }
        return false;
    }

    @Override
    public SGBaseTE getBaseTE(IBlockAccess world, Vector3i pos) {
        SGRingTE rte = getRingTE(world, pos);
        if (rte != null) return rte.getBaseTE();
        return null;
    }

    public SGRingTE getRingTE(IBlockAccess world, Vector3i pos) {
        TileEntity te = getTileEntity(world, pos);
        if (te instanceof SGRingTE) {
            return (SGRingTE) te;
        }
        return null;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < numSubBlocks; i++) list.add(new ItemStack(item, 1, i));
    }

    public boolean isMerged(IBlockAccess world, Vector3i pos) {
        SGRingTE te = getRingTE(world, pos);
        return te != null && te.isMerged;
    }

    public void mergeWith(World world, Vector3i pos, Vector3i basePos) {
        SGRingTE te = getRingTE(world, pos);
        te.isMerged = true;
        te.basePos = basePos;
        // te.onInventoryChanged();
        markWorldBlockForUpdate(world, pos);
    }

    public void unmergeFrom(World world, Vector3i pos, Vector3i basePos) {
        SGRingTE te = getRingTE(world, pos);
        if (SGBaseBlock.debugMerge) SGCraft.log.debug(
                String.format(
                        "SGRingBlock.unmergeFrom: ring at %s base at %s te.isMerged = %s te.basePos = %s",
                        pos,
                        basePos,
                        te.isMerged,
                        te.basePos));
        if (te.isMerged && te.basePos.equals(basePos)) {
            if (SGBaseBlock.debugMerge) SGCraft.log.debug("SGRingBlock.unmergeFrom: unmerging");
            te.isMerged = false;
            te.markBlockChanged();
        }
    }

    @Override
    public void onBlockAdded(World world, Vector3i pos, IBlockState state) {
        if (SGBaseBlock.debugMerge) SGCraft.log.debug(String.format("SGRingBlock.onBlockAdded: at %s", pos));
        SGRingTE te = getRingTE(world, pos);
        updateBaseBlocks(world, pos, te);
    }

    @Override
    public void breakBlock(World world, Vector3i pos, IBlockState state) {
        SGRingTE te = getRingTE(world, pos);
        super.breakBlock(world, pos, state);
        if (te != null && te.isMerged) updateBaseBlocks(world, pos, te);
    }

    void updateBaseBlocks(World world, Vector3i pos, SGRingTE te) {
        if (SGBaseBlock.debugMerge) SGCraft.log
                .debug(String.format("SGRingBlock.updateBaseBlocks: merged = %s, base = %s", te.isMerged, te.basePos));
        for (int i = -2; i <= 2; i++) for (int j = -4; j <= 0; j++) for (int k = -2; k <= 2; k++) {
            Vector3i blockPos = pos.add(i, j, k);
            Block block = getWorldBlock(world, blockPos);
            if (block instanceof SGBaseBlock) {
                if (SGBaseBlock.debugMerge)
                    SGCraft.log.debug(String.format("SGRingBlock.updateBaseBlocks: found base at %s", blockPos));
                SGBaseBlock base = (SGBaseBlock) block;
                if (!te.isMerged) base.checkForMerge(world, blockPos);
                else if (te.basePos.equals(blockPos)) base.unmerge(world, blockPos);
            }
        }
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return true;
    }
}
