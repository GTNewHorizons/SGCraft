//------------------------------------------------------------------------------------------------
//
//   SG Craft - Computercraft Integration Module
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import gcewing.sg.BaseSubsystem;
import gcewing.sg.IIntegration;
import gcewing.sg.SGCraft;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class CCIntegration extends BaseSubsystem implements IIntegration {

    public static Block ccInterface;

//  @Override
//  public void init(SGCraft mod) {
//      System.out.printf("CCIntegration.init\n");
//      this.mod = mod;
//      ComputerCraftAPI.registerPeripheralProvider(new CCPeripheralProvider());
//      CCMethodQueue.init();
//  }

    @Override
    public void registerBlocks() {
        System.out.print("CCIntegration.registerBlocks\n");
        ccInterface = SGCraft.mod.newBlock("ccInterface", CCInterfaceBlock.class);
    }

//  @Override
//  public void registerItems() {
//  }

    @Override
    public void registerRecipes() {
        SGCraft.mod.newRecipe(ccInterface, 1, "SnS", "SrS", "SSS",
                'S', Blocks.stone, 'n', "ingotNaquadahAlloy", 'r', Items.redstone);
    }

    @Override
    public void registerOther() {
        ComputerCraftAPI.registerPeripheralProvider(new CCPeripheralProvider());
        CCMethodQueue.init();
    }

    @Override
    public void onServerTick() {
        //System.out.printf("CCIntegration.onServerTick\n");
        CCMethodQueue.onServerTick();
    }

}
