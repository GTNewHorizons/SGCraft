package gcewing.sg.guis.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import gcewing.sg.SGAddressing;
import gcewing.sg.SGCraft;
import gcewing.sg.guis.screens.DHDScreen;

public class DHDSymbolButton extends GuiButton {

    public static final double ANGLE = 360.0 / 19;
    private static final double OUTER_UPPER_LIMIT = 88 * 88;
    private static final double OUTER_LOWER_LIMIT = 63 * 63;
    private static final double INNER_UPPER_LIMIT = 61 * 61;
    private static final double INNER_LOWER_LIMIT = 33 * 33;

    private static final ResourceLocation[] Textures = new ResourceLocation[] {
            SGCraft.mod.resourceLocation("textures/gui/dhd/classic_dhd_outer_buttons_1.png"),
            SGCraft.mod.resourceLocation("textures/gui/dhd/classic_dhd_outer_buttons_2.png"),
            SGCraft.mod.resourceLocation("textures/gui/dhd/classic_dhd_inner_buttons.png") };

    private final DHDScreen dhdScreen;
    private final int textureIndex;

    private final int symbol;
    private final double angle;
    private final double upperLimit, lowerLimit;
    private final int u, v;

    public DHDSymbolButton(int id, int x, int y, int u, int v, int width, int height, int symbol, boolean outer,
            int textureIndex, DHDScreen dhdScreen) {
        super(id, x, y, width, height, "");
        this.symbol = symbol;
        this.dhdScreen = dhdScreen;
        this.upperLimit = outer ? OUTER_UPPER_LIMIT : INNER_UPPER_LIMIT;
        this.lowerLimit = outer ? OUTER_LOWER_LIMIT : INNER_LOWER_LIMIT;

        if (symbol > 36) {
            enabled = false;
            this.displayString = "#";
            this.angle = 0;
        } else {
            this.angle = (((this.symbol - 1) % 19) * ANGLE + 270) % 360;
            this.displayString = String.valueOf(SGAddressing.symbolToChar(symbol - 1));
        }
        this.u = u;
        this.v = v;
        if (textureIndex > 2 || textureIndex < 0) throw new RuntimeException();
        this.textureIndex = textureIndex;
    }

    private boolean check(int mouseX, int mouseY) {
        double phi = -Math.toDegrees(Math.atan2(mouseX - dhdScreen.centreX, mouseY - dhdScreen.centreY)) + 180;

        if (phi > this.angle && phi < this.angle + ANGLE) {
            double distance = Math.pow(mouseX - dhdScreen.centreX, 2) + Math.pow(mouseY - dhdScreen.centreY, 2);
            return distance >= lowerLimit && distance <= upperLimit;
        }
        return false;
    }

    public int getHoverState(int mouseX, int mouseY) {
        if (this.enabled && this.visible) {
            if (dhdScreen.getEnteredAddress().contains(displayString)) return 2;

            if (check(mouseX, mouseY)) return 1;
        }

        return 0;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(Textures[textureIndex]);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            int k = this.getHoverState(mouseX, mouseY);

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v + k * this.height, this.width, this.height);

            int l = 14737632;

            if (packedFGColour != 0) {
                l = packedFGColour;
            } else if (!this.enabled) {
                l = 10526880;
            } else if (this.field_146123_n) {
                l = 16777120;
            }

            this.drawCenteredString(
                    fontrenderer,
                    this.displayString,
                    this.xPosition + this.width / 2,
                    this.yPosition + (this.height - 8) / 2,
                    l);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (check(mouseX, mouseY)) {
            dhdScreen.dhdButtonPressed(symbol);
            return true;
        }
        return false;
    }
}
