package gcewing.sg.interfaces;

import gcewing.sg.utils.Vector3;
import net.minecraft.util.EnumFacing;

public interface IRenderTarget {
    boolean isRenderingBreakEffects();

    void setTexture(ITexture texture);

    void setColor(double r, double g, double b, double a);

    void setNormal(Vector3 n);

    void beginTriangle();

    void beginQuad();

    void addVertex(Vector3 p, double u, double v);

    void addProjectedVertex(Vector3 p, EnumFacing face);

    void endFace();
}
