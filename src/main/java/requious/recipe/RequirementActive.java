package requious.recipe;

import requious.compat.crafttweaker.IWorldFunction;
import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentBase;

public class RequirementActive extends RequirementBase {
    int time;

    public RequirementActive(int time) {
        super("active");
        this.time = time;
    }

    @Override
    public MatchResult matches(AssemblyProcessor assembly, ConsumptionResult result) {
        assembly.setVariable("active",time);
        return MatchResult.MATCHED;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {
        //NOOP
    }

    @Override
    public <T> void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        //NOOP
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Integer(this,0);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        return false;
    }
}
