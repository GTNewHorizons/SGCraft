package gcewing.sg.utils;

import net.minecraft.util.EnumFacing;

import org.joml.Vector3d;

import com.google.common.base.Objects;

public class Vec3i implements Comparable<Vec3i> {

    // Workaround for EnumFacing.getDirectionVec being client-side only
    public static Vec3i[] directionVec = { new Vec3i(0, -1, 0), new Vec3i(0, 1, 0), new Vec3i(0, 0, -1),
            new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(1, 0, 0) };

    public static Vector3d getDirectionVec(EnumFacing f) {
        return new Vector3d(directionVec[f.ordinal()].x, directionVec[f.ordinal()].y, directionVec[f.ordinal()].z);
    }

    /** X coordinate */
    private final int x;
    /** Y coordinate */
    private final int y;
    /** Z coordinate */
    private final int z;

    public Vec3i(int xIn, int yIn, int zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof Vec3i)) {
            return false;
        } else {
            Vec3i vec3i = (Vec3i) p_equals_1_;
            return this.x == vec3i.x && (this.y == vec3i.y && this.z == vec3i.z);
        }
    }

    public int hashCode() {
        return (this.y + this.z * 31) * 31 + this.x;
    }

    public int compareTo(Vec3i p_compareTo_1_) {
        return this.y == p_compareTo_1_.y
                ? (this.z == p_compareTo_1_.z ? this.x - p_compareTo_1_.x : this.z - p_compareTo_1_.z)
                : this.y - p_compareTo_1_.y;
    }

    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x).add("y", this.y).add("z", this.z).toString();
    }
}
