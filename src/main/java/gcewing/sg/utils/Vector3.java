// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - 3D Vector
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.utils;

import static java.lang.Math.abs;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import org.joml.Vector3i;

public class Vector3 {

    public static Vector3 zero = new Vector3(0, 0, 0);
    public static Vector3 blockCenter = new Vector3(0.5, 0.5, 0.5);

    public static Vector3 unitX = new Vector3(1, 0, 0);
    public static Vector3 unitY = new Vector3(0, 1, 0);
    public static Vector3 unitZ = new Vector3(0, 0, 1);

    public static Vector3 unitNX = new Vector3(-1, 0, 0);
    public static Vector3 unitNZ = new Vector3(0, 0, -1);

    public static Vector3 blockCenter(double x, double y, double z) {
        return blockCenter.add(x, y, z);
    }

    public static Vector3 blockCenter(Vector3i pos) {
        return blockCenter.add(new Vector3(pos.x, pos.y, pos.z));
    }

    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vec3 v) {
        this(v.xCoord, v.yCoord, v.zCoord);
    }

    public Vector3(Vector3i pos) {
        this(pos.x, pos.y, pos.z);
    }

    public Vector3(Vec3i v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public Vector3(EnumFacing f) {
        this(getDirectionVec(f));
    }

    public String toString() {
        return String.format("(%.3f,%.3f,%.3f)", x, y, z);
    }

    public Vector3 add(double x, double y, double z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 add(Vector3 v) {
        return add(v.x, v.y, v.z);
    }

    public Vector3 add(Vector3i pos) {
        return add(pos.x, pos.y, pos.z);
    }

    public Vector3 sub(double x, double y, double z) {
        return new Vector3(this.x - x, this.y - y, this.z - z);
    }

    public Vector3 sub(Vector3 v) {
        return sub(v.x, v.y, v.z);
    }

    public static Vector3 sub(double[] u, double[] v) {
        return new Vector3(u[0] - v[0], u[1] - v[1], u[2] - v[2]);
    }

    public Vector3 mul(double c) {
        return new Vector3(c * x, c * y, c * z);
    }

    public double dot(EnumFacing f) {
        Vec3i v = getDirectionVec(f);
        return dot(v.getX(), v.getY(), v.getZ());
    }

    public double dot(double vx, double vy, double vz) {
        return x * vx + y * vy + z * vz;
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public Vector3 min(Vector3 v) {
        return new Vector3(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z));
    }

    public Vector3 max(Vector3 v) {
        return new Vector3(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z));
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(Vector3 v) {
        double dx = x - v.x, dy = y - v.y, dz = z - v.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Vector3 unit(Vector3 v) {
        return v.mul(1 / v.length());
    }

    public int floorX() {
        return (int) Math.floor(x);
    }

    public int floorY() {
        return (int) Math.floor(y);
    }

    public int floorZ() {
        return (int) Math.floor(z);
    }

    public int roundX() {
        return (int) Math.round(x);
    }

    public EnumFacing facing() {
        return facing(x, y, z);
    }

    // todo: remove this method
    public Vector3i blockPos() {
        return new Vector3i(floorX(), floorY(), floorZ());
    }

    // Normals at 45 degrees are biased towards UP or DOWN.
    // In 1.8 this is important for item lighting in inventory to work well.

    public static EnumFacing facing(double dx, double dy, double dz) {
        double ax = abs(dx), ay = abs(dy), az = abs(dz);
        if (ay >= ax && ay >= az) return dy < 0 ? EnumFacing.DOWN : EnumFacing.UP;
        else if (ax >= az) return dx > 0 ? EnumFacing.WEST : EnumFacing.EAST; // E/W are swapped between 1.7 and 1.8
        return dz < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
    }

    public static Vector3[][] faceBases = { { unitX, unitZ }, // DOWN
            { unitX, unitNZ }, // UP
            { unitNX, unitY }, // NORTH
            { unitX, unitY }, // SOUTH
            { unitZ, unitY }, // WEST
            { unitNZ, unitY }, // EAST
    };

    // Workaround for EnumFacing.getDirectionVec being client-side only

    public static Vec3i[] directionVec = { new Vec3i(0, -1, 0), new Vec3i(0, 1, 0), new Vec3i(0, 0, -1),
            new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(1, 0, 0) };

    public static Vec3i getDirectionVec(EnumFacing f) {
        return directionVec[f.ordinal()];
    }

}
