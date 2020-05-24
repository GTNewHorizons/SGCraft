//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.7 Version B - Generic Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.Thread;

import static org.lwjgl.opengl.GL11.*;

import net.minecraft.block.*;
//import net.minecraft.block.state.IBlockState;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.*;
import net.minecraftforge.client.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.client.*;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;

import gcewing.sg.BaseMod.*;
import static gcewing.sg.BaseUtils.*;
//import static gcewing.sg.BaseBlockUtils.*;

public class BaseModClient<MOD extends BaseMod<? extends BaseModClient>> implements IGuiHandler {

    public boolean debugModelRegistration = false;

    MOD base;
    boolean customRenderingRequired;
    boolean debugSound = false;

    Map<Integer, Class<? extends GuiScreen>> screenClasses =
        new HashMap<Integer, Class<? extends GuiScreen>>();

    public BaseModClient(MOD mod) {
        base = mod;
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void preInit(FMLPreInitializationEvent e) {
//         System.out.printf("BaseModClient.preInit\n");
        registerSavedVillagerSkins();
//         registerDummyStateMappers();
        for (BaseSubsystem sub : base.subsystems) {
            sub.registerBlockRenderers();
            sub.registerItemRenderers();
        }
        registerDefaultRenderers();
//         registerDefaultModelLocations();
        removeUnusedDefaultTextureNames();
   }
    
    public void init(FMLInitializationEvent e) {
//         System.out.printf("BaseModClient.init\n");
    }
    
    public void postInit(FMLPostInitializationEvent e) {
//         System.out.printf("BaseModClient.postInit\n");
        for (BaseSubsystem sub : base.subsystems) {
            sub.registerModelLocations();
            sub.registerTileEntityRenderers();
            sub.registerEntityRenderers();
            sub.registerScreens();
            sub.registerOtherClient();
        }
//         if (customRenderingRequired)
//             enableCustomRendering();
    }
    
    void registerSavedVillagerSkins() {
        VillagerRegistry reg = VillagerRegistry.instance();
        for (VSBinding b : base.registeredVillagers)
            reg.registerVillagerSkin(b.id, b.object);
    }
        
//  String qualifyName(String name) {
//      return getClass().getPackage().getName() + "." + name;
//  }
    
    void registerOther() {}
    
    //-------------- Screen registration --------------------------------------------------------
    
    void registerScreens() {
        //  Make calls to addScreen() here.
    }
    
    //  Screen classes registered using addScreen() must implement one of:
    //
    //  (1) A static method create(EntityPlayer, World, BlockPos [, int param])
    //  (2) A constructor MyScreen(EntityPlayer, World, BlockPos [, int param])
    //  (3) A constructor MyScreen(MyContainer) where MyContainer is the
    //      corresponding registered container class

    public void addScreen(Enum id, Class<? extends GuiScreen> cls) {
        addScreen(id.ordinal(), cls);
    }

    public void addScreen(int id, Class<? extends GuiScreen> cls) {
        if (screenClasses.containsKey(id))
            throw new RuntimeException("Duplicate screen registration with ID " + id);
        screenClasses.put(id, cls);
    }
    
    //-------------- Renderer registration --------------------------------------------------------
    
    protected void registerBlockRenderers() {}
    protected void registerItemRenderers() {}
    protected void registerEntityRenderers() {}
    protected void registerTileEntityRenderers() {}

    public void addTileEntityRenderer(Class <? extends TileEntity> teClass, TileEntitySpecialRenderer renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
    }
    
    public void addEntityRenderer(Class<? extends Entity> entityClass, Render renderer) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderer);
    }
    
    public void addEntityRenderer(Class<? extends Entity> entityClass, Class<? extends Render> rendererClass) {
        Render renderer;
        try {
            renderer = rendererClass.newInstance();
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        addEntityRenderer(entityClass, renderer);
    }
    
    protected void registerDefaultRenderers() {
        for (Block block : base.registeredBlocks) {
            Item item = Item.getItemFromBlock(block);
            if (block instanceof IBlock) {
                if (debugModelRegistration)
                    System.out.printf("BaseModClient.registerDefaultRenderers: %s %s\n",
                        block, block.getUnlocalizedName());
                if (!blockRenderers.containsKey(block)) {
                    String name = ((IBlock)block).getQualifiedRendererClassName();
                    if (name != null) {
                        try {
                            Class cls = Class.forName(name);
                            addBlockRenderer((IBlock)block, (ICustomRenderer)cls.newInstance());
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (blockNeedsCustomRendering(block)) {
                    installCustomBlockRenderDispatcher((IBlock)block);
                    installCustomItemRenderDispatcher(item);
                }
            }
            if (itemNeedsCustomRendering(item))
                installCustomItemRenderDispatcher(item);
        }
        for (Item item : base.registeredItems) {
            if (debugModelRegistration)
                System.out.printf("BaseModClient.registerDefaultRenderers: %s %s\n",
                    item, item.getUnlocalizedName());
            if (itemNeedsCustomRendering(item))
                installCustomItemRenderDispatcher(item);
        }
    }
    
    protected void installCustomBlockRenderDispatcher(IBlock block) {
        if (debugModelRegistration)
            System.out.printf("BaseModClient.installCustomBlockRenderDisatcher: %s\n", block);
        block.setRenderType(getCustomBlockRenderType());
    }
    
    protected void installCustomItemRenderDispatcher(Item item) {
        if (item != null) {
            if (debugModelRegistration)
                System.out.printf("BaseModClient.installCustomItemRenderDispatcher: %s\n", item);
            MinecraftForgeClient.registerItemRenderer(item, getItemRenderDispatcher());
        }
    }
    
    protected void removeUnusedDefaultTextureNames() {
        for (Block block : base.registeredBlocks) {
            if (blockNeedsCustomRendering(block)) {
                if (debugModelRegistration)
                    System.out.printf("BaseModClient: Removing default texture from block %s\n",
                        block.getUnlocalizedName());
                block.setBlockTextureName("minecraft:stone");
            }
        }
        for (Item item : base.registeredItems) {
            if (itemNeedsCustomRendering(item)) {
                if (debugModelRegistration)
                    System.out.printf("BaseModClient: Removing default texture from item %s\n",
                        item.getUnlocalizedName());
                item.setTextureName("minecraft:apple");
            }
        }
    }

    //-------------- Client-side guis ------------------------------------------------
    
    public static void openClientGui(GuiScreen gui) {
        FMLClientHandler.instance().getClient().displayGuiScreen(gui);
    }
    
    //-------------- Rendering --------------------------------------------------------
    
    public ResourceLocation textureLocation(String path) {
        return base.resourceLocation("textures/" + path);
    }
    
    public void bindTexture(String path) {
        bindTexture(textureLocation(path));
    }
    
    public static void bindTexture(ResourceLocation rsrc) {
        TextureManager tm = Minecraft.getMinecraft().getTextureManager();
        tm.bindTexture(rsrc);
    }
    
    //-------------- GUI - Internal --------------------------------------------------------
    
    /**
     * Returns a Container to be displayed to the user. 
     * On the client side, this needs to return a instance of GuiScreen
     * On the server side, this needs to return a instance of Container
     *
     * @param ID The Gui ID Number
     * @param player The player viewing the Gui
     * @param world The current world
     * @param pos Position in world
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return base.getServerGuiElement(id, player, world, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return getClientGuiElement(id, player, world, new BlockPos(x, y, z));
    }

    public Object getClientGuiElement(int id, EntityPlayer player, World world, BlockPos pos) {
        int param = id >> 16;
        id = id & 0xffff;
        Object result = null;
        if (base.debugGui)
            System.out.printf("BaseModClient.getClientGuiElement: for id %s\n", id);
        Class scrnCls = screenClasses.get(id);
        if (scrnCls != null) {
            if (base.debugGui)
                System.out.printf("BaseModClient.getClientGuiElement: Instantiating %s\n", scrnCls);
            // If there is a container class registered for this gui and the screen class has
            // a constructor taking it, instantiate the screen automatically.
            Class contCls = base.containerClasses.get(id);
            if (contCls != null) {
                try {
                    if (base.debugGui)
                        System.out.printf("BaseModClient.getClientGuiElement: Looking for constructor taking %s\n", contCls);
                    Constructor ctor = scrnCls.getConstructor(contCls);
                    if (base.debugGui)
                        System.out.printf("BaseModClient.getClientGuiElement: Instantiating container\n");
                    Object cont = base.createGuiElement(contCls, player, world, pos, param);
                    if (cont != null) {
                        if (base.debugGui)
                            System.out.printf("BaseModClient.getClientGuiElement: Instantiating screen with container\n");
                        try {
                            result = ctor.newInstance(cont);
                        }
                        catch (Exception e) {
                            //throw new RuntimeException(e);
                            base.reportExceptionCause(e);
                            return null;
                        }
                    }
                }
                catch (NoSuchMethodException e) {
                }
            }
            // Otherwise, contruct screen from player, world, pos.
            if (result == null)
                result = base.createGuiElement(scrnCls, player, world, pos, param);
        }
        else {
            result = getGuiScreen(id, player, world, pos, param);
        }
        base.setModOf(result);
        if (base.debugGui)
            System.out.printf("BaseModClient.getClientGuiElement: returning %s\n", result);
        return result;
    }
    
    GuiScreen getGuiScreen(int id, EntityPlayer player, World world, BlockPos pos, int param) {
        //  Called when screen id not found in registry
        System.out.printf("%s: BaseModClient.getGuiScreen: No GuiScreen class found for gui id %d\n", 
            this, id);
        return null;
    }

    //======================================= Custom Rendering =======================================
    
    public interface ICustomRenderer {
        void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, IRenderTarget target,
            EnumWorldBlockLayer layer, Trans3 t);
        void renderItemStack(ItemStack stack, IRenderTarget target, Trans3 t);
    }
    
    public interface ITexture {
        ResourceLocation location();
        int tintIndex();
        double red();
        double green();
        double blue();
        double interpolateU(double u);
        double interpolateV(double v);
        boolean isEmissive();
        boolean isProjected();
        boolean isSolid();
        ITexture tinted(int index);
        ITexture colored(double red, double green, double blue);
        ITexture projected();
        ITexture emissive();
        ITiledTexture tiled(int numRows, int numCols);
    }
    
    public interface ITiledTexture extends ITexture {
        ITexture tile(int row, int col);
    }

    public interface IRenderTarget {
        boolean isRenderingBreakEffects();
        void setTexture(ITexture texture);
        void setColor(double r, double g, double b, double a);
        void setNormal(Vector3 n);
        void beginTriangle();
        void beginQuad();
        void addVertex(Vector3 p, double u, double v);
        void addProjectedVertex(Vector3 p, EnumFacing face);
        void endFace();
    }
    
    public interface IModel {
        AxisAlignedBB getBounds();
        void addBoxesToList(Trans3 t, List list);
        void render(Trans3 t, IRenderTarget renderer, ITexture... textures);
    }
    
    public static class TextureCache extends HashMap<ResourceLocation, ITexture> {}
    
    protected Map<IBlock, ICustomRenderer> blockRenderers = new HashMap<IBlock, ICustomRenderer>();
    protected Map<Item, ICustomRenderer> itemRenderers = new HashMap<Item, ICustomRenderer>();
    protected Map<IBlockState, ICustomRenderer> stateRendererCache = new HashMap<IBlockState, ICustomRenderer>();
    protected TextureCache[] textureCaches = new TextureCache[2];
    {
        for (int i = 0; i < 2; i++)
            textureCaches[i] = new TextureCache();
    }
    
    //-------------- Renderer registration -------------------------------

    public void addBlockRenderer(IBlock block, ICustomRenderer renderer) {
        blockRenderers.put(block, renderer);
        customRenderingRequired = true;
//         block.setRenderType(getCustomBlockRenderType());
        Item item = Item.getItemFromBlock((Block)block);
        if (item != null && !itemRenderers.containsKey(item))
            addItemRenderer(item, renderer);
    }
    
    public void addItemRenderer(Item item, ICustomRenderer renderer) {
        itemRenderers.put(item, renderer);
//      MinecraftForgeClient.registerItemRenderer(item, getItemRenderDispatcher());
    }
    
    //--------------- Model Locations ----------------------------------------------------
    
    protected boolean blockNeedsCustomRendering(Block block) {
        return blockRenderers.containsKey(block) || specifiesTextures(block);
    }
    
    protected boolean itemNeedsCustomRendering(Item item) {
        return itemRenderers.containsKey(item) || specifiesTextures(item);
    }
    
    protected boolean specifiesTextures(Object obj) {
        return obj instanceof ITextureConsumer && ((ITextureConsumer)obj).getTextureNames() != null;
    }

    //------------------------------------------------------------------------------------------------
    
    public static EnumWorldBlockLayer[][] passLayers = {
        {EnumWorldBlockLayer.SOLID, EnumWorldBlockLayer.CUTOUT_MIPPED, EnumWorldBlockLayer.CUTOUT,
            EnumWorldBlockLayer.TRANSLUCENT},
        {EnumWorldBlockLayer.SOLID, EnumWorldBlockLayer.CUTOUT_MIPPED, EnumWorldBlockLayer.CUTOUT},
        {EnumWorldBlockLayer.TRANSLUCENT}
    };

    protected BlockRenderDispatcher blockRenderDispatcher;

    protected int getCustomBlockRenderType() {
        return getBlockRenderDispatcher().renderID;
    }
    
    protected BlockRenderDispatcher getBlockRenderDispatcher() {
        if (blockRenderDispatcher == null)
            blockRenderDispatcher = new BlockRenderDispatcher();
        return blockRenderDispatcher;
    }
    
    protected class BlockRenderDispatcher implements ISimpleBlockRenderingHandler {
    
        protected int renderID;
        
        public BlockRenderDispatcher() {
            renderID = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(renderID, this);
        }
        
        public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
            boolean result = false;
            BlockPos pos = new BlockPos(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            BaseBlock baseBlock = (BaseBlock)block;
            IBlockState state = baseBlock.getStateFromMeta(meta);
            if (debugRenderBlock)
                System.out.printf("BaseModClient.BlockRenderDispatcher.renderWorldBlock: %s with meta %s state %s\n",
                    block, meta, state);
            ICustomRenderer renderer = getCustomBlockRenderer(world, pos, state);
            if (renderer != null) {
                if (debugRenderBlock)
                    System.out.printf("BaseModClient.BlockRenderDispatcher.renderWorldBlock: using %s\n",
                        renderer);
                int pass = ForgeHooksClient.getWorldRenderPass();
                for (EnumWorldBlockLayer layer : passLayers[pass + 1]) {
                    if (debugRenderBlock)
                        System.out.printf("BaseModClient.BlockRenderDispatcher.renderWorldBlock: %s in layer %s\n",
                            block, layer);
                    if (baseBlock.canRenderInLayer(layer)) {
                        BaseWorldRenderTarget target = new BaseWorldRenderTarget(world, pos,
                            Tessellator.instance, rb.overrideBlockTexture);
                        Trans3 t = Trans3.blockCenter(pos);
                        renderer.renderBlock(world, pos, state, target, layer, t);
                        if (target.end())
                            result = true;
                    }
                }
            }
            return result;
        }

        public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
        }

        public boolean shouldRender3DInInventory(int modelId) {
            return true;
        }

        public int getRenderId() {
            return renderID;
        }
    
    }   

    //------------------------------------------------------------------------------------------------
    
    public static boolean debugRenderBlock = false;
    public static boolean debugRenderItem = false;

    protected ItemRenderDispatcher itemRenderDispatcher;
    
    protected ItemRenderDispatcher getItemRenderDispatcher() {
        if (itemRenderDispatcher == null)
            itemRenderDispatcher = new ItemRenderDispatcher();
        return itemRenderDispatcher;
    }

    protected static BaseGLRenderTarget glTarget = new BaseGLRenderTarget();

    protected static Trans3 entityTrans = Trans3.blockCenter;
    protected static Trans3 equippedTrans = Trans3.blockCenter;
    protected static Trans3 firstPersonTrans = Trans3.blockCenterSideTurn(0, 3);
    protected static Trans3 inventoryTrans = Trans3.blockCenter;
    
    protected class ItemRenderDispatcher implements IItemRenderer {
    
        public boolean handleRenderType(ItemStack item, ItemRenderType type) {
            return type != ItemRenderType.FIRST_PERSON_MAP;
        }
        
        public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
            return true;
        }
        
        public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
            if (debugRenderItem)
                System.out.printf("BaseModClient.ItemRenderDispatcher.renderItem: %s %s pass %s\n",
                    type, stack, MinecraftForgeClient.getRenderPass());
            ICustomRenderer renderer = itemRenderers.get(stack.getItem());
            if (debugRenderItem)
                System.out.printf("BaseModClient.ItemRenderDispatcher.renderItem: Custom renderer = %s\n", renderer);
            if (renderer == null) {
                renderer = getModelRendererForItemStack(stack);
                if (debugRenderItem)
                    System.out.printf("BaseModClient.ItemRenderDispatcher.renderItem: Model renderer = %s\n", renderer);
            }
            if (renderer != null) {
                Trans3 t;
                switch (type) {
                    case ENTITY:
                        t = entityTrans;
                        break;
                    case EQUIPPED:
                        t = equippedTrans;
                        break;
                    case EQUIPPED_FIRST_PERSON:
                        t = firstPersonTrans;
                        break;
                    case INVENTORY:
                        t = inventoryTrans;
                        glEnable(GL_BLEND);
                        glEnable(GL_CULL_FACE);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                         break;
                    default:
                        return;
                }
                glTarget.start(false);
                renderer.renderItemStack(stack, glTarget, t);
                glTarget.finish();
                switch (type) {
                    case INVENTORY:
                        glDisable(GL_BLEND);
                        glDisable(GL_CULL_FACE);
                        break;
                }
            }
        }
    
    }

    //------------------------------------------------------------------------------------------------

    protected ICustomRenderer getCustomBlockRenderer(IBlockAccess world, BlockPos pos, IBlockState state) {
        //System.out.printf("BaseModClient.getCustomRenderer: %s\n", state);
        BaseBlock block = (BaseBlock)state.getBlock();
        ICustomRenderer rend = blockRenderers.get(block);
        if (rend == null && block instanceof IBlock) {
            IBlockState astate = block.getActualState(state, world, pos);
            rend = getModelRendererForState(astate);
        }
        return rend;
    }
    
    protected ICustomRenderer getModelRendererForSpec(ModelSpec spec, int textureType) {
        //System.out.printf("BaseModClient.getModelRendererForSpec: %s", spec.modelName);
        //for (int i = 0; i < spec.textureNames.length; i++)
        //  System.out.printf(" %s", spec.textureNames[i]);
        //System.out.printf("\n");
        IModel model = getModel(spec.modelName);
        ITexture[] textures = new ITexture[spec.textureNames.length];
        for (int i = 0; i < textures.length; i++)
            textures[i] = getTexture(textureType, spec.textureNames[i]);
        //for (int i = 0; i < spec.textureNames.length; i++)
        //  System.out.printf("BaseModClient.getModelRendererForSpec: texture[%s] = %s\n",
        //      i, textures[i]);
        return new BaseModelRenderer(model, spec.origin, textures);
    }
    
    protected ICustomRenderer getModelRendererForState(IBlockState astate) {
        //System.out.printf("BaseModClient.getModelRendererForState: %s\n", astate);
        ICustomRenderer rend = stateRendererCache.get(astate);
        if (rend == null) {
            Block block = astate.getBlock();
            if (block instanceof IBlock) {
                ModelSpec spec = ((IBlock)block).getModelSpec(astate);
                if (spec != null) {
                    rend = getModelRendererForSpec(spec, 0);
                    stateRendererCache.put(astate, rend);
                }
            }
        }
        return rend;
    }
    
    protected ICustomRenderer getModelRendererForItemStack(ItemStack stack) {
        Item item = stack.getItem();
        if (debugRenderItem)
            System.out.printf("BaseModClient.getModelRendererForItemStack: item = %s %s\n",
                item, item.getUnlocalizedName());
        if (item instanceof IItem) {
            ModelSpec spec = ((IItem)item).getModelSpec(stack);
            if (spec != null)
                return getModelRendererForSpec(spec, 1);
        }
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock)item).field_150939_a;
            if (debugRenderItem)
                System.out.printf("BaseModClient.getModelRendererForItemStack: block = %s %s\n",
                    block, block.getUnlocalizedName());
            if (block instanceof BaseBlock) {
                IBlockState state = BaseBlockUtils.getBlockStateFromItemStack(stack);
                ModelSpec spec = ((IBlock)block).getModelSpec(state);
                return getModelRendererForSpec(spec, 0);
            }
        }
        return null;
    }
    
    // Call this from renderBlock of an ICustomRenderer to fall back to model spec
    public void renderBlockUsingModelSpec(IBlockAccess world, BlockPos pos, IBlockState state,
        IRenderTarget target, EnumWorldBlockLayer layer, Trans3 t)
    {
        ICustomRenderer rend = getModelRendererForState(state);
        if (rend != null)
            rend.renderBlock(world, pos, state, target, layer, t);
    }
    
    // Call this from renderItemStack of an ICustomRenderer to fall back to model spec
    public void renderItemStackUsingModelSpec(ItemStack stack, IRenderTarget target, Trans3 t) {
        ICustomRenderer rend = getModelRendererForItemStack(stack);
        if (debugRenderItem) System.out.printf("BaseModClient.renderItemStackUsingModelSpec: renderer = %s\n", rend);
        if (rend != null)
            rend.renderItemStack(stack, target, t);
    }

    public IModel getModel(String name) {
        return base.getModel(name);
    }

    public ITexture getTexture(int type, String name) {
        // Cache is keyed by texture name without "textures/"
        ResourceLocation loc = base.resourceLocation(name);
        return textureCaches[type].get(loc);
    }
    
    public IIcon getIcon(int type, String name) {
        return ((BaseTexture.Sprite)getTexture(type, name)).icon;
    }

    @SubscribeEvent
    public void onTextureStitchEventPre(TextureStitchEvent.Pre e) {
        int type = e.map.getTextureType();
//         System.out.printf("BaseModClient.onTextureStitchEventPre: %s [%s]\n", e.map, type);
        if (type >= 0 && type <= 1) {
            TextureCache cache = textureCaches[type];
            cache.clear();
            switch (type) {
                case 0:
                    for (Block block : base.registeredBlocks)
                        registerSprites(e.map, cache, block);
                    break;
                case 1:
                    for (Item item : base.registeredItems)
                        registerSprites(e.map, cache, item);
                    break;
            }
        }
    }
    
    protected void registerSprites(TextureMap reg, TextureCache cache, Object obj) {
        if (debugModelRegistration)
            System.out.printf("BaseModClient.registerSprites: for %s\n", obj);
        if (obj instanceof ITextureConsumer) {
            String names[] = ((ITextureConsumer)obj).getTextureNames();
            if (names != null) {
                customRenderingRequired = true;
                for (String name : names) {
                    ResourceLocation loc = base.resourceLocation(name); // TextureMap adds "textures/"
                    if (cache.get(loc) == null) {
                        if (debugModelRegistration)
                            System.out.printf("BaseModClient.registerSprites: %s\n", loc);
                        IIcon icon = reg.registerIcon(loc.toString());
                        ITexture texture = BaseTexture.fromSprite(icon);
                        cache.put(loc, texture);
                    }
                }
            }
        }
    }
    
}
