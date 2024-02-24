// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate controller fuelling gui screen
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis;

import gcewing.sg.SGCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;

public class DHDFuelScreen extends SGScreen {

    static String screenTitle = "Stargate Controller";
    static final int guiWidth = 256;
    static final int guiHeight = 208;
    static final int fuelGaugeWidth = 16;
    static final int fuelGaugeHeight = 34;
    static final int fuelGaugeX = 214;
    static final int fuelGaugeY = 84;
    static final int fuelGaugeU = 0;
    static final int fuelGaugeV = 208;

    DHDTE te;

    public static DHDFuelScreen create(EntityPlayer player, World world, Vector3i pos) {
        DHDTE te = DHDTE.at(world, pos);
        if (te != null) return new DHDFuelScreen(player, te);
        return null;
    }

    public DHDFuelScreen(EntityPlayer player, DHDTE te) {
        super(new DHDFuelContainer(player, te), guiWidth, guiHeight);
        this.te = te;
    }

    @Override
    protected void drawBackgroundLayer() {
        bindTexture(SGCraft.mod.resourceLocation("textures/gui/dhd_fuel_gui.png"), 256, 256);
        drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);
        drawFuelGauge();
        int cx = xSize / 2;
        setTextColor(0x004c66);
        drawCenteredString(screenTitle, cx, 8);
        if (DHDTE.numFuelSlots > 0) drawString("Fuel", 150, 96);
    }

    void drawFuelGauge() {
        int level = (int) (fuelGaugeHeight * te.energyInBuffer / DHDTE.maxEnergyBuffer);
        if (level > fuelGaugeHeight) level = fuelGaugeHeight;
        GL11.glEnable(GL11.GL_BLEND);
        drawTexturedRect(
                fuelGaugeX,
                fuelGaugeY + fuelGaugeHeight - level,
                fuelGaugeWidth,
                level,
                fuelGaugeU,
                fuelGaugeV);
        GL11.glDisable(GL11.GL_BLEND);
    }

}
