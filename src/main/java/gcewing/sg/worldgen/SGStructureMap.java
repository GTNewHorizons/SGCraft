package gcewing.sg.worldgen;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

import gcewing.sg.SGCraft;

public class SGStructureMap extends HashMap {

    @Override
    public Object put(Object key, Object value) {
        if (value instanceof StructureStart) augmentStructureStart((StructureStart) value);
        return super.put(key, value);
    }

    void augmentStructureStart(StructureStart start) {
        LinkedList oldComponents = start.getComponents();
        LinkedList newComponents = new LinkedList();
        for (Object comp : oldComponents) {
            if (!(comp instanceof ComponentScatteredFeaturePieces.DesertPyramid)) {
                continue;
            }

            StructureBoundingBox box = ((StructureComponent) comp).getBoundingBox();
            if (FeatureGeneration.debugStructures) {
                SGCraft.log.debug(
                        String.format(
                                "SGCraft: FeatureGeneration: Augmenting %s at (%s, %s)",
                                comp.getClass().getSimpleName(),
                                box.getCenterX(),
                                box.getCenterZ()));
            }
            newComponents.add(new FeatureUnderDesertPyramid((ComponentScatteredFeaturePieces.DesertPyramid) comp));
        }
        oldComponents.addAll(newComponents);
    }
}
