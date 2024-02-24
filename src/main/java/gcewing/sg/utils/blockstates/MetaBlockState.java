package gcewing.sg.utils.blockstates;

import com.google.common.collect.ImmutableMap;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.IProperty;
import net.minecraft.block.Block;

import java.util.Collection;

public class MetaBlockState implements IBlockState {

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
