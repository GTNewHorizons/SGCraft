// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base for 1.7 Version B - Block Utilities
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg;

import static gcewing.sg.BaseUtils.facings;
import static gcewing.sg.BaseUtils.oppositeFacing;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.joml.Vector3i;

public class BaseBlockUtils {

    public static String getNameForBlock(Block block) {
        if (block != null) return Block.blockRegistry.getNameForObject(block).toString();
        else return "";
    }

    /*
     * Test whether a block is receiving a redstone signal from a source other than itself. For blocks that can both
     * send and receive in any direction.
     */
    public static boolean blockIsGettingExternallyPowered(World world, Vector3i pos) {
        for (EnumFacing side : facings) {
            if (isPoweringSide(world, new Vector3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ()).add(pos), side)) return true;
        }
        return false;
    }

    static boolean isPoweringSide(World world, Vector3i pos, EnumFacing side) {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        if (block.isProvidingWeakPower(world, pos.x, pos.y, pos.z, side.ordinal()) > 0) return true;
        if (block.shouldCheckWeakPower(world, pos.x, pos.y, pos.z, side.ordinal())) {
            for (EnumFacing side2 : facings) if (side2 != oppositeFacing(side)) {
                Vector3i pos2 = new Vector3i(side2.getFrontOffsetX(), side2.getFrontOffsetY(), side2.getFrontOffsetZ()).add(pos);
                Block block2 = world.getBlock(pos2.x, pos2.y, pos2.z);
                if (block2.isProvidingStrongPower(world, pos2.x, pos2.y, pos2.z, side2.ordinal()) > 0) return true;
            }
        }
        return false;
    }

    public static IBlockState getBlockStateFromItemStack(ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        int meta = 0;
        if (stack.getItem().getHasSubtypes()) meta = stack.getItem().getMetadata(stack.getItemDamage());
        if (block instanceof BaseBlock) return ((BaseBlock) block).getStateFromMeta(meta);
        else return new MetaBlockState(block, meta);
    }

    public static IBlockState getBlockStateFromMeta(Block block, int meta) {
        if (block instanceof BaseBlock) return ((BaseBlock) block).getStateFromMeta(meta);
        else return new MetaBlockState(block, meta);
    }

    public static int getMetaFromBlockState(IBlockState state) {
        if (state instanceof MetaBlockState) return ((MetaBlockState) state).meta;
        else return ((BaseBlock) state.getBlock()).getMetaFromState(state);
    }

    public static Block getWorldBlock(IBlockAccess world, Vector3i pos) {
        return world.getBlock(pos.x, pos.y, pos.z);
    }

    public static IBlockState getWorldBlockState(IBlockAccess world, Vector3i pos) {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);
        if (block instanceof BaseBlock) return ((BaseBlock) block).getStateFromMeta(meta);
        else return new MetaBlockState(block, meta);
    }

    public static void markWorldBlockForUpdate(World world, Vector3i pos) {
        world.markBlockForUpdate(pos.x, pos.y, pos.z);
    }

    public static void notifyWorldNeighborsOfStateChange(World world, Vector3i pos, Block block) {
        world.notifyBlocksOfNeighborChange(pos.x, pos.y, pos.z, block);
    }

    public static TileEntity getWorldTileEntity(IBlockAccess world, Vector3i pos) {
        return world.getTileEntity(pos.x, pos.y, pos.z);
    }

    public static World getTileEntityWorld(TileEntity te) {
        return te.getWorldObj();
    }

    public static Vector3i getTileEntityPos(TileEntity te) {
        return new Vector3i(te.xCoord, te.yCoord, te.zCoord);
    }

    public static boolean blockCanRenderInLayer(Block block, EnumWorldBlockLayer layer) {
        if (block instanceof BaseBlock) return ((BaseBlock) block).canRenderInLayer(layer);
        else switch (layer) {
            case SOLID:
                return block.canRenderInPass(0);
            case TRANSLUCENT:
                return block.canRenderInPass(1);
            default:
                return false;
        }
    }

    // ------------------------------------------------------------------------------------------------

    protected static class MetaBlockState implements IBlockState {

        protected Block block;
        public int meta;

        public MetaBlockState(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        public Collection<IProperty> getPropertyNames() {
            return null;
        }

        public <T extends Comparable<T>> T getValue(IProperty<T> property) {
            return null;
        }

        public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
            return null;
        }

        public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
            return null;
        }

        public ImmutableMap<IProperty, Comparable> getProperties() {
            return null;
        }

        public Block getBlock() {
            return block;
        }

    }

}
