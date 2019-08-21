package requious.recipe;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.Energy;
import requious.compat.jei.slot.EnergySlot;
import requious.compat.jei.slot.ItemSlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentEnergy;

public class RequirementEnergy extends RequirementBase {
    String mark;
    int min, max;

    public RequirementEnergy(String group, int energy, String mark) {
        this(group,energy,energy,mark);
    }

    public RequirementEnergy(String group, int min, int max, String mark) {
        super(group);
        this.mark = mark;
        this.min = min;
        this.max = max;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentEnergy.Slot && slot.isGroup(group)) {
            int extracted = ((ComponentEnergy.Slot) slot).extract(max,true);
            if(extracted >= min) {
                result.add(extracted);
                return MatchResult.MATCHED;
            }
        }
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {
        if(mark != null)
            container.addInput(mark, (int) result.consumed);
    }

    @Override
    public void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentEnergy.Slot && result instanceof ConsumptionResult.Integer) {
            ((ComponentEnergy.Slot) slot).extract((int)result.getConsumed(),false);
        }
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Integer(this,0);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if(slot instanceof EnergySlot && slot.group.equals(group) && ((EnergySlot) slot).input == null) {
            EnergySlot energySlot = (EnergySlot) slot;
            energySlot.input = new Energy(min, energySlot.unit);
            return true;
        }

        return false;
    }
}
