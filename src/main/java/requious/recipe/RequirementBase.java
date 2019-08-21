package requious.recipe;

import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.data.component.ComponentBase;

public abstract class RequirementBase {
    public String group;

    public RequirementBase(String group) {
        this.group = group;
    }

    public abstract MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result);

    public abstract void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container);

    public abstract <T> void consume(ComponentBase.Slot slot, ConsumptionResult result);

    public abstract ConsumptionResult createResult();

    public abstract boolean fillJEI(JEISlot slot);
}
