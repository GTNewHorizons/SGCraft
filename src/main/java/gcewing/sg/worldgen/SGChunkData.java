// ------------------------------------------------------------------------------------------------
//
// SG Craft - Extra data saved with a chunk
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.worldgen;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gcewing.sg.SGCraft;

public class SGChunkData {

    private static final boolean DEBUG = false;
    private static final HashMap<ChunkCoordIntPair, Boolean> map = new HashMap<>();

    public static boolean forChunk(Chunk chunk, NBTTagCompound nbt) {
        ChunkCoordIntPair coords = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);
        Boolean isPresent = map.get(coords);
        if (isPresent == null) {
            isPresent = nbt != null && nbt.getBoolean("gcewing.sg.oresGenerated");
            map.put(coords, isPresent);
        }
        return isPresent;
    }

    public static void setChunk(Chunk chunk, boolean isPresent) {
        ChunkCoordIntPair coords = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);
        map.put(coords, isPresent);
    }

    public static class EventHandler {

        @SubscribeEvent
        public void onChunkLoad(ChunkDataEvent.Load e) {
            final Chunk chunk = e.getChunk();
            final boolean isPresent = SGChunkData.forChunk(chunk, e.getData());
            if (!isPresent && SGCraft.addOresToExistingWorlds) {
                if (DEBUG) SGCraft.log.debug(
                        "SGChunkData.onChunkLoad: Adding ores to chunk ({}, {})",
                        chunk.xPosition,
                        chunk.zPosition);
                SGCraft.naquadahOreGenerator.regenerate(chunk);
            }
        }

        @SubscribeEvent
        public void onChunkSave(ChunkDataEvent.Save e) {
            final Chunk chunk = e.getChunk();
            final boolean isPresent = forChunk(chunk, null);
            e.getData().setBoolean("gcewing.sg.oresGenerated", isPresent);
        }
    }
}
