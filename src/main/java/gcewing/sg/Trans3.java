// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - 3D Transformation
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class Trans3 {

    public static Matrix3d[] turnRotations = {
            new Matrix3d(),
            new Matrix3d().rotation(PI/2,0, 1, 0),
            new Matrix3d().rotation(PI, 0, 1, 0),
            new Matrix3d().rotation(3*PI/2, 0, 1, 0)
    };

    public static Matrix3d[] sideRotations = {
            /* 0, -Y, DOWN */ new Matrix3d(),
            /* 1, +Y, UP */ new Matrix3d().rotation(PI, 1, 0, 0),
            /* 2, -Z, NORTH */ new Matrix3d().rotation(PI/2, 1, 0, 0),
            /* 3, +Z, SOUTH */ new Matrix3d().rotation(-PI/2, 1, 0, 0).rotation(PI,0, 1, 0),
            /* 4, -X, WEST */ new Matrix3d().rotation(-PI/2, 0, 0, 1).rotation(PI/2,0, 1, 0),
            /* 5, +X, EAST */ new Matrix3d().rotation(PI/2, 0, 0, 1).rotation(-PI/2,0, 1, 0)
    };

    public static Matrix3d[][] sideTurnRotations = new Matrix3d[6][4];
    static {
        for (int side = 0; side < 6; side++) for (int turn = 0; turn < 4; turn++)
            sideTurnRotations[side][turn] = new Matrix3d(sideRotations[side]).mul(turnRotations[turn]);
    }

    public static Trans3 blockCenter = new Trans3(Vector3.blockCenter);


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
        return new Trans3(v, sideTurnRotations[side][turn]);
    }

    public Vector3 offset;
    public Matrix3d rotation;
    public double scaling;

    public Trans3(Vector3 v) {
        this(v, new Matrix3d());
    }

    public Trans3(Vector3 v, Matrix3d m) {
        this(v, m, 1.0);
    }

    public Trans3(Vector3 v, Matrix3d m, double s) {
        offset = v;
        rotation = m;
        scaling = s;
    }

    public Trans3(double dx, double dy, double dz) {
        this(new Vector3(dx, dy, dz));
    }

    public Trans3(Vector3i pos) {
        this(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
    }

    public Trans3 translate(Vector3 v) {
        if (v == Vector3.zero) return this;
        else return translate(v.x, v.y, v.z);
    }

    public Trans3 translate(double dx, double dy, double dz) {
        Vector3d computation = new Vector3d(dx, dy, dz).mul(scaling).mul(rotation).add(offset.x, offset.y, offset.z);
        return new Trans3(new Vector3(computation.x, computation.y, computation.z), rotation, scaling);
    }

    public Trans3 rotate(Matrix3d m) {
        return new Trans3(offset, new Matrix3d(rotation).mul(m), scaling);
    }


    public Trans3 scale(double s) {
        return new Trans3(offset, rotation, scaling * s);
    }

    public Trans3 side(EnumFacing dir) {
        return side(dir.ordinal());
    }

    public Trans3 side(int i) {
        return rotate(sideRotations[i]);
    }

    public Trans3 turn(int i) {
        return rotate(turnRotations[i]);
    }

    public Trans3 t(Trans3 t) {
        Vector3d computation = new Vector3d(t.offset.x, t.offset.y, t.offset.z).mul(rotation).mul(scaling).add(offset.x, offset.y, offset.z);
        return new Trans3(
                new Vector3(computation.x, computation.y, computation.z),
                new Matrix3d(rotation).mul(t.rotation),
                scaling * t.scaling);
    }

    public void p(double x, double y, double z, Vector3 result) {
        // return offset.add(rotation.mul(u.mul(scaling)));
        Vector3d computation = new Vector3d(x, y, z).mul(scaling).mul(rotation).add(offset.x, offset.y, offset.z);
        result.x = computation.x;
        result.y = computation.y;
        result.z = computation.z;
    }

    public Vector3 p(Vector3 u) {
        Vector3d computation = new Vector3d(u.x, u.y, u.z).mul(scaling).mul(rotation).add(offset.x, offset.y, offset.z);
        return new Vector3(computation.x, computation.y, computation.z);
    }

    public Vector3 ip(Vector3 u) {
        Vector3d computation = new Vector3d(u.x, u.y, u.z).sub(offset.x, offset.y, offset.z).mul(new Matrix3d(rotation).transpose()).mul(1.0/scaling);
        return  new Vector3(computation.x, computation.y, computation.z);
    }

    public void v(double x, double y, double z, Vector3 result) {
        //return rotation.mul(u.mul(scaling));
        Vector3d computation = new Vector3d(x, y, z).mul(scaling).mul(rotation);
        result.x = computation.x;
        result.y = computation.y;
        result.z = computation.z;
    }

    public Vector3 v(Vector3 u) {
        Vector3d computation = new Vector3d(u.x, u.y, u.z).mul(scaling).mul(rotation);
        return new Vector3(computation.x, computation.y, computation.z);
    }

    public Vector3 iv(Vector3 u) {
        Vector3d computation = new Vector3d(u.x, u.y, u.z).mul(new Matrix3d(rotation).transpose()).mul(1.0/scaling);
        return new Vector3(computation.x, computation.y, computation.z);
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
