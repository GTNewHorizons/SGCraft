//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import static gcewing.sg.BaseBlockUtils.getWorldBlock;
import static gcewing.sg.BaseBlockUtils.getWorldBlockState;
import static gcewing.sg.BaseBlockUtils.markWorldBlockForUpdate;

import java.util.List;

import gcewing.sg.BaseMod.ModelSpec;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
// import net.minecraft.block.properties.*;
// import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SGRingBlock extends SGBlock<SGRingTE> {

    static final int numSubBlocks = 2;
    
    public static IProperty<Integer> VARIANT = PropertyInteger.create("variant", 0, 1);

    static String[] textures = {"stargateBlock", "stargateRing", "stargateChevron"};
    static ModelSpec models[] = {
        new ModelSpec("block/sg_ring_block.smeg", "stargateBlock", "stargateRing"),
        new ModelSpec("block/sg_ring_block.smeg", "stargateBlock", "stargateChevron")
    };
    
    static String[] subBlockTitles = {
        "Stargate Ring Block",
        "Stargate Chevron Block",
    };

    public SGRingBlock() {
        super(Material.rock, SGRingTE.class);
        setHardness(1.5F);
        setCreativeTab(CreativeTabs.tabMisc);
//      registerSubItemNames();
    }
    
    protected void defineProperties() {
        super.defineProperties();
        addProperty(VARIANT);
    }
    
    @Override
    public int getNumSubtypes() {
        return VARIANT.getAllowedValues().size();
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
    public boolean shouldCheckWeakPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }
    
    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
        EnumFacing side, float cx, float cy, float cz)
    {
        //System.out.printf("SGRingBlock.onBlockActivated at (%d, %d, %d)\n", x, y, z);
        SGRingTE te = getRingTE(world, pos);
        if (te.isMerged) {
            //System.out.printf("SGRingBlock.onBlockActivated: base at %s\n", te.basePos);
            IBlockState baseState = getWorldBlockState(world, te.basePos);
            Block block = baseState.getBlock();
            if (block instanceof SGBaseBlock)
                ((SGBaseBlock)block).onBlockActivated(world, te.basePos, baseState, player, side, cx, cy, cz);
            return true;
        }
        return false;
    }
    
    @Override
    public SGBaseTE getBaseTE(IBlockAccess world, BlockPos pos) {
        SGRingTE rte = getRingTE(world, pos);
        if (rte != null)
            return rte.getBaseTE();
        else
            return null;
    }
    
    public SGRingTE getRingTE(IBlockAccess world, BlockPos pos) {		
		TileEntity te = getTileEntity(world, pos); 
		if (SGRingTE.class.isInstance(te)) {
			return (SGRingTE) te;
		}
		return null;
	}
    
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < numSubBlocks; i++)
            list.add(new ItemStack(item, 1, i));
    }
    
    public boolean isMerged(IBlockAccess world, BlockPos pos) {
        SGRingTE te = getRingTE(world, pos);
        return te != null && te.isMerged;
    }
    
    public void mergeWith(World world, BlockPos pos, BlockPos basePos) {
        SGRingTE te = getRingTE(world, pos);
        te.isMerged = true;
        te.basePos = basePos;
        //te.onInventoryChanged();
        markWorldBlockForUpdate(world, pos);
    }
    
    public void unmergeFrom(World world, BlockPos pos, BlockPos basePos) {
        SGRingTE te = getRingTE(world, pos);
        if (SGBaseBlock.debugMerge)
            System.out.printf("SGRingBlock.unmergeFrom: ring at %s base at %s te.isMerged = %s te.basePos = %s\n",
                pos, basePos, te.isMerged, te.basePos);
        if (te.isMerged && te.basePos.equals(basePos)) {
            if (SGBaseBlock.debugMerge)
                System.out.printf("SGRingBlock.unmergeFrom: unmerging\n");
            te.isMerged = false;
            te.markBlockChanged();
        }
    }
    
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (SGBaseBlock.debugMerge)
            System.out.printf("SGRingBlock.onBlockAdded: at %s\n", pos);
        SGRingTE te = getRingTE(world, pos);
        updateBaseBlocks(world, pos, te);
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        SGRingTE te = getRingTE(world, pos);
        super.breakBlock(world, pos, state);
        if (te != null && te.isMerged)
            updateBaseBlocks(world, pos, te);
    }
    
    void updateBaseBlocks(World world, BlockPos pos, SGRingTE te) {
        if (SGBaseBlock.debugMerge)
            System.out.printf("SGRingBlock.updateBaseBlocks: merged = %s, base = %s\n",
                te.isMerged, te.basePos);
        for (int i = -2; i <= 2; i++)
            for (int j = -4; j <= 0; j++)
                for (int k = -2; k <= 2; k++) {
                    BlockPos bp = pos.add(i, j, k);
                    Block block = getWorldBlock(world, bp);
                    if (block instanceof SGBaseBlock) {
                         if (SGBaseBlock.debugMerge)
                            System.out.printf("SGRingBlock.updateBaseBlocks: found base at %s\n", bp);
                        SGBaseBlock base = (SGBaseBlock)block;
                        if (!te.isMerged)
                            base.checkForMerge(world, bp);
                        else if (te.basePos.equals(bp))
                            base.unmerge(world, bp);
                }
        }
    }
    
}
