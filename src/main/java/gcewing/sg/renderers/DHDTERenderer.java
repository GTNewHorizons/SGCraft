// ------------------------------------------------------------------------------------------------
//
// SG Craft - DHD tile entity renderer
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.renderers;

import net.minecraft.util.ResourceLocation;

import gcewing.sg.BaseModel;
import gcewing.sg.BaseTexture.Image;
import gcewing.sg.SGCraft;
import gcewing.sg.interfaces.IModel;
import gcewing.sg.interfaces.IRenderTarget;
import gcewing.sg.interfaces.ITexture;
import gcewing.sg.interfaces.ITiledTexture;
import gcewing.sg.tileentities.BaseTileEntity;
import gcewing.sg.tileentities.DHDTE;
import gcewing.sg.tileentities.SGBaseTE;
import gcewing.sg.utils.Trans3;

public class DHDTERenderer extends BaseTileEntityRenderer {

    IModel model;
    ITexture mainTexture;
    ITexture[] buttonTextures;
    ITexture[] textures;

    final static int buttonTextureIndex = 3;

    public DHDTERenderer() {
        SGCraft mod = SGCraft.mod;
        ResourceLocation ttLoc = mod.textureLocation("tileentity/dhd_top.png");
        ResourceLocation stLoc = mod.textureLocation("tileentity/dhd_side.png");
        ResourceLocation dtLoc = mod.textureLocation("tileentity/dhd_detail.png");
        ITiledTexture detail = new Image(dtLoc).tiled(2, 2);
        textures = new ITexture[] { new Image(ttLoc), new Image(stLoc), detail.tile(1, 1), null, // button texture
                                                                                                 // inserted here
        };
        ITexture button = detail.tile(0, 0);
        buttonTextures = new ITexture[] { button.colored(0.5, 0.5, 0.5), button.colored(0.5, 0.25, 0.0),
                button.colored(1.0, 0.5, 0.0).emissive(), };
        model = BaseModel.fromResource(mod.resourceLocation("models/dhd.smeg"));
        DHDTE.bounds = model.getBounds();
    }

    public void render(BaseTileEntity te, float dt, int destroyStage, Trans3 t, IRenderTarget target) {
        DHDTE dte = (DHDTE) te;
        SGBaseTE gte = dte.getLinkedStargateTE();
        int i;
        if (gte == null) i = 0;
        else if (gte.isActive()) i = 2;
        else i = 1;
        textures[buttonTextureIndex] = buttonTextures[i];
        model.render(t.translate(0, -0.5, 0), target, textures);
    }

}
