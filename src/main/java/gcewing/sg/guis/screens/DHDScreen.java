// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate controller gui screen
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis.screens;

import static gcewing.sg.utils.BaseBlockUtils.getWorldTileEntity;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ENABLE_BIT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.joml.Vector3i;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import gcewing.sg.SGAddressing;
import gcewing.sg.SGCraft;
import gcewing.sg.SGState;
import gcewing.sg.guis.DHDTE;
import gcewing.sg.guis.SGScreen;
import gcewing.sg.guis.widgets.DHDCentreButton;
import gcewing.sg.guis.widgets.DHDSymbolButton;
import gcewing.sg.packets.SGChannel;
import gcewing.sg.tileentities.SGBaseTE;

public class DHDScreen extends SGScreen {

    final static int dhdWidth = 192;
    final static int dhdHeight = 192;

    final static int BaseId = 10;
    ResourceLocation background = SGCraft.mod.resourceLocation("textures/gui/dhd/classic_dhd_background.png");
    World world;
    Vector3i pos;
    public int centreX, centreY;
    int closingDelay = 0;
    int addressLength;
    DHDTE cte;

    public DHDScreen(EntityPlayer player, World world, Vector3i pos) {
        this.world = world;
        this.pos = pos;
        cte = getControllerTE();
        SGBaseTE te = getStargateTE();
        if (te != null) addressLength = te.getNumChevrons();
    }

    SGBaseTE getStargateTE() {
        if (cte != null) return cte.getLinkedStargateTE();
        return null;
    }

    DHDTE getControllerTE() {
        TileEntity te = getWorldTileEntity(world, pos);
        if (te instanceof DHDTE) return (DHDTE) te;
        return null;
    }

    public String getEnteredAddress() {
        return cte.enteredAddress;
    }

    void setEnteredAddress(String address) {
        cte.enteredAddress = address;
        SGChannel.sendEnteredAddressToServer(cte, address);
    }

