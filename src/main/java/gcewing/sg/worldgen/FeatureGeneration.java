// ------------------------------------------------------------------------------------------------
//
// SG Craft - Map feature generation
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.worldgen;

import static gcewing.sg.utils.BaseUtils.getFieldDef;
import static gcewing.sg.utils.BaseUtils.setField;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.SCATTERED_FEATURE;

import java.lang.reflect.Field;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

import gcewing.sg.BaseConfiguration;
import gcewing.sg.SGCraft;

public class FeatureGeneration {

    public static boolean augmentStructures = false;
    public static boolean debugStructures = false;

    static Field structureMap = getFieldDef(MapGenStructure.class, "structureMap", "field_75053_d");

    public static void configure(BaseConfiguration config) {
        augmentStructures = config.getBoolean("options", "augmentStructures", augmentStructures);
        debugStructures = config.getBoolean("debug", "debugStructures", debugStructures);
    }

    public static void onInitMapGen(InitMapGenEvent e) {
        if (!augmentStructures) {
            return;
        }
        if (e.type == SCATTERED_FEATURE) {
            if (e.newGen instanceof MapGenStructure) e.newGen = modifyScatteredFeatureGen((MapGenStructure) e.newGen);
            else {
                SGCraft.log.warn(
                        "SGCraft: FeatureGeneration: SCATTERED_FEATURE generator is not a MapGenStructure, cannot customise");
            }
        }

    }

    static MapGenStructure modifyScatteredFeatureGen(MapGenStructure gen) {
        setField(gen, structureMap, new SGStructureMap());
        return gen;
    }

}
