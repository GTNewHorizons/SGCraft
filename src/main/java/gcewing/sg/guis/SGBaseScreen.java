// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate base gui screen
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.joml.Vector3i;

import gcewing.sg.SGAddressing;
import gcewing.sg.SGCraft;
import gcewing.sg.guis.containers.SGBaseContainer;
import gcewing.sg.tileentities.SGBaseTE;

public class SGBaseScreen extends SGScreen {

    static String screenTitle = "Stargate Address";
    static final int guiWidth = 256;
    static final int guiHeight = 208; // 92;

    SGBaseTE te;
    String address;
    String formattedAddress;
    boolean addressValid;

    public static SGBaseScreen create(EntityPlayer player, World world, Vector3i pos) {
        SGBaseTE te = SGBaseTE.at(world, pos);
        if (te != null) return new SGBaseScreen(player, te);
        else return null;
    }

    public SGBaseScreen(EntityPlayer player, SGBaseTE te) {
        super(new SGBaseContainer(player, te), guiWidth, guiHeight);
        this.te = te;
        getAddress();
        if (addressValid) setClipboardString(formattedAddress);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void drawBackgroundLayer() {
        bindTexture(SGCraft.mod.resourceLocation("textures/gui/sg_gui.png"), 256, 256);
        drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);

        int cx = xSize / 2;
        if (addressValid) drawAddressSymbols(cx, 22, address);
        setTextColor(0x004c66);
        drawCenteredString(screenTitle, cx, 8);
        drawCenteredString(formattedAddress, cx, 72);
        if (SGBaseTE.numCamouflageSlots > 0) drawCenteredString("Base Camouflage", 92, 92);
    }

    void getAddress() {
        if (te.homeAddress != null) {
            address = te.homeAddress;
            formattedAddress = SGAddressing.formatAddress(address, "-", "-");
            addressValid = true;
        } else {
            address = "";
            formattedAddress = te.addressError;
            addressValid = false;
        }
    }

}
