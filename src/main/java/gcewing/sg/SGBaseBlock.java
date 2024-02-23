// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate base block
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static gcewing.sg.BaseBlockUtils.getWorldBlock;
import static gcewing.sg.BaseBlockUtils.getWorldBlockState;
import static gcewing.sg.BaseBlockUtils.markWorldBlockForUpdate;

import gcewing.sg.interfaces.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import gcewing.sg.utils.ModelSpec;
import org.joml.Vector3i;

public class SGBaseBlock extends SGBlock<SGBaseTE> {

    static boolean debugMerge = false;
    static int explosionRadius = 10;
    static boolean fieryExplosion = true;
    static boolean smokyExplosion = true;

    static int[][] pattern = { { 2, 1, 2, 1, 2 }, { 1, 0, 0, 0, 1 }, { 2, 0, 0, 0, 2 }, { 1, 0, 0, 0, 1 },
            { 2, 1, 0, 1, 2 }, };

    protected static String[] textures = { "stargateBlock", "stargateRing", "stargateBase_front" };
    protected static ModelSpec model = new ModelSpec("block/sg_base_block.smeg", textures);

    public static void configure(BaseConfiguration config) {
        explosionRadius = config.getInteger("stargate", "explosionRadius", explosionRadius);
        fieryExplosion = config.getBoolean("stargate", "explosionFlame", fieryExplosion);
        smokyExplosion = config.getBoolean("stargate", "explosionSmoke", smokyExplosion);
    }

    public SGBaseBlock() {
        super(Material.rock /* SGRingBlock.ringMaterial */, SGBaseTE.class);
        setHardness(1.5F);
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
        return true; // So that translucent camouflage blocks render correctly
    }

