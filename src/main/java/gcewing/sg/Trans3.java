// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - 3D Transformation
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import org.joml.Vector3i;

public class Trans3 {

    public static Trans3 blockCenter = new Trans3(Vector3.blockCenter);

    public static Trans3[][] sideTurnRotations = new Trans3[6][4];
    static {
        for (int side = 0; side < 6; side++) for (int turn = 0; turn < 4; turn++)
            sideTurnRotations[side][turn] = new Trans3(Vector3.zero, Matrix3.sideTurnRotations[side][turn]);
    }

    public static Trans3 blockCenter(Vector3i pos) {
        return new Trans3(Vector3.blockCenter.add(new Vector3(pos.x, pos.y, pos.z)));
    }

    public static Vector3i intVector(Vector3 v) {
        return new Vector3i((int) Math.floor(v.x), (int) Math.floor(v.y), (int) Math.floor(v.z));
    }

    public static Trans3 blockCenterSideTurn(int side, int turn) {
        return sideTurn(Vector3.blockCenter, side, turn);
    }

    public static Trans3 sideTurn(Vector3 v, int side, int turn) {
        return new Trans3(v, Matrix3.sideTurnRotations[side][turn]);
    }

    public Vector3 offset;
    public Matrix3 rotation;
    public double scaling;

    public Trans3(Vector3 v) {
        this(v, Matrix3.ident);
    }

    public Trans3(Vector3 v, Matrix3 m) {
        this(v, m, 1.0);
    }

    public Trans3(Vector3 v, Matrix3 m, double s) {
        offset = v;
        rotation = m;
        scaling = s;
    }

    public Trans3(double dx, double dy, double dz) {
        this(new Vector3(dx, dy, dz), Matrix3.ident, 1.0);
    }

    public Trans3(Vector3i pos) {
        this(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
    }

    public Trans3 translate(Vector3 v) {
        if (v == Vector3.zero) return this;
        else return translate(v.x, v.y, v.z);
    }

    public Trans3 translate(double dx, double dy, double dz) {
        return new Trans3(offset.add(rotation.mul(dx * scaling, dy * scaling, dz * scaling)), rotation, scaling);
    }

    public Trans3 rotate(Matrix3 m) {
        return new Trans3(offset, rotation.mul(m), scaling);
    }


    public Trans3 scale(double s) {
        return new Trans3(offset, rotation, scaling * s);
    }

    public Trans3 side(EnumFacing dir) {
        return side(dir.ordinal());
    }

    public Trans3 side(int i) {
        return rotate(Matrix3.sideRotations[i]);
    }

    public Trans3 turn(int i) {
        return rotate(Matrix3.turnRotations[i]);
    }

    public Trans3 t(Trans3 t) {
        return new Trans3(
                offset.add(rotation.mul(t.offset).mul(scaling)),
                rotation.mul(t.rotation),
                scaling * t.scaling);
    }

    public void p(double x, double y, double z, Vector3 result) {
        result.x = x * scaling * rotation.m[0][0] + y * scaling * rotation.m[0][1]
                + z * scaling * rotation.m[0][2]
                + offset.x;
        result.y = x * scaling * rotation.m[1][0] + y * scaling * rotation.m[1][1]
                + z * scaling * rotation.m[1][2]
                + offset.y;
        result.z = x * scaling * rotation.m[2][0] + y * scaling * rotation.m[2][1]
                + z * scaling * rotation.m[2][2]
                + offset.z;
    }

    public Vector3 p(Vector3 u) {
        return offset.add(rotation.mul(u.mul(scaling)));
    }

    public Vector3 ip(Vector3 u) {
        return rotation.imul(u.sub(offset)).mul(1.0 / scaling);
    }

    public void v(double x, double y, double z, Vector3 result) {
        result.x = x * scaling * rotation.m[0][0] + y * scaling * rotation.m[0][1] + z * scaling * rotation.m[0][2];
        result.y = x * scaling * rotation.m[1][0] + y * scaling * rotation.m[1][1] + z * scaling * rotation.m[1][2];
        result.z = x * scaling * rotation.m[2][0] + y * scaling * rotation.m[2][1] + z * scaling * rotation.m[2][2];
    }

    public Vector3 v(Vector3 u) {
        return rotation.mul(u.mul(scaling));
    }

    public Vector3 iv(Vector3 u) {
        return rotation.imul(u).mul(1.0 / scaling);
    }


    public AxisAlignedBB t(AxisAlignedBB box) {
        return boxEnclosing(p(new Vector3(box.minX, box.minY, box.minZ)), p(new Vector3(box.maxX, box.maxY, box.maxZ)));
    }

    public AxisAlignedBB box(Vector3 p0, Vector3 p1) {
        return boxEnclosing(p(p0), p(p1));
    }


    public static AxisAlignedBB boxEnclosing(Vector3 p, Vector3 q) {
        return AxisAlignedBB.getBoundingBox(
                min(p.x, q.x),
                min(p.y, q.y),
                min(p.z, q.z),
                max(p.x, q.x),
                max(p.y, q.y),
                max(p.z, q.z));
    }

    public EnumFacing t(EnumFacing f) {
        Vector3 vectorFromFacing = Vec3i.getDirectionVec(f);
        return Vector3.facing(v(vectorFromFacing));
    }

    public EnumFacing it(EnumFacing f) {
        Vector3 vectorFromFacing = Vec3i.getDirectionVec(f);
        return Vector3.facing(iv(vectorFromFacing));
    }


    public void addBox(double x0, double y0, double z0, double x1, double y1, double z1, List list) {
        AxisAlignedBB box = boxEnclosing(p(new Vector3(x0, y0, z0)), p(new Vector3(x1, y1, z1)));
        list.add(box);
    }

}
