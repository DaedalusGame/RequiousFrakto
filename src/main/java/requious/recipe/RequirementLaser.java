package requious.recipe;

import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.Energy;
import requious.compat.jei.ingredient.Laser;
import requious.compat.jei.slot.EnergySlot;
import requious.compat.jei.slot.LaserSlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentLaser;
import requious.util.SlotVisual;

public class RequirementLaser extends RequirementBase {
    String mark;
    String type;
    int energy;
    SlotVisual slotVisual;

    public RequirementLaser(String group, int energy, String mark, SlotVisual slotVisual) {
        super(group);
        this.mark = mark;
        this.energy = energy;
        this.slotVisual = slotVisual;
    }

    public RequirementLaser(String group, String type, int energy, String mark, SlotVisual slotVisual) {
        super(group);
        this.mark = mark;
        this.type = type;
        this.energy = energy;
        this.slotVisual = slotVisual;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentLaser.Slot && slot.isGroup(group)) {
            ComponentLaser.Slot laserSlot = (ComponentLaser.Slot) slot;
            int energy = laserSlot.getEnergy(type);
            if(energy >= this.energy) {
                result.add(energy);
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
        //It's a level, so it need not consume
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Integer(this,0);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if(slot instanceof LaserSlot && slot.group.equals(group) && !slot.isFilled()) {
            LaserSlot laserSlot = (LaserSlot) slot;
            laserSlot.energies.add(new Laser(energy,type,slotVisual));
            laserSlot.setInput(true);
            return true;
        }

        return false;
    }
}
