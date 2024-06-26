// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Rendering target base class
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.renderers;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;

import org.joml.Vector3d;

import gcewing.sg.interfaces.IRenderTarget;
import gcewing.sg.interfaces.ITexture;
import gcewing.sg.textures.BaseTexture;
import gcewing.sg.utils.Trans3;

public abstract class BaseRenderTarget implements IRenderTarget {

    // Position of block in rendering coordinates (may be different from world coordinates)
    protected double blockX, blockY, blockZ;

    protected int verticesPerFace;
    protected int vertexCount;
    protected ITexture texture;
    protected Vector3d normal = new Vector3d();
    protected EnumFacing face;
    protected float red = 1, green = 1, blue = 1, alpha = 1;
    protected float shade;
    protected boolean expandTrianglesToQuads;
    protected boolean textureOverride;

    public BaseRenderTarget(double x, double y, double z, IIcon overrideIcon) {
        blockX = x;
        blockY = y;
        blockZ = z;
        if (overrideIcon != null) {
            texture = BaseTexture.fromSprite(overrideIcon);
            textureOverride = true;
        }
    }

    // ---------------------------- IRenderTarget ----------------------------

    public boolean isRenderingBreakEffects() {
        return textureOverride;
    }

    public void beginTriangle() {
        setMode(3);
    }

    protected void setMode(int mode) {
        if (vertexCount != 0) throw new IllegalStateException("Changing mode in mid-face");
        verticesPerFace = mode;
    }

    public void setTexture(ITexture texture) {
        if (!textureOverride) {
            if (texture == null) throw new IllegalArgumentException("Setting null texture");
            this.texture = texture;
        }
    }

    public void setNormal(Vector3d n) {
        normal.set(n);
        face = Trans3.facing(n);
        shade = (float) (0.6 * n.x * n.x + 0.8 * n.z * n.z + (n.y > 0 ? 1 : 0.5) * n.y * n.y);
    }

    public void addVertex(Vector3d p, double u, double v) {
        if (texture.isProjected()) addProjectedVertex(p, face);
        else addUVVertex(p, u, v);
    }

    public void addUVVertex(Vector3d p, double u, double v) {
        double iu, iv;
        if (verticesPerFace == 0) throw new IllegalStateException("No face active");
        if (vertexCount >= verticesPerFace) throw new IllegalStateException("Too many vertices in face");
        if (normal == null) throw new IllegalStateException("No normal");
        if (texture == null) throw new IllegalStateException("No texture");
        iu = texture.interpolateU(u);
        iv = texture.interpolateV(v);
        rawAddVertex(p, iu, iv);
        if (++vertexCount == 3 && expandTrianglesToQuads && verticesPerFace == 3) {
            rawAddVertex(p, iu, iv);
        }
    }

    public void endFace() {
        if (vertexCount < verticesPerFace) {
            throw new IllegalStateException("Too few vertices in face");
        }
        vertexCount = 0;
        verticesPerFace = 0;
    }

    public void finish() {
        if (vertexCount > 0) throw new IllegalStateException("Rendering ended with incomplete face");
    }

    protected abstract void rawAddVertex(Vector3d p, double u, double v);

    public float r() {
        return (float) (red * texture.red());
    }

    public float g() {
        return (float) (green * texture.green());
    }

    public float b() {
        return (float) (blue * texture.blue());
    }

    public float a() {
        return (float) alpha;
    }

    // Add vertex with texture coords projected from the given direction
    public void addProjectedVertex(Vector3d p, EnumFacing face) {
        double x = p.x - blockX;
        double y = p.y - blockY;
        double z = p.z - blockZ;
        double u, v;
        switch (face) {
            case DOWN:
                u = x;
                v = 1 - z;
                break;
            case UP:
                u = x;
                v = z;
                break;
            case NORTH:
                u = 1 - x;
                v = 1 - y;
                break;
            case SOUTH:
                u = x;
                v = 1 - y;
                break;
            case WEST:
                u = 1 - z;
                v = 1 - y;
                break;
            case EAST:
                u = z;
                v = 1 - y;
                break;
            default:
                u = 0;
                v = 0;
        }
        addUVVertex(p, u, v);
    }

}
