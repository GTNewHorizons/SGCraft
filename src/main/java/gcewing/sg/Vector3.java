// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - 3D Vector
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static java.lang.Math.abs;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.joml.Vector3i;

public class Vector3 {

    public static Vector3 zero = new Vector3(0, 0, 0);
    public static Vector3 blockCenter = new Vector3(0.5, 0.5, 0.5);

    public static Vector3 blockCenter(double x, double y, double z) {
        return blockCenter.add(new Vector3(x, y, z));
    }

    public static Vector3 blockCenter(Vector3i pos) {
        return blockCenter.add(new Vector3(pos.x, pos.y, pos.z));
    }

    double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 v) {
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vector3 sub(Vector3 v) {
        return new Vector3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public Vector3 mul(double c) {
        return new Vector3(c * x, c * y, c * z);
    }

    public double dot(Vector3 v){
        return this.x*v.x + this.y*v.y + z* v.z;
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

    public static Vector3 floorVector(Vector3 v){
        return new Vector3((int) Math.floor(v.x), (int) Math.floor(v.y), (int) Math.floor(v.z));
    }

    //todo: remove this method
    public Vector3i blockPos() {
        Vector3 floored = Vector3.floorVector(this);
        return new Vector3i((int) floored.x, (int) floored.y, (int) floored.z);
    }

    // Normals at 45 degrees are biased towards UP or DOWN.
    // In 1.8 this is important for item lighting in inventory to work well.

    public static EnumFacing facing(Vector3 v) {
        double dx = v.x;
        double dy= v.y;
        double dz=v.z;
        double ax = abs(dx), ay = abs(dy), az = abs(dz);
        if (ay >= ax && ay >= az) return dy < 0 ? EnumFacing.DOWN : EnumFacing.UP;
        else if (ax >= az) return dx > 0 ? EnumFacing.WEST : EnumFacing.EAST; // E/W are swapped between 1.7 and 1.8
        return dz < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
    }
}
