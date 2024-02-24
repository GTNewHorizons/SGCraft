package gcewing.sg.interfaces;

import gcewing.sg.utils.Trans3;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public interface IModel {
    AxisAlignedBB getBounds();

    void addBoxesToList(Trans3 t, List list);

    void render(Trans3 t, IRenderTarget renderer, ITexture... textures);

}
