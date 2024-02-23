// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Utilities
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapStorage;

import org.joml.Vector3i;

public class BaseUtils {

    public static EnumFacing[] facings = EnumFacing.values();
    public static EnumFacing[] horizontalFacings = { EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH,
            EnumFacing.EAST };

    public static int ifloor(double x) {
        return (int) Math.floor(x);
    }

    public static int iround(double x) {
        return (int) Math.round(x);
    }

    public static Object[] arrayOf(Collection c) {
        int n = c.size();
        Object[] result = new Object[n];
        int i = 0;
        for (Object item : c) result[i++] = item;
        return result;
    }

    public static Class classForName(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getFieldDef(Class cls, String unobfName, String obfName) {
        try {
            Field field;
            try {
                field = cls.getDeclaredField(unobfName);
            } catch (NoSuchFieldException e) {
                field = cls.getDeclaredField(obfName);
            }
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Cannot find field %s or %s of %s", unobfName, obfName, cls.getName()),
                    e);
        }
    }

    public static Object getField(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int getIntField(Object obj, Field field) {
        try {
            return field.getInt(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Object obj, String unobfName, String obfName, Object value) {
        Field field = getFieldDef(obj.getClass(), unobfName, obfName);
        setField(obj, field, value);
    }

    public static void setField(Object obj, Field field, Object value) {
        try {
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setIntField(Object obj, Field field, int value) {
        try {
            field.setInt(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMethodDef(Class cls, String unobfName, String obfName, Class... params) {
        try {
            Method meth;
            try {
                meth = cls.getDeclaredMethod(unobfName, params);
            } catch (NoSuchMethodException e) {
                meth = cls.getDeclaredMethod(obfName, params);
            }
            meth.setAccessible(true);
            return meth;
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Cannot find method %s or %s of %s", unobfName, obfName, cls.getName()),
                    e);
        }
    }

    public static Object invokeMethod(Object target, Method meth, Object... args) {
        try {
            return meth.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int packedColor(double red, double green, double blue) {
        return ((int) (red * 255) << 16) | ((int) (green * 255) << 8) | (int) (blue * 255);
    }

    public static int turnToFaceEast(EnumFacing f) {
        switch (f) {
            case SOUTH:
                return 1;
            case WEST:
                return 2;
            case NORTH:
                return 3;
            default:
                return 0;
        }
    }

    public static Vector3i readBlockPos(DataInput data) {
        try {
            int x = data.readInt();
            int y = data.readInt();
            int z = data.readInt();
            return new Vector3i(x, y, z);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBlockPos(DataOutput data, Vector3i pos) {
        try {
            data.writeInt(pos.x);
            data.writeInt(pos.y);
            data.writeInt(pos.z);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getWorldDimensionId(World world) {
        return world.provider.dimensionId;
    }

    public static EnumDifficulty getWorldDifficulty(World world) {
        return world.difficultySetting;
    }

    public static World getChunkWorld(Chunk chunk) {
        return chunk.worldObj;
    }

    public static Map getChunkTileEntityMap(Chunk chunk) {
        return chunk.chunkTileEntityMap;
    }

    public static AxisAlignedBB newAxisAlignedBB(double x0, double y0, double z0, double x1, double y1, double z1) {
        return AxisAlignedBB.getBoundingBox(x0, y0, z0, x1, y1, z1);
    }

    public static boolean getGameRuleBoolean(GameRules gr, String name) {
        return gr.getGameRuleBooleanValue(name);
    }

    public static void scmPreparePlayer(ServerConfigurationManager scm, EntityPlayerMP player, WorldServer world) {
        scm.func_72375_a(player, world);
    }

    public static EnumFacing oppositeFacing(EnumFacing dir) {
        return facings[dir.ordinal() ^ 1];
    }

    public static MovingObjectPosition newMovingObjectPosition(Vec3 hitVec, int sideHit, Vector3i pos) {
        return new MovingObjectPosition(pos.x, pos.y, pos.z, sideHit, hitVec, true);
    }

    public static MinecraftServer getMinecraftServer() {
        return MinecraftServer.getServer();
    }

    public static WorldServer getWorldForDimension(int id) {
        return getMinecraftServer().worldServerForDimension(id);
    }

    public static <T extends WorldSavedData> T getWorldData(World world, Class<T> cls, String name) {
        MapStorage storage = world.perWorldStorage;
        T result = (T) storage.loadData(cls, name);
        if (result == null) {
            try {
                result = cls.getConstructor(String.class).newInstance(name);
                storage.setData(name, result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
