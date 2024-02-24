// ------------------------------------------------------------------------------------------------
//
// SG Craft - Client Proxy
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.entities.IrisEntity;
import gcewing.sg.guis.DHDFuelScreen;
import gcewing.sg.guis.DHDScreen;
import gcewing.sg.guis.PowerScreen;
import gcewing.sg.guis.SGBaseScreen;
import gcewing.sg.guis.SGGui;
import gcewing.sg.renderers.DHDTERenderer;
import gcewing.sg.renderers.IrisRenderer;
import gcewing.sg.renderers.SGBaseTERenderer;
import gcewing.sg.tileentities.SGBaseTE;

public class SGCraftClient extends BaseModClient<SGCraft> {

    public SGCraftClient(SGCraft mod) {
        super(mod);
    }

    @Override
    protected void registerScreens() {
        addScreen(SGGui.SGBase, SGBaseScreen.class);
        addScreen(SGGui.SGController, DHDScreen.class);
        addScreen(SGGui.DHDFuel, DHDFuelScreen.class);
        addScreen(SGGui.PowerUnit, PowerScreen.class);
    }

    @Override
    protected void registerTileEntityRenderers() {
        addTileEntityRenderer(SGBaseTE.class, new SGBaseTERenderer());
        addTileEntityRenderer(DHDTE.class, new DHDTERenderer());
    }

    @Override
    protected void registerEntityRenderers() {
        addEntityRenderer(IrisEntity.class, IrisRenderer.class);
    }

}
