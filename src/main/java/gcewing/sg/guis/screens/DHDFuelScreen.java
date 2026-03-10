// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate controller fuelling gui screen
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis.screens;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;

import gcewing.sg.SGCraft;
import gcewing.sg.guis.DHDTE;
import gcewing.sg.guis.SGScreen;
import gcewing.sg.guis.containers.DHDFuelContainer;

public class DHDFuelScreen extends SGScreen {

    static String screenTitleKey = "gui.gcewing_sg:dhdFuel.title";
    public static final int guiWidth = 256;
    public static final int guiHeight = 208;
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
        drawCenteredString(StatCollector.translateToLocal(screenTitleKey), cx, 8);
        if (DHDTE.numFuelSlots > 0) {
            drawString(StatCollector.translateToLocal("gui.gcewing_sg:dhdFuel.fuel"), 150, 96);
        }
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
