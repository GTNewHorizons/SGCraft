package gcewing.sg.interfaces;

import java.util.List;

import net.minecraft.util.AxisAlignedBB;

import gcewing.sg.utils.Trans3;

public interface IModel {

    AxisAlignedBB getBounds();

    void addBoxesToList(Trans3 t, List list);

    void render(Trans3 t, IRenderTarget renderer, ITexture... textures);

}
