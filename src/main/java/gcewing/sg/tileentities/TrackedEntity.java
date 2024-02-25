package gcewing.sg.tileentities;

import net.minecraft.entity.Entity;

import org.joml.Vector3d;

public class TrackedEntity {

    public Entity entity;
    public Vector3d lastPos;

    public TrackedEntity(Entity entity) {
        this.entity = entity;
        this.lastPos = new Vector3d(entity.posX, entity.posY, entity.posZ);
    }

}
