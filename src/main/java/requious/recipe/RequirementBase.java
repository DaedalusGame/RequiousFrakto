package requious.recipe;

import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentBase;

public abstract class RequirementBase {
    public String group;

    public RequirementBase(String group) {
        this.group = group;
    }

    //Machine global matching
    public MatchResult matches(AssemblyProcessor assembly, ConsumptionResult result) {
        return MatchResult.NOT_MATCHED;
    }

    //Slot local matching
    public abstract MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result);

    public abstract void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container);

    public abstract <T> void consume(ComponentBase.Slot slot, ConsumptionResult result);

    public abstract ConsumptionResult createResult();

    public abstract boolean fillJEI(JEISlot slot);
}
