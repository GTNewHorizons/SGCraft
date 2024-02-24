package gcewing.sg.utils.blockstates;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import gcewing.sg.utils.carthesian.Cartesian;
import net.minecraft.block.Block;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import gcewing.sg.MapPopulator;
import gcewing.sg.interfaces.IBlockState;
import gcewing.sg.interfaces.IProperty;

public class BlockState {

    private static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final Function<IProperty, String> GET_NAME_FUNC = new Function<IProperty, String>() {

        public String apply(IProperty p_apply_1_) {
            return p_apply_1_ == null ? "<NULL>" : p_apply_1_.getName();
        }
    };
    private final Block block;
    private final ImmutableList<IProperty> properties;
    private final ImmutableList<IBlockState> validStates;

    protected StateImplementation createState(Block block,
            ImmutableMap<IProperty, Comparable> properties /*
                                                            * , ImmutableMap<IUnlistedProperty<?>,
                                                            * com.google.common.base.Optional<?>> unlistedProperties
                                                            */) {
        return new StateImplementation(block, properties);
    }

    // protected BlockState(Block blockIn, IProperty[] properties, ImmutableMap<IUnlistedProperty<?>,
    // com.google.common.base.Optional<?>> unlistedProperties)
    public BlockState(Block blockIn, IProperty... properties) {
        this.block = blockIn;
        Arrays.sort(properties, new Comparator<IProperty>() {

            public int compare(IProperty p_compare_1_, IProperty p_compare_2_) {
                return p_compare_1_.getName().compareTo(p_compare_2_.getName());
            }
        });
        this.properties = ImmutableList.copyOf(properties);
        Map<Map<IProperty, Comparable>, StateImplementation> map = Maps.<Map<IProperty, Comparable>, StateImplementation>newLinkedHashMap();
        List<StateImplementation> list = Lists.<StateImplementation>newArrayList();

        for (List<Comparable> list1 : Cartesian.cartesianProduct(this.getAllowedValues())) {
            Map<IProperty, Comparable> map1 = MapPopulator.<IProperty, Comparable>createMap(this.properties, list1);
            StateImplementation blockstate$stateimplementation = createState(
                    blockIn,
                    ImmutableMap.copyOf(map1) /* , unlistedProperties */);
            map.put(map1, blockstate$stateimplementation);
            list.add(blockstate$stateimplementation);
        }

        for (StateImplementation blockstate$stateimplementation1 : list) {
            blockstate$stateimplementation1.buildPropertyValueTable(map);
        }

        this.validStates = ImmutableList.<IBlockState>copyOf(list);
    }

    public ImmutableList<IBlockState> getValidStates() {
        return this.validStates;
    }

    private List<Iterable<Comparable>> getAllowedValues() {
        List<Iterable<Comparable>> list = Lists.<Iterable<Comparable>>newArrayList();

        for (int i = 0; i < this.properties.size(); ++i) {
            list.add(((IProperty) this.properties.get(i)).getAllowedValues());
        }

        return list;
    }

    public IBlockState getBaseState() {
        return (IBlockState) this.validStates.get(0);
    }

    public Block getBlock() {
        return this.block;
    }

    public Collection<IProperty> getProperties() {
        return this.properties;
    }

    public String toString() {
        return Objects.toStringHelper(this).add("block", Block.blockRegistry.getNameForObject(this.block))
                .add("properties", Iterables.transform(this.properties, GET_NAME_FUNC)).toString();
    }
}
