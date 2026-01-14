// ------------------------------------------------------------------------------------------------
//
// SG Craft - Open Computers Interface GUI Screen
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.oc;

import net.minecraft.util.StatCollector;

import gcewing.sg.guis.screens.Screen;

public class OCInterfaceScreen extends Screen {

    final static int bgUSize = 256;
    final static int bgVSize = 128;

    public OCInterfaceScreen(OCInterfaceContainer container) {
        super(container);
    }

    @Override
    protected void drawBackgroundLayer() {
        bindTexture("gui/oc_sg_interface_gui.png", bgUSize, bgVSize);
        drawTexturedRect(0, 0, xSize, ySize, 0, 0);
        drawCenteredString(StatCollector.translateToLocal("gui.gcewing_sg:ocInterface.title"), xSize / 2, 5);
    }

}
