package requious.compat.jei.ingredient;

import mezz.jei.api.recipe.IIngredientType;

public class IngredientTypes {
    public static final IIngredientType<Energy> ENERGY = () -> Energy.class;
    public static final IIngredientType<Laser> LASER = () -> Laser.class;
}
