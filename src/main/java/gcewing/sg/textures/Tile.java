package gcewing.sg.textures;

public class Tile extends Proxy {

    protected double u0, v0, uSize, vSize;

    public Tile(TileSet base, int row, int col) {
        super(base);
        uSize = base.tileSizeU;
        vSize = base.tileSizeV;
        u0 = uSize * col;
        v0 = vSize * row;
    }

    @Override
    public double interpolateU(double u) {
        return super.interpolateU(u0 + u * uSize);
    }

    @Override
    public double interpolateV(double v) {
        return super.interpolateV(v0 + v * vSize);
    }

}
