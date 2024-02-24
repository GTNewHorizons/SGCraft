// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate gui base class
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import net.minecraft.inventory.Container;

import gcewing.sg.SGCraft;
import gcewing.sg.guis.containers.BaseContainer;
import gcewing.sg.guis.screens.Screen;
import gcewing.sg.tileentities.SGBaseTE;

// ------------------------------------------------------------------------------------------------

public class SGScreen extends Screen {

    final static String symbolTextureFile = "symbols48.png";
    final static int symbolsPerRowInTexture = 10;
    final static int symbolWidthInTexture = 48;
    final static int symbolHeightInTexture = 48;
    final static int symbolTextureWidth = 512;
    final static int symbolTextureHeight = 256;
    final static int frameHeight = 44;
    final static int cellSize = 24;

    public SGScreen() {
        super(new BaseContainer(0, 0));
    }

    public SGScreen(Container container, int width, int height) {
        super(container, width, height);
    }

    protected void drawAddressSymbols(int x, int y, String address) {
        int x0 = x - address.length() * cellSize / 2;
        int y0 = y + frameHeight / 2 - cellSize / 2;
        bindSGTexture(
                symbolTextureFile,
                symbolTextureWidth * cellSize / symbolWidthInTexture,
                symbolTextureHeight * cellSize / symbolHeightInTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        int n = address.length();
        for (int i = 0; i < n; i++) {
            int s = SGBaseTE.charToSymbol(address.charAt(i));
            int row = s / symbolsPerRowInTexture;
            int col = s % symbolsPerRowInTexture;
            drawTexturedRect(x0 + i * cellSize, y0, cellSize, cellSize, col * cellSize, row * cellSize);
        }
    }

    protected void drawAddressString(int x, int y, String address) {
        drawCenteredString(this.fontRendererObj, address, x, y, 0xffffff);
    }

    void bindSGTexture(String name, int usize, int vsize) {
        bindTexture(SGCraft.mod.resourceLocation("textures/gui/" + name), usize, vsize);
    }

}
