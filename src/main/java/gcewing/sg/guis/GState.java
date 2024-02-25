package gcewing.sg.guis;

import static gcewing.sg.guis.BaseGui.defaultTextColor;

import net.minecraft.util.ResourceLocation;

public class GState {

    public GState previous;
    public double uscale, vscale;
    public float red, green, blue;
    public int textColor;
    public boolean textShadow;
    public ResourceLocation texture;

    public GState() {
        uscale = 1;
        vscale = 1;
        red = green = blue = 1;
        textColor = defaultTextColor;
        textShadow = false;
    }
}
