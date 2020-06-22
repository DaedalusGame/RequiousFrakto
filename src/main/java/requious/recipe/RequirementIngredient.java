package requious.recipe;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.compat.jei.slot.FluidSlot;
import requious.compat.jei.slot.ItemSlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentItem;

public class RequirementIngredient extends RequirementBase {
    IIngredient ingredient;
    int min, max;

    public RequirementIngredient(String group, IIngredient ingredient) {
        this(group,ingredient,ingredient.getAmount(),ingredient.getAmount());
    }

    public RequirementIngredient(String group, IIngredient ingredient, int min, int max) {
        super(group);
        this.ingredient = ingredient;
        this.min = min;
        this.max = max;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentItem.Slot && slot.isGroup(group)) {
            ItemStack stack = ((ComponentItem.Slot) slot).getItem().getStack();
            if(ingredient.matches(CraftTweakerMC.getIItemStack(stack)) && stack.getCount() >= min) {
                result.add(Math.min(max,stack.getCount()));
                return MatchResult.MATCHED;
            }
        }
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {
        if(ingredient.getMark() != null)
            container.addInput(ingredient.getMark(),((ComponentItem.Slot) slot).getItem().extract((int)result.getConsumed(),true));
    }

    @Override
    public void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentItem.Slot && result instanceof ConsumptionResult.Integer) {
            ((ComponentItem.Slot) slot).getItem().extract((int)result.getConsumed(),false);
        }
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Integer(this,0);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if(slot instanceof ItemSlot && slot.group.equals(group) && !slot.isFilled()) {
            ItemSlot itemSlot = (ItemSlot) slot;
            if(ingredient.getItems() != null) {
                for (IItemStack stack : ingredient.getItems()) {
                    ItemStack jeiStack = CraftTweakerMC.getItemStack(stack);
                    jeiStack.setCount(ingredient.getAmount());
                    itemSlot.items.add(jeiStack);
                }
            } else {
                //TODO: Mark wildcard
            }
            itemSlot.setInput(true);
            return true;
        }

        return false;
    }
}
