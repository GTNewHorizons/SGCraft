package gcewing.sg.textures;

import gcewing.sg.interfaces.ITexture;
import gcewing.sg.interfaces.ITiledTexture;

public class TileSet extends Proxy implements ITiledTexture {

    public double tileSizeU, tileSizeV;

    public TileSet(ITexture base, int numRows, int numCols) {
        super(base);
        tileSizeU = 1.0 / numCols;
        tileSizeV = 1.0 / numRows;
    }

    public ITexture tile(int row, int col) {
        return new Tile(this, row, col);
    }

}
