//------------------------------------------------------------------------------------------------
//
//   SG Craft - Map feature generation
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;
import java.lang.reflect.Field;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.structure.*;

import net.minecraftforge.event.terraingen.*;

import static gcewing.sg.BaseUtils.*;

public class FeatureGeneration {

    public static boolean augmentStructures = false;
    public static boolean debugStructures = false;
    
    static Field structureMap = getFieldDef(MapGenStructure.class,
        "structureMap", "field_75053_d");
    
    public static void configure(BaseConfiguration config) {
        augmentStructures = config.getBoolean("options", "augmentStructures", augmentStructures);
        debugStructures = config.getBoolean("debug", "debugStructures", debugStructures);
    }

    public static void onInitMapGen(InitMapGenEvent e) {
        if (augmentStructures) {
            switch (e.type) {
                case SCATTERED_FEATURE:
                    if (e.newGen instanceof MapGenStructure)
                        e.newGen = modifyScatteredFeatureGen((MapGenStructure)e.newGen);
                    else
                        System.out.printf("SGCraft: FeatureGeneration: SCATTERED_FEATURE generator is not a MapGenStructure, cannot customise\n");
                    break;
            }
        }
    }

    static MapGenStructure modifyScatteredFeatureGen(MapGenStructure gen) {
        //MapGenStructureAccess.setStructureMap(gen, new SGStructureMap());
        setField(gen, structureMap, new SGStructureMap());
        return gen;
    }

}

class SGStructureMap extends HashMap {

    @Override
    public Object put(Object key, Object value) {
        //System.out.printf("SGCraft: FeatureGeneration: SGStructureMap.put: %s\n", value);
        if (value instanceof StructureStart)
            augmentStructureStart((StructureStart)value);
        return super.put(key, value);
    }
    
    void augmentStructureStart(StructureStart start) {
        LinkedList oldComponents = start.getComponents();
        LinkedList newComponents = new LinkedList();
        for (Object comp : oldComponents) {
            //System.out.printf("SGCraft: FeatureGeneration: Found component %s\n", comp);
            if (comp instanceof ComponentScatteredFeaturePieces.DesertPyramid) {
                StructureBoundingBox box = ((StructureComponent)comp).getBoundingBox();
                if (FeatureGeneration.debugStructures)
                    System.out.printf("SGCraft: FeatureGeneration: Augmenting %s at (%s, %s)\n",
                        comp.getClass().getSimpleName(), box.getCenterX(), box.getCenterZ());
                newComponents.add(new FeatureUnderDesertPyramid((ComponentScatteredFeaturePieces.DesertPyramid)comp));
            }
        }
        oldComponents.addAll(newComponents);
    }

}
