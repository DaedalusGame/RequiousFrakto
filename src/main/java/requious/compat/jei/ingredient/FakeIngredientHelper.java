package requious.compat.jei.ingredient;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredientHelper;
import requious.Requious;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class FakeIngredientHelper<T extends IFakeIngredient> implements IIngredientHelper<T> {
    @Override
    public List<T> expandSubtypes(List<T> ingredients) {
        return ingredients;
    }

    @Nullable
    @Override
    public T getMatch(Iterable<T> ingredients, T ingredientToMatch) {
        return null;
    }

    @Override
    public String getDisplayName(IFakeIngredient ingredient) {
        return ingredient.getDisplayName();
    }

    @Override
    public String getUniqueId(IFakeIngredient ingredient) {
        return ingredient.getUniqueID();
    }

    @Override
    public String getWildcardId(IFakeIngredient ingredient) {
        return ingredient.getUniqueID();
    }

    @Override
    public String getModId(IFakeIngredient ingredient) {
        return Requious.MODID;
    }

    @Override
    public Iterable<Color> getColors(IFakeIngredient ingredient) {
        return Lists.newArrayList();
    }

    @Override
    public String getResourceId(IFakeIngredient ingredient) {
        return ingredient.getUniqueID();
    }

    @Override
    public IFakeIngredient copyIngredient(IFakeIngredient ingredient) {
        return ingredient;
    }

    @Override
    public String getErrorInfo(IFakeIngredient ingredient) {
        return "This is a dummy ingredient for use with Requious Frakto";
    }

    @Override
    public boolean isValidIngredient(IFakeIngredient ingredient) {
        return ingredient.isValid();
    }
}