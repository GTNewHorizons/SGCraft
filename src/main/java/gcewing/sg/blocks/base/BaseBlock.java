// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Generic Block with optional Tile Entity
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.blocks.base;

import static gcewing.sg.utils.BaseBlockUtils.getMetaFromBlockState;
import static gcewing.sg.utils.BaseBlockUtils.getWorldBlockState;
import static gcewing.sg.utils.BaseBlockUtils.getWorldTileEntity;
import static gcewing.sg.utils.BaseUtils.facings;
import static gcewing.sg.utils.BaseUtils.newMovingObjectPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import gcewing.sg.interfaces.ICustomRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.joml.Vector3d;
import org.joml.Vector3i;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gcewing.sg.BaseMod;
import gcewing.sg.BaseModClient;
import gcewing.sg.SGCraft;
import gcewing.sg.blocks.orientation.Orient1Way;
import gcewing.sg.interfaces.IBlock;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.IModel;
import gcewing.sg.interfaces.IOrientationHandler;
import gcewing.sg.interfaces.IProperty;
import gcewing.sg.interfaces.ITileEntity;
import gcewing.sg.tileentities.InventoryHelper;
import gcewing.sg.tileentities.SGBaseTE;
import gcewing.sg.utils.BaseUtils;
import gcewing.sg.utils.EnumWorldBlockLayer;
import gcewing.sg.utils.ModelSpec;
import gcewing.sg.utils.Trans3;
import gcewing.sg.utils.blockstates.BlockState;

public class BaseBlock<TE extends TileEntity> extends BlockContainer implements IBlock {

    protected ICustomRenderer RENDERER_INSTANCE;

    public static boolean debugState = false;

    protected static Random RANDOM = new Random();

    public Class getDefaultItemClass() {
        return BaseItemBlock.class;
    }

    // --------------------------- Orientation -------------------------------

    public static IOrientationHandler orient1Way = new Orient1Way();

    // --------------------------- Members -------------------------------

    protected MapColor mapColor;
    protected final BlockState blockState;
    protected IBlockState defaultBlockState;
    protected IProperty[] properties;
    protected Object[][] propertyValues;
    protected int numProperties; // Do not explicitly initialise
    protected int renderID;
    protected Class<? extends TileEntity> tileEntityClass = null;
    protected IOrientationHandler orientationHandler = orient1Way;
    protected String[] textureNames;
    protected ModelSpec modelSpec;
    public BaseMod mod;
    protected AxisAlignedBB boxHit;

    // --------------------------- Constructors -------------------------------

    public BaseBlock(Material material) {
        this(material, null, null, null);
    }

    public BaseBlock(Material material, IOrientationHandler orient) {
        this(material, orient, null, null);
    }

    public BaseBlock(Material material, Class<TE> teClass) {
        this(material, null, teClass, null);
    }

    public BaseBlock(Material material, IOrientationHandler orient, Class<TE> teClass) {
        this(material, orient, teClass, null);
    }

    public BaseBlock(Material material, Class<TE> teClass, String teID) {
        this(material, null, teClass, teID);
    }

    public BaseBlock(Material material, IOrientationHandler orient, Class<TE> teClass, String teID) {
        super(material);
        if (orient == null) orient = orient1Way;
        this.orientationHandler = orient;
        tileEntityClass = teClass;
        if (teClass != null) {
            if (teID == null) teID = teClass.getName();
            try {
                GameRegistry.registerTileEntity(teClass, teID);
            } catch (IllegalArgumentException e) {
                // Ignore redundant registration
            }
        }
        blockState = createBlockState();
        defaultBlockState = blockState.getBaseState();
        opaque = true;
    }

    // --------------------------- Accessors ----------------------------

    @Override
    public boolean isOpaqueCube() {
        return opaque;
    }

    // --------------------------- States -------------------------------

    public IOrientationHandler getOrientationHandler() {
        return orientationHandler;
    }

    protected void defineProperties() {
        properties = new IProperty[4];
        propertyValues = new Object[4][];
        getOrientationHandler().defineProperties(this);
    }

    public void addProperty(IProperty property) {
        if (debugState)
            SGCraft.log.debug(String.format("BaseBlock.addProperty: %s to %s", property, getClass().getName()));
        if (numProperties < 4) {
            int i = numProperties++;
            properties[i] = property;
            Object[] values = BaseUtils.arrayOf(property.getAllowedValues());
            propertyValues[i] = values;
        } else throw new IllegalStateException("Block " + getClass().getName() + " has too many properties");
        if (debugState) SGCraft.log.debug(
                String.format("BaseBlock.addProperty: %s now has %s properties", getClass().getName(), numProperties));
    }

    protected BlockState createBlockState() {
        if (debugState) SGCraft.log.debug("BaseBlock.createBlockState: Defining properties");
        defineProperties();
        if (debugState) dumpProperties();
        checkProperties();
        IProperty[] props = Arrays.copyOf(properties, numProperties);
        if (debugState) SGCraft.log.debug(
                String.format("BaseBlock.createBlockState: Creating BlockState with %s properties", props.length));
        return new BlockState(this, props);
    }

    private void dumpProperties() {
        SGCraft.log.debug(String.format("BaseBlock: Properties of %s:", getClass().getName()));
        for (int i = 0; i < numProperties; i++) {
            SGCraft.log.debug(String.format("%s: %s", i, properties[i]));
            Object[] values = propertyValues[i];
            for (int j = 0; j < values.length; j++) SGCraft.log.debug(String.format("   %s: %s", j, values[j]));
        }
    }

    protected void checkProperties() {
        int n = 1;
        for (int i = 0; i < numProperties; i++) n *= propertyValues[i].length;
        if (n > 16) throw new IllegalStateException(
                String.format("Block %s has %s combinations of property values (16 allowed)", getClass().getName(), n));
    }

    public final IBlockState getDefaultState() {
        return this.defaultBlockState;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, Vector3i pos) {
        return state;
    }

    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        for (int i = numProperties - 1; i >= 0; i--) {
            Object value = state.getValue(properties[i]);
            Object[] values = propertyValues[i];
            int k = values.length - 1;
            while (k > 0 && !values[k].equals(value)) --k;
            if (debugState) SGCraft.log.debug(
                    String.format(
                            "BaseBlock.getMetaFromState: property %s value %s --> %s of %s",
                            i,
                            value,
                            k,
                            values.length));
            meta = meta * values.length + k;
        }
        if (debugState) SGCraft.log.debug(String.format("BaseBlock.getMetaFromState: %s --> %s", state, meta));
        return meta & 15; // To be on the safe side
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        int m = meta;
        for (int i = numProperties - 1; i >= 0; i--) {
            Object[] values = propertyValues[i];
            int n = values.length;
            int k = m % n;
            m /= n;
            state = state.withProperty(properties[i], (Comparable) values[k]);
        }
        if (debugState) SGCraft.log.debug(String.format("BaseBlock.getStateFromMeta: %s --> %s", meta, state));
        return state;
    }

    protected ThreadLocal<TileEntity> harvestingTileEntity = new ThreadLocal();

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        harvestingTileEntity.set(te);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        TileEntity te = harvestingTileEntity.get();
        harvestBlock(world, player, new Vector3i(x, y, z), getStateFromMeta(meta), te);
    }

    public void harvestBlock(World world, EntityPlayer player, Vector3i pos, IBlockState state, TileEntity te) {
        super.harvestBlock(world, player, pos.x, pos.y, pos.z, getMetaFromState(state));
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        IBlockState state = getStateFromMeta(meta);
        ArrayList<ItemStack> result = getDrops(world, new Vector3i(x, y, z), state, fortune);
        harvestingTileEntity.set(null);
        return result;
    }

    public ArrayList<ItemStack> getDrops(IBlockAccess world, Vector3i pos, IBlockState state, int fortune) {
        TileEntity te = getWorldTileEntity(world, pos);
        if (te == null) te = harvestingTileEntity.get();
        return getDropsFromTileEntity(world, pos, state, te, fortune);
    }

    protected ArrayList<ItemStack> getDropsFromTileEntity(IBlockAccess world, Vector3i pos, IBlockState state,
            TileEntity te, int fortune) {
        int meta = getMetaFromState(state);
        return super.getDrops((World) world, pos.x, pos.y, pos.z, meta, fortune);
    }

    public void setModelAndTextures(String modelName, String... textureNames) {
        this.textureNames = textureNames;
        this.modelSpec = new ModelSpec(modelName, textureNames);
    }

    @Override
    public String[] getTextureNames() {
        return textureNames;
    }

    @Override
    public ModelSpec getModelSpec(IBlockState state) {
        return modelSpec;
    }

    public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
        return getBlockLayer() == layer;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.SOLID;
    }

    @Override
    public int getRenderType() {
        return renderID;
    }

    @Override
    public void setRenderType(int id) {
        renderID = id;
    }

    protected String getRendererClassName() {
        return null;
    }

    @Override
    public ICustomRenderer getCustomRenderer(){
        return RENDERER_INSTANCE;
    }

    public Trans3 localToGlobalTransformation(IBlockAccess world, Vector3i pos) {
        return localToGlobalTransformation(world, pos, getWorldBlockState(world, pos));
    }

    public Trans3 localToGlobalTransformation(IBlockAccess world, Vector3i pos, IBlockState state) {
        return localToGlobalTransformation(world, pos, state, new Vector3d(0.5 + pos.x, 0.5 + pos.y, 0.5 + pos.z));
    }

    public Trans3 localToGlobalTransformation(IBlockAccess world, Vector3i pos, IBlockState state, Vector3d origin) {
        IOrientationHandler oh = getOrientationHandler();
        return oh.localToGlobalTransformation(world, pos, state, origin);
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return hasTileEntity(getStateFromMeta(meta));
    }

    public boolean hasTileEntity(IBlockState state) {
        return tileEntityClass != null;
    }

    public TileEntity getTileEntity(IBlockAccess world, Vector3i pos) {
        return world.getTileEntity(pos.x, pos.y, pos.z);
    }

    public SGBaseTE getSGBaseTE(IBlockAccess world, Vector3i pos) {
        TileEntity te = getTileEntity(world, pos);
        if (te instanceof SGBaseTE) {
            return (SGBaseTE) te;
        }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (tileEntityClass != null) {
            try {
                return tileEntityClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else return null;
    }

    public IBlockState onBlockPlaced(World world, Vector3i pos, EnumFacing side, float hitX, float hitY, float hitZ,
            int meta, EntityLivingBase placer) {
        return getOrientationHandler()
                .onBlockPlaced(this, world, pos, side, hitX, hitY, hitZ, getStateFromMeta(meta), placer);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        Vector3i pos = new Vector3i(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (hasTileEntity(meta)) {
            TileEntity te = getTileEntity(world, pos);
            if (te instanceof ITileEntity) ((ITileEntity) te).onAddedToWorld();
        }
        onBlockAdded(world, pos, getStateFromMeta(meta));
    }

    public void onBlockAdded(World world, Vector3i pos, IBlockState state) {}

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        Vector3i pos = new Vector3i(x, y, z);
        IBlockState state = getWorldBlockState(world, pos);
        onBlockPlacedBy(world, pos, state, entity, stack);
    }

    public void onBlockPlacedBy(World world, Vector3i pos, IBlockState state, EntityLivingBase entity,
            ItemStack stack) {}

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        Vector3i pos = new Vector3i(x, y, z);
        breakBlock(world, pos, getStateFromMeta(meta));
        if (hasTileEntity(meta)) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof IInventory) InventoryHelper.dropInventoryItems(world, x, y, z, (IInventory) te);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public void breakBlock(World world, Vector3i pos, IBlockState state) {}

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return canHarvestBlock(getStateFromMeta(meta), player);
    }

    public boolean canHarvestBlock(IBlockState state, EntityPlayer player) {
        return super.canHarvestBlock(player, getMetaFromState(state));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
            float hitY, float hitZ) {
        int meta = world.getBlockMetadata(x, y, z);
        IBlockState state = getStateFromMeta(meta);
        return onBlockActivated(world, new Vector3i(x, y, z), state, player, facings[side], hitX, hitY, hitZ);
    }

    public boolean onBlockActivated(World world, Vector3i pos, IBlockState state, EntityPlayer player, EnumFacing side,
            float cx, float cy, float cz) {
        TileEntity te = getTileEntity(world, pos);
        if (te != null) {
            int id = mod.getGuiId(te.getClass());
            if (id >= 0) {
                mod.openGui(player, id, world, pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        if (side != ForgeDirection.UNKNOWN) return isSideSolid(world, new Vector3i(x, y, z), facings[side.ordinal()]);
        else return super.isSideSolid(world, x, y, z, side);
    }

    public boolean isSideSolid(IBlockAccess world, Vector3i pos, EnumFacing side) {
        return super.isSideSolid(world, pos.x, pos.y, pos.z, ForgeDirection.VALID_DIRECTIONS[side.ordinal()]);
    }

    @Override
    public boolean getWeakChanges(IBlockAccess world, int x, int y, int z) {
        return getWeakChanges(world, new Vector3i(x, y, z));
    }

    public boolean getWeakChanges(IBlockAccess world, Vector3i pos) {
        return super.getWeakChanges(world, pos.x, pos.y, pos.z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);
        IBlockState state = getStateFromMeta(meta);
        onNeighborBlockChange(world, new Vector3i(x, y, z), state, block);
    }

    public void onNeighborBlockChange(World world, Vector3i pos, IBlockState state, Block block) {}

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        IBlockState state = getStateFromMeta(meta);
        return getStrongPower(world, new Vector3i(x, y, z), state, facings[side]);
    }

    public int getStrongPower(IBlockAccess world, Vector3i pos, IBlockState state, EnumFacing side) {
        return 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        IBlockState state = getStateFromMeta(meta);
        return getWeakPower(world, new Vector3i(x, y, z), state, facings[side]);
    }

    public int getWeakPower(IBlockAccess world, Vector3i pos, IBlockState state, EnumFacing side) {
        return 0;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return shouldCheckWeakPower(world, new Vector3i(x, y, z), facings[side]);
    }

    public boolean shouldCheckWeakPower(IBlockAccess world, Vector3i pos, EnumFacing side) {
        return super.shouldCheckWeakPower(world, pos.x, pos.y, pos.z, side.ordinal());
    }

    public void spawnAsEntity(World world, Vector3i pos, ItemStack stack) {
        dropBlockAsItem(world, pos.x, pos.y, pos.z, stack);
    }

    @Override
    public int damageDropped(int meta) {
        return damageDropped(getStateFromMeta(meta));
    }

    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public MapColor getMapColor(int meta) {
        if (mapColor != null) return mapColor;
        else return super.getMapColor(meta);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return getItemDropped(getStateFromMeta(meta), random, fortune);
    }

    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return super.getItemDropped(getMetaFromState(state), random, fortune);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return isFullCube();
    }

    public boolean isFullCube() {
        return super.renderAsNormalBlock();
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        return collisionRayTrace(world, new Vector3i(x, y, z), start, end);
    }

    public MovingObjectPosition collisionRayTrace(World world, Vector3i pos, Vec3 start, Vec3 end) {
        boxHit = null;
        MovingObjectPosition result = null;
        double nearestDistance = 0;
        IBlockState state = getWorldBlockState(world, pos);
        List<AxisAlignedBB> list = getGlobalCollisionBoxes(world, pos, state, null);
        if (list != null) {
            int n = list.size();
            for (int i = 0; i < n; i++) {
                AxisAlignedBB box = list.get(i);
                MovingObjectPosition mp = box.calculateIntercept(start, end);
                if (mp != null) {
                    mp.subHit = i;
                    double d = start.squareDistanceTo(mp.hitVec);
                    if (result == null || d < nearestDistance) {
                        result = mp;
                        nearestDistance = d;
                    }
                }
            }
        }
        if (result != null) {
            int i = result.subHit;
            boxHit = list.get(i).offset(-pos.x, -pos.y, -pos.z);
            result = newMovingObjectPosition(result.hitVec, result.sideHit, pos);
            result.subHit = i;
        }
        return result;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, new Vector3i(x, y, z));
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, Vector3i pos) {
        AxisAlignedBB box = boxHit;
        if (box == null) {
            IBlockState state = getWorldBlockState(world, pos);
            box = getLocalBounds(world, pos, state, null);
        }
        if (box != null) {
            setBlockBounds(box);
        } else {
            super.setBlockBoundsBasedOnState(world, pos.x, pos.y, pos.z);
        }
    }

    protected AxisAlignedBB getLocalBounds(IBlockAccess world, Vector3i pos, IBlockState state, Entity entity) {
        ModelSpec spec = getModelSpec(state);
        if (spec != null) {
            IModel model = mod.getModel(spec.modelName);
            Trans3 t = localToGlobalTransformation(world, pos, state, new Vector3d(0.5, 0.5, 0.5))
                    .translate(spec.origin);
            return t.t(model.getBounds());
        }
        return null;
    }

    public void setBlockBounds(AxisAlignedBB box) {
        setBlockBounds(
                (float) box.minX,
                (float) box.minY,
                (float) box.minZ,
                (float) box.maxX,
                (float) box.maxY,
                (float) box.maxZ);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB clip, List result,
            Entity entity) {
        Vector3i pos = new Vector3i(x, y, z);
        IBlockState state = getWorldBlockState(world, pos);
        addCollisionBoxesToList(world, pos, state, clip, result, entity);
    }

    public void addCollisionBoxesToList(World world, Vector3i pos, IBlockState state, AxisAlignedBB clip, List result,
            Entity entity) {
        List<AxisAlignedBB> list = getGlobalCollisionBoxes(world, pos, state, entity);
        if (list != null) for (AxisAlignedBB box : list) if (clip.intersectsWith(box)) result.add(box);
        else super.addCollisionBoxesToList(world, pos.x, pos.y, pos.z, clip, result, entity);
    }

    protected List<AxisAlignedBB> getGlobalCollisionBoxes(IBlockAccess world, Vector3i pos, IBlockState state,
            Entity entity) {
        Trans3 t = localToGlobalTransformation(world, pos, state);
        return getCollisionBoxes(world, pos, state, t, entity);
    }

    protected List<AxisAlignedBB> getCollisionBoxes(IBlockAccess world, Vector3i pos, IBlockState state, Trans3 t,
            Entity entity) {
        List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
        ModelSpec spec = getModelSpec(state);
        if (spec != null) {
            IModel model = mod.getModel(spec.modelName);
            model.addBoxesToList(t.translate(spec.origin), list);
        } else list.add(t.t(defaultCollisionBox));
        return list;
    }

    protected static AxisAlignedBB defaultCollisionBox = AxisAlignedBB.getBoundingBox(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return getPickBlock(target, world, new Vector3i(x, y, z));
    }

    public ItemStack getPickBlock(MovingObjectPosition target, World world, Vector3i pos) {
        return super.getPickBlock(target, world, pos.x, pos.y, pos.z);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        return getPlayerRelativeBlockHardness(player, world, new Vector3i(x, y, z));
    }

    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, Vector3i pos) {
        return super.getPlayerRelativeBlockHardness(player, world, pos.x, pos.y, pos.z);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        return getBlockHardness(world, new Vector3i(x, y, z));
    }

    public float getBlockHardness(World world, Vector3i pos) {
        return super.getBlockHardness(world, pos.x, pos.y, pos.z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer er) {
        Vector3i pos = new Vector3i(target.blockX, target.blockY, target.blockZ);
        IBlockState state = getParticleState(world, pos);
        Block block = state.getBlock();
        int meta = getMetaFromBlockState(state);
        EntityDiggingFX fx;
        int i = pos.x;
        int j = pos.y;
        int k = pos.z;
        float f = 0.1F;
        double d0 = i + RANDOM.nextDouble() * (getBlockBoundsMaxX() - getBlockBoundsMinX() - (f * 2.0F))
                + f
                + getBlockBoundsMinX();
        double d1 = j + RANDOM.nextDouble() * (getBlockBoundsMaxY() - getBlockBoundsMinY() - (f * 2.0F))
                + f
                + getBlockBoundsMinY();
        double d2 = k + RANDOM.nextDouble() * (getBlockBoundsMaxZ() - getBlockBoundsMinZ() - (f * 2.0F))
                + f
                + getBlockBoundsMinZ();
        switch (target.sideHit) {
            case 0:
                d1 = j + getBlockBoundsMinY() - f;
                break;
            case 1:
                d1 = j + getBlockBoundsMaxY() + f;
                break;
            case 2:
                d2 = k + getBlockBoundsMinZ() - f;
                break;
            case 3:
                d2 = k + getBlockBoundsMaxZ() + f;
                break;
            case 4:
                d0 = i + getBlockBoundsMinX() - f;
                break;
            case 5:
                d0 = i + getBlockBoundsMaxX() + f;
                break;
        }
        fx = new EntityDiggingFX(world, d0, d1, d2, 0, 0, 0, block, meta);
        er.addEffect(fx.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer er) {
        IBlockState state = getParticleState(world, x, y, z);
        Block block = state.getBlock();
        meta = getMetaFromBlockState(state);
        EntityDiggingFX fx;
        byte b0 = 4;
        for (int i = 0; i < b0; ++i) {
            for (int j = 0; j < b0; ++j) {
                for (int k = 0; k < b0; ++k) {
                    double d0 = x + (i + 0.5D) / b0;
                    double d1 = y + (j + 0.5D) / b0;
                    double d2 = z + (k + 0.5D) / b0;
                    fx = new EntityDiggingFX(
                            world,
                            d0,
                            d1,
                            d2,
                            d0 - x - 0.5D,
                            d1 - y - 0.5D,
                            d2 - z - 0.5D,
                            block,
                            meta);
                    er.addEffect(fx);
                }
            }
        }
        return true;
    }

    public IBlockState getParticleState(IBlockAccess world, Vector3i pos) {
        return getWorldBlockState(world, pos);
    }

    public IBlockState getParticleState(IBlockAccess world, int x, int y, int z) {
        return getWorldBlockState(world, x, y, z);
    }

    // This needs to return the MAXIMUM pass number that the block renders in.
    @Override
    public int getRenderBlockPass() {
        if (canRenderInLayer(EnumWorldBlockLayer.TRANSLUCENT)) return 1;
        else return 0;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        for (EnumWorldBlockLayer layer : BaseModClient.passLayers[pass + 1]) if (canRenderInLayer(layer)) return true;
        return false;
    }

}
