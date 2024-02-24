package gcewing.sg.utils.rendering;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class AltBlockAccess implements IBlockAccess {

    IBlockAccess base;
    int targetX, targetY, targetZ;
    int metadata;

    void setup(IBlockAccess base, int x, int y, int z, int data) {
        this.base = base;
        targetX = x;
        targetY = y;
        targetZ = z;
        metadata = data;
    }

    public Block getBlock(int x, int y, int z) {
        return base.getBlock(x, y, z);
    }

    public TileEntity getTileEntity(int x, int y, int z) {
        return base.getTileEntity(x, y, z);
    }

    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int w) {
        return base.getLightBrightnessForSkyBlocks(x, y, z, w);
    }

    public int getBlockMetadata(int x, int y, int z) {
        if (x == targetX && y == targetY && z == targetZ) return metadata;
        else return base.getBlockMetadata(x, y, z);
    }

    public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
        return base.isBlockProvidingPowerTo(x, y, z, side);
    }

    public boolean isAirBlock(int x, int y, int z) {
        return base.isAirBlock(x, y, z);
    }

    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return base.getBiomeGenForCoords(x, z);
    }

    public int getHeight() {
        return base.getHeight();
    }

    public boolean extendedLevelsInChunkCache() {
        return base.extendedLevelsInChunkCache();
    }

    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return base.isSideSolid(x, y, z, side, _default);
    }

}
