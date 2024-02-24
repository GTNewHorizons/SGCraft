package gcewing.sg.interfaces;

import net.minecraft.util.EnumFacing;
import org.joml.Vector3d;

public interface IRenderTarget {


    boolean isRenderingBreakEffects();

    void setTexture(ITexture texture);

    void setColor(double r, double g, double b, double a);

    void setNormal(Vector3d n);

    void beginTriangle();

    void beginQuad();

    void addVertex(Vector3d p, double u, double v);

    void addProjectedVertex(Vector3d p, EnumFacing face);

    void endFace();
}
