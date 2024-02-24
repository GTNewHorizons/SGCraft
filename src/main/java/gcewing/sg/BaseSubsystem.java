// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Mod Subsystem
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class BaseSubsystem<MOD extends BaseMod, CLIENT extends BaseModClient> {

    public MOD mod;
    public CLIENT client;

    public void preInit(FMLPreInitializationEvent e) {}

    public void init(FMLInitializationEvent e) {}

    public void postInit(FMLPostInitializationEvent e) {}

    public void configure(BaseConfiguration config) {}

    protected void registerBlocks() {}

    protected void registerItems() {}

    protected void registerOres() {}

    protected void registerRecipes() {}

    protected void registerTileEntities() {}

    protected void registerRandomItems() {}

    protected void registerWorldGenerators() {}

    protected void registerContainers() {}

    protected void registerEntities() {}

    protected void registerVillagers() {}

    protected void registerOther() {}

    protected void registerScreens() {}

    protected void registerBlockRenderers() {}

    protected void registerItemRenderers() {}

    protected void registerEntityRenderers() {}

    protected void registerTileEntityRenderers() {}

    protected void registerModelLocations() {}

    protected void registerOtherClient() {}

}
