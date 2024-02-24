// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.8 - OpenGL Utilities
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.nio.FloatBuffer;

import net.minecraft.client.renderer.GLAllocation;

import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

class BaseGLUtils {

    public static void glMultMatrix(Trans3 t) {
        FloatBuffer b = GLAllocation.createDirectFloatBuffer(16);
        Vector3d v = t.offset;
        Matrix3d m = t.rotation;

        b.put(0, (float) m.m00);
        b.put(1, (float) m.m10);
        b.put(2, (float) m.m20);
        b.put(3, 0);

        b.put(4, (float) m.m01);
        b.put(5, (float) m.m11);
        b.put(6, (float) m.m21);
        b.put(7, 0);

        b.put(8, (float) m.m02);
        b.put(9, (float) m.m12);
        b.put(10, (float) m.m22);
        b.put(11, 0);

        b.put(12, (float) v.x);
        b.put(13, (float) v.y);
        b.put(14, (float) v.z);
        b.put(15, 1);

        GL11.glMultMatrix(b);
    }

}
