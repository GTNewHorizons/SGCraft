package gcewing.sg.interfaces;

import gcewing.sg.BaseMod;
import gcewing.sg.utils.ModelSpec;
import net.minecraft.item.ItemStack;

public interface IItem extends ITextureConsumer {

    ModelSpec getModelSpec(ItemStack stack);

    int getNumSubtypes();
}
