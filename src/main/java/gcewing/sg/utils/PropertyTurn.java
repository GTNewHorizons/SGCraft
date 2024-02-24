package gcewing.sg.utils;

import net.minecraft.util.EnumFacing;

import java.util.Arrays;
import java.util.Collection;

public class PropertyTurn extends PropertyEnum<EnumFacing> {

    protected static EnumFacing[] values = { EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.EAST };
    protected static Collection valueList = Arrays.asList(values);

    public PropertyTurn(String name) {
        super(name, EnumFacing.class, valueList);
    }

}
