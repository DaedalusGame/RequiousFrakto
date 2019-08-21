package requious.compat.jei.ingredient;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

public class FakeIngredientRenderer<T extends IFakeIngredient> implements IIngredientRenderer<T> {
    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable IFakeIngredient ingredient) {

    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, T ingredient, ITooltipFlag tooltipFlag) {
        return Lists.newArrayList();
    }
}
