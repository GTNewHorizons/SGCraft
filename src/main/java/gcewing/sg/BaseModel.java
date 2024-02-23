// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.8 - Model
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import com.google.gson.Gson;

import gcewing.sg.interfaces.IModel;
import gcewing.sg.interfaces.IRenderTarget;
import gcewing.sg.interfaces.ITexture;

public class BaseModel implements IModel {

    private Vector3 p = new Vector3(0, 0, 0);
    private Vector3 n = new Vector3(0, 0, 0);

    public double[] bounds;
    public Face[] faces;
    public double[][] boxes;

    public static class Face {

        int texture;
        double[][] vertices;
        int[][] triangles;
        // Vector3 centroid;
        Vector3 normal;
    }

    static Gson gson = new Gson();

    public static BaseModel fromResource(ResourceLocation location) {
        // Can't use resource manager because this needs to work on the server
        String path = String.format("/assets/%s/%s", location.getResourceDomain(), location.getResourcePath());
        InputStream in = BaseModel.class.getResourceAsStream(path);
        if (in == null) throw new RuntimeException(String.format("Cannot find resource %s", path));
        BaseModel model = gson.fromJson(new InputStreamReader(in), BaseModel.class);
        model.prepare();
        return model;
    }

    public AxisAlignedBB getBounds() {
        return AxisAlignedBB.getBoundingBox(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
    }

    void prepare() {
        for (Face face : faces) {
            double[][] p = face.vertices;
            int[] t = face.triangles[0];
            // face.centroid = Vector3.average(p[t[0]], p[t[1]], p[t[2]]);
            face.normal = Vector3.unit(Vector3.sub(p[t[1]], p[t[0]]).cross(Vector3.sub(p[t[2]], p[t[0]])));
        }
    }

    public void addBoxesToList(Trans3 t, List list) {
        if (boxes != null && boxes.length > 0) {
            for (int i = 0; i < boxes.length; i++) addBoxToList(boxes[i], t, list);
        } else {
            addBoxToList(bounds, t, list);
        }
    }

    protected void addBoxToList(double[] b, Trans3 t, List list) {
        t.addBox(b[0], b[1], b[2], b[3], b[4], b[5], list);
    }

    public void render(Trans3 t, IRenderTarget renderer, ITexture... textures) {
        for (Face face : faces) {
            int k = face.texture;
            if (k >= textures.length) k = textures.length - 1;
            ITexture tex = textures[k];
            if (tex == null) {
                continue;
            }

            renderer.setTexture(tex);
            for (int[] tri : face.triangles) {
                renderer.beginTriangle();
                for (int i = 0; i < 3; i++) {
                    int j = tri[i];
                    double[] c = face.vertices[j];
                    t.p(c[0], c[1], c[2], this.p);
                    t.v(c[3], c[4], c[5], this.n);
                    renderer.setNormal(this.n);
                    renderer.addVertex(this.p, c[6], c[7]);
                }
                renderer.endFace();
            }
        }
    }

}
