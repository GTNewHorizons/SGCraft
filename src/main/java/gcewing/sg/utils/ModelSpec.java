package gcewing.sg.utils;

import org.joml.Vector3d;

public class ModelSpec {

    public String modelName;
    public String[] textureNames;
    public Vector3d origin;

    public ModelSpec(String model, String... textures) {
        this(model, new Vector3d(), textures);
    }

    public ModelSpec(String model, Vector3d origin, String... textures) {
        modelName = model;
        textureNames = textures;
        this.origin = origin;
    }
}