    @Override
    public IOrientationHandler getOrientationHandler() {
        return BaseOrientation.orient4WaysByState;
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
    public SGBaseTE getBaseTE(IBlockAccess world, Vector3i pos) {
        TileEntity te = getTileEntity(world, pos);
        if (te instanceof SGBaseTE) {
            return (SGBaseTE) te;
        }
        return null;
    }

    @Override
    protected String getRendererClassName() {
        return "SGRingBlockRenderer";
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, Vector3i pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
        return true;
    }

    public boolean isMerged(IBlockAccess world, Vector3i pos) {
        SGBaseTE te = getSGBaseTE(world, pos);
        return te != null && te.isMerged;
    }

    @Override
    public void onBlockAdded(World world, Vector3i pos, IBlockState state) {
        if (SGBaseBlock.debugMerge) SGCraft.log.debug(String.format("SGBaseBlock.onBlockAdded: at %s", pos));
        checkForMerge(world, pos);
    }

    @Override
    public boolean onBlockActivated(World world, Vector3i pos, IBlockState state, EntityPlayer player, EnumFacing side,
            float cx, float cy, float cz) {
        if (!world.isRemote) SGCraft.log.debug(
                String.format(
                        "SGBaseBlock: at %s meta %s state %s",
                        pos,
                        world.getBlockMetadata(pos.x, pos.y, pos.z),
                        state));
        String Side = world.isRemote ? "Client" : "Server";
        SGBaseTE te = getSGBaseTE(world, pos);
        if (te != null) {
            if (debugMerge)
                SGCraft.log.debug(String.format("SGBaseBlock.onBlockActivated: %s: isMerged = %s", Side, te.isMerged));
            if (te.isMerged) {
                SGCraft.mod.openGui(player, SGGui.SGBase, world, pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getWeakChanges(IBlockAccess world, Vector3i pos) {
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, Vector3i pos, IBlockState state, Block block) {
        SGBaseTE te = getSGBaseTE(world, pos);
        if (te != null) te.onNeighborBlockChange();
    }

    void checkForMerge(World world, Vector3i pos) {
        if (debugMerge) SGCraft.log.debug(String.format("SGBaseBlock.checkForMerge at %s", pos));
        if (isMerged(world, pos)) {
            return;
        }

        Trans3 t = localToGlobalTransformation(world, pos);
        for (int i = -2; i <= 2; i++) for (int j = 0; j <= 4; j++) if (!(i == 0 && j == 0)) {
            // BlockPos rp = pos.add(i * dx, j, i * dz);
            Vector3i rp = t.p(i, j, 0).blockPos();
            int type = getRingBlockType(world, rp);
            int pat = pattern[4 - j][2 + i];
            if (pat != 0 && type != pat) {
                if (debugMerge) SGCraft.log
                        .debug(String.format("SGBaseBlock: world %d != pattern %d at %s", type, pattern[j][2 + i], rp));
                return;
            }
        }
        if (debugMerge) SGCraft.log.debug("SGBaseBlock: Merging");
        SGBaseTE te = getSGBaseTE(world, pos);
        te.setMerged(true);
        markWorldBlockForUpdate(world, pos);
        for (int i = -2; i <= 2; i++) for (int j = 0; j <= 4; j++) if (!(i == 0 && j == 0)) {
            Vector3i rp = t.p(i, j, 0).blockPos();
            Block block = getWorldBlock(world, rp);
            if (block instanceof SGRingBlock) ((SGRingBlock) block).mergeWith(world, rp, pos);
        }
        te.checkForLink();
    }

    int getRingBlockType(World world, Vector3i pos) {
        Block block = getWorldBlock(world, pos);
        if (block == Blocks.air) return 0;
        if (block == SGCraft.sgRingBlock) {
            if (!SGCraft.sgRingBlock.isMerged(world, pos)) {
                IBlockState state = getWorldBlockState(world, pos);
                switch (state.getValue(SGRingBlock.VARIANT)) {
                    case 0:
                        return 1;
                    case 1:
                        return 2;
                }
            }
        }
        return -1;
    }

    @Override
    public void breakBlock(World world, Vector3i pos, IBlockState state) {
        unmerge(world, pos);
        dropUpgrades(world, pos);
        super.breakBlock(world, pos, state);
    }

    void dropUpgrades(World world, Vector3i pos) {
        SGBaseTE te = getSGBaseTE(world, pos);
        if (te != null) {
            if (te.hasChevronUpgrade) spawnAsEntity(world, pos, new ItemStack(SGCraft.sgChevronUpgrade));
            if (te.hasIrisUpgrade) spawnAsEntity(world, pos, new ItemStack(SGCraft.sgIrisUpgrade));
        }
    }

    public void unmerge(World world, Vector3i pos) {
        SGBaseTE te = getSGBaseTE(world, pos);
        boolean goBang = false;
        if (te != null) {
            if (te.isMerged && te.state == SGState.Connected) {
                te.state = SGState.Idle;
                goBang = true;
            }
            te.disconnect();
            te.unlinkFromController();
            te.setMerged(false);
            markWorldBlockForUpdate(world, pos);
            unmergeRing(world, pos);
        }
        if (goBang && explosionRadius > 0) explode(world, new Vector3(pos).add(0.5, 2.5, 0.5), explosionRadius);
    }

    void explode(World world, Vector3 p, double s) {
        world.newExplosion(null, p.x, p.y, p.z, (float) s, fieryExplosion, smokyExplosion);
    }

    void unmergeRing(World world, Vector3i pos) {
        for (int i = -2; i <= 2; i++)
            for (int j = 0; j <= 4; j++) for (int k = -2; k <= 2; k++) unmergeRingBlock(world, pos, pos.add(i, j, k));
    }

    void unmergeRingBlock(World world, Vector3i pos, Vector3i ringPos) {
        Block block = getWorldBlock(world, ringPos);
        if (debugMerge)
            SGCraft.log.debug(String.format("SGBaseBlock.unmergeRingBlock: found %s at %s", block, ringPos));
        if (block instanceof SGRingBlock) {
            ((SGRingBlock) block).unmergeFrom(world, ringPos, pos);
        }
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int getStrongPower(IBlockAccess world, Vector3i pos, IBlockState state, EnumFacing side) {
        return getWeakPower(world, pos, state, side);
    }

    @Override
    public int getWeakPower(IBlockAccess world, Vector3i pos, IBlockState state, EnumFacing side) {
        SGBaseTE te = getSGBaseTE(world, pos);
        return (te != null && te.state != SGState.Idle) ? 15 : 0;
    }

}
