package requious.recipe;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.FluidStack;
import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.compat.jei.slot.FluidSlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentFluid;

public class RequirementFluid extends RequirementBase {
    ILiquidStack ingredient;
    int min, max;

    public RequirementFluid(String group, ILiquidStack ingredient) {
        this(group,ingredient,ingredient.getAmount(),ingredient.getAmount());
    }

    public RequirementFluid(String group, ILiquidStack ingredient, int min, int max) {
        super(group);
        this.ingredient = ingredient;
        this.min = min;
        this.max = max;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentFluid.Slot && slot.isGroup(group)) {
            FluidStack stack = ((ComponentFluid.Slot) slot).getContents();
            if(stack != null && ingredient.matches(CraftTweakerMC.getILiquidStack(stack)) && stack.amount >= min) {
                result.add(Math.min(stack.amount,max));
                return MatchResult.MATCHED;
            }
        }
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {
        if(ingredient.getMark() != null)
            container.addInput(ingredient.getMark(),((ComponentFluid.Slot) slot).drain((int)result.getConsumed(),true));
    }

    @Override
    public void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentFluid.Slot && result instanceof ConsumptionResult.Integer) {
            ((ComponentFluid.Slot) slot).drain((int)result.getConsumed(),false);
        }
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Integer(this,0);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if(slot instanceof FluidSlot && slot.group.equals(group) && !slot.isFilled()) {
            FluidSlot fluidSlot = (FluidSlot) slot;
            if(ingredient.getLiquids() != null) {
                for (ILiquidStack fluid : ingredient.getLiquids()) {
                    fluidSlot.fluids.add(CraftTweakerMC.getLiquidStack(fluid));
                }
            } else {
                //TODO: Mark wildcard
            }
            fluidSlot.setInput(true);
            return true;
        }

        return false;
    }
}
