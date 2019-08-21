package requious.recipe;

import crafttweaker.annotations.ZenRegister;
import requious.compat.crafttweaker.RecipeContainer;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.requious.IRecipeFunction")
public interface IRecipeFunction {
    void calculate(RecipeContainer container);
}
