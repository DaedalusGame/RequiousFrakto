package requious.recipe;

import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.JEIInfo;
import requious.compat.jei.ingredient.Laser;
import requious.compat.jei.slot.JEIInfoSlot;
import requious.compat.jei.slot.LaserSlot;
import requious.data.component.ComponentBase;
import requious.util.SlotVisual;

public class RequirementJEI extends RequirementBase {
    String langKey;
    SlotVisual slotVisual;

    public RequirementJEI(String group, String langKey, SlotVisual slotVisual) {
        super(group);
        this.langKey = langKey;
        this.slotVisual = slotVisual;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        return MatchResult.MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {

    }

    @Override
    public <T> void consume(ComponentBase.Slot slot, ConsumptionResult result) {

    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Integer(this,0);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if(slot instanceof JEIInfoSlot && slot.group.equals(group) && !slot.isFilled()) {
            JEIInfoSlot laserSlot = (JEIInfoSlot) slot;
            laserSlot.info = new JEIInfo(langKey,slotVisual);
            return true;
        }

        return false;
    }
}
