package gcewing.sg.interfaces;

import net.minecraft.item.ItemStack;

import gcewing.sg.utils.ModelSpec;

public interface IItem extends ITextureConsumer {

    ModelSpec getModelSpec(ItemStack stack);

    int getNumSubtypes();
}
