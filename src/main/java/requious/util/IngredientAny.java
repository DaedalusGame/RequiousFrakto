package requious.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class IngredientAny extends Ingredient {
    @Override
    public boolean apply(@Nullable ItemStack p_apply_1_) {
        return true;
    }
}
