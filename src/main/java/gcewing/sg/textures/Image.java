package gcewing.sg.textures;

import net.minecraft.util.ResourceLocation;

public class Image extends BaseTexture {

    public Image(ResourceLocation location) {
        this.location = location;
    }

    public double interpolateU(double u) {
        return u;
    }

    public double interpolateV(double v) {
        return v;
    }

}
