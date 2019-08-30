package requious.item;

import net.minecraft.item.ItemStack;

import java.awt.*;

public interface IDynamicItemModel {
    Color getTint(ItemStack stack, int tintIndex);
}
