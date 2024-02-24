// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base - Generic Tile Entity Renderer
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.utils.Trans3;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import gcewing.sg.BaseModClient.IRenderTarget;
import org.joml.Vector3d;

public abstract class BaseTileEntityRenderer extends TileEntitySpecialRenderer {

    protected static BaseGLRenderTarget target = new BaseGLRenderTarget();

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float dt) {
        renderTileEntityAt(te, x, y, z, dt, -1);
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float dt, int destroyStage) {
        BaseTileEntity bte = (BaseTileEntity) te;
        Trans3 t = bte.localToGlobalTransformation(new Vector3d(0.5+x, 0.5+y, 0.5+z));
        target.start(true);
        render(bte, dt, destroyStage, t, target);
        target.finish();
    }

    public void render(BaseTileEntity te, float dt, int destroyStage, Trans3 t, IRenderTarget target) {}

}