    @Override
    public void initGui() {
        this.centreX = width / 2;
        this.centreY = height / 2;

        this.buttonList
                .add(new DHDSymbolButton(BaseId + 1, centreX - 88, centreY - 28, 0, 0, 28, 27, 1, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 2, centreX - 82, centreY - 53, 28, 0, 31, 31, 2, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 3, centreX - 69, centreY - 73, 59, 0, 34, 34, 3, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 4, centreX - 46, centreY - 85, 93, 0, 29, 31, 4, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 5, centreX - 21, centreY - 88, 122, 0, 28, 26, 5, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 6, centreX + 7, centreY - 87, 150, 0, 28, 29, 6, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 7, centreX + 26, centreY - 80, 178, 0, 33, 33, 7, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 8, centreX + 44, centreY - 64, 211, 0, 33, 33, 8, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 9, centreX + 56, centreY - 40, 0, 93, 30, 28, 9, true, 0, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 10, centreX + 62, centreY - 14, 30, 93, 26, 28, 10, true, 0, this));

        this.buttonList
                .add(new DHDSymbolButton(BaseId + 11, centreX + 56, centreY + 12, 0, 0, 30, 28, 11, true, 1, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 12, centreX + 44, centreY + 31, 30, 0, 33, 33, 12, true, 1, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 13, centreX + 26, centreY + 47, 63, 0, 33, 33, 13, true, 1, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 14, centreX + 7, centreY + 58, 96, 0, 28, 29, 14, true, 1, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 15, centreX - 21, centreY + 62, 124, 0, 28, 26, 15, true, 1, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 16, centreX - 46, centreY + 54, 152, 0, 29, 31, 16, true, 1, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 17, centreX - 69, centreY + 39, 181, 0, 34, 34, 17, true, 1, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 18, centreX - 82, centreY + 22, 215, 0, 31, 31, 18, true, 1, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 19, centreX - 88, centreY + 1, 0, 84, 28, 27, 19, true, 1, this));

        this.buttonList
                .add(new DHDSymbolButton(BaseId + 20, centreX - 61, centreY - 19, 0, 0, 29, 18, 20, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 21, centreX - 57, centreY - 36, 29, 0, 30, 25, 21, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 22, centreX - 47, centreY - 49, 59, 0, 28, 29, 22, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 23, centreX - 32, centreY - 58, 87, 0, 22, 30, 23, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 24, centreX - 14, centreY - 60, 109, 0, 18, 29, 24, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 25, centreX + 3, centreY - 61, 127, 0, 20, 30, 25, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 26, centreX + 14, centreY - 55, 147, 0, 27, 30, 26, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 27, centreX + 23, centreY - 44, 174, 0, 30, 27, 27, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 28, centreX + 30, centreY - 28, 204, 0, 30, 22, 28, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 29, centreX + 33, centreY - 9, 0, 90, 28, 18, 29, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 30, centreX + 30, centreY + 6, 28, 90, 30, 22, 30, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 31, centreX + 23, centreY + 18, 58, 90, 30, 27, 31, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 32, centreX + 14, centreY + 25, 88, 90, 27, 30, 32, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 33, centreX + 3, centreY + 31, 115, 90, 20, 30, 33, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 34, centreX - 14, centreY + 32, 135, 90, 18, 29, 34, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 35, centreX - 32, centreY + 28, 153, 90, 22, 30, 35, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 36, centreX - 47, centreY + 21, 175, 90, 28, 29, 36, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 37, centreX - 57, centreY + 12, 203, 90, 30, 25, 37, false, 2, this));
        this.buttonList
                .add(new DHDSymbolButton(BaseId + 38, centreX - 61, centreY + 1, 0, 180, 29, 18, 38, false, 2, this));

        this.buttonList.add(new DHDCentreButton(90, centreX - 54 / 2, centreY - 54 / 2, 54, 54, this));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (closingDelay > 0) {
            if (--closingDelay == 0) {
                setEnteredAddress("");
                close();
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled && button.id == 90) {
            orangeButtonPressed(false);
        }
    }

    void closeAfterDelay(int ticks) {
        closingDelay = ticks;
    }

    public void dhdButtonPressed(int i) {
        buttonSound();
        if (i >= 37) backspace();
        else enterCharacter(SGBaseTE.symbolToChar(i - 1));
    }

    void buttonSound() {
        EntityPlayer player = mc.thePlayer;
        ISound sound = new PositionedSoundRecord(
                new ResourceLocation("random.click"),
                1.0F,
                1.0F,
                (float) player.posX,
                (float) player.posY,
                (float) player.posZ);
        mc.getSoundHandler().playSound(sound);
    }

    @Override
    public void keyTyped(char c, int key) {
        if (key == Keyboard.KEY_ESCAPE) close();
        else if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE) backspace();
        else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) orangeButtonPressed(true);
        else {
            String C = String.valueOf(c).toUpperCase();
            if (SGAddressing.isValidSymbolChar(C)) enterCharacter(C.charAt(0));
        }
    }

    void orangeButtonPressed(boolean connectOnly) {
        SGBaseTE te = getStargateTE();
        if (te != null) {
            if (te.state == SGState.Idle) sendConnectOrDisconnect(te, getEnteredAddress());
            else if (!connectOnly) sendConnectOrDisconnect(te, "");
        }
    }

    void sendConnectOrDisconnect(SGBaseTE te, String address) {
        SGChannel.sendConnectOrDisconnectToServer(te, address);
        closeAfterDelay(10);
    }

    void backspace() {
        if (stargateIsIdle()) {
            buttonSound();
            String a = getEnteredAddress();
            int n = a.length();
            if (n > 0) setEnteredAddress(a.substring(0, n - 1));
        }
    }

    void enterCharacter(char c) {
        if (stargateIsIdle()) {
            buttonSound();
            String a = getEnteredAddress();
            int n = a.length();
            if (n < addressLength) setEnteredAddress(a + c);
        }
    }

    boolean stargateIsIdle() {
        SGBaseTE te = getStargateTE();
        return (te != null && te.state == SGState.Idle);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
        SGBaseTE te = getStargateTE();
        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_ALPHA_TEST);
        drawBackgroundImage();

        if (te != null) {
            if (te.state == SGState.Idle) {
                drawEnteredSymbols();
                drawEnteredString();
            }
        }
        glPopAttrib();
    }

    void drawBackgroundImage() {
        bindTexture(background);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        drawTexturedRect(
                (double) (width - dhdWidth) / 2,
                (double) (height - dhdHeight) / 2,
                dhdWidth,
                dhdHeight,
                0,
                0,
                (double) dhdWidth / 256,
                (double) dhdHeight / 256);

    }

    public boolean getConnected() {
        SGBaseTE te = getStargateTE();
        return te != null && te.isActive();
    }

    void drawEnteredSymbols() {
        drawAddressSymbols(centreX, 1, getEnteredAddress());
    }

    void drawEnteredString() {
        String address = SGAddressing.padAddress(getEnteredAddress(), "|", addressLength);
        drawAddressString(centreX, height - 30, address);
    }
}
