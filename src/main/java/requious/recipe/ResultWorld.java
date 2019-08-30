package requious.recipe;

import requious.compat.crafttweaker.IWorldFunction;
import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.Energy;
import requious.compat.jei.slot.EnergySlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentEnergy;
import requious.data.component.ComponentWorld;

public class ResultWorld extends ResultBase {
    IWorldFunction worldCheck;

    public ResultWorld(String group, IWorldFunction worldCheck) {
        super(group);
        this.worldCheck = worldCheck;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        if(slot instanceof ComponentWorld.Slot) {
            ComponentWorld.Slot worldSlot = (ComponentWorld.Slot) slot;
            if(worldSlot.run(worldCheck))
                return true;
        }
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        //NOOP
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        return false;
    }
}
