package gcewing.sg.guis.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import gcewing.sg.SGCraft;
import gcewing.sg.guis.screens.DHDScreen;

public class DHDCentreButton extends GuiButton {

    protected static final ResourceLocation Texture = SGCraft.mod
            .resourceLocation("textures/gui/dhd/classic_dhd_big_red_button.png");
    private final DHDScreen dhdScreen;

    private final double MaxDistance;

    public DHDCentreButton(int id, int x, int y, int width, int height, DHDScreen dhdScreen) {
        super(id, x, y, width, height, "");
        this.dhdScreen = dhdScreen;
        MaxDistance = ((double) width / 2) * ((double) width / 2);
    }

    public int getHoverState(int mouseX, int mouseY) {
        double distance = Math.pow(mouseX - dhdScreen.centreX, 2) + Math.pow(mouseY - dhdScreen.centreY, 2);

        if (dhdScreen.getConnected()) {
            return distance < MaxDistance ? 3 : 2;
        } else return distance < MaxDistance ? 1 : 0;

    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(Texture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            int k = this.getHoverState(mouseX, mouseY);

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, k * this.height, this.width, this.height);

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}
