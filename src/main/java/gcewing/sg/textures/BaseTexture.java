// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.8 - Texture
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.textures;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import gcewing.sg.interfaces.ITexture;
import gcewing.sg.interfaces.ITiledTexture;

public abstract class BaseTexture implements ITexture {

    public ResourceLocation location;
    public int tintIndex;
    public double red = 1, green = 1, blue = 1;
    public boolean isEmissive;
    public boolean isProjected;

    public int tintIndex() {
        return tintIndex;
    }

    public double red() {
        return red;
    }

    public double green() {
        return green;
    }

    public double blue() {
        return blue;
    }

    public boolean isEmissive() {
        return isEmissive;
    }

    public boolean isProjected() {
        return isProjected;
    }

    public boolean isSolid() {
        return false;
    }

    public static Sprite fromSprite(IIcon icon) {
        return new Sprite(icon);
    }

    public ResourceLocation location() {
        return location;
    }

    public ITexture colored(double red, double green, double blue) {
        BaseTexture result = new Proxy(this);
        result.red = red;
        result.green = green;
        result.blue = blue;
        return result;
    }

    public ITexture emissive() {
        BaseTexture result = new Proxy(this);
        result.isEmissive = true;
        return result;
    }

    public ITexture projected() {
        BaseTexture result = new Proxy(this);
        result.isProjected = true;
        return result;
    }

    public ITiledTexture tiled(int numRows, int numCols) {
        return new TileSet(this, numRows, numCols);
    }
}
