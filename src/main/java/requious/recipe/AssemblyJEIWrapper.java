package requious.recipe;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import requious.compat.jei.IngredientCollector;
import requious.compat.jei.JEISlot;

import java.util.ArrayList;
import java.util.List;

public class AssemblyJEIWrapper implements IRecipeWrapper {
    public AssemblyRecipe recipe;

    public AssemblyJEIWrapper(AssemblyRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        recipe.generateJEI();
        IngredientCollector collector = new IngredientCollector();
        for(JEISlot slot : recipe.jeiSlots) {
            slot.getIngredients(collector);
        }
        collector.collect(ingredients); //Possibly cache since it doesn't change ever.
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        for (JEISlot slot : recipe.jeiSlots) {
            slot.render(minecraft,recipeWidth,recipeHeight,mouseX,mouseY);
        }
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ITooltipFlag.TooltipFlags tooltipFlag = Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
        List<String> tooltip = new ArrayList<>();
        for (JEISlot slot : recipe.jeiSlots) {
            if(mouseX >= slot.x*18 && mouseY >= slot.y*18 && mouseX < slot.x*18+18 && mouseY < slot.y*18+18)
                slot.getTooltip(tooltip,tooltipFlag);
        }
        return tooltip;
    }
}
