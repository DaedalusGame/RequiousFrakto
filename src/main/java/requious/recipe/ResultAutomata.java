package requious.recipe;

import requious.compat.crafttweaker.IAutomataStep;
import requious.compat.jei.JEISlot;
import requious.data.component.ComponentAutomata;
import requious.data.component.ComponentBase;

public class ResultAutomata extends ResultBase {
    IAutomataStep step;

    public ResultAutomata(String group, IAutomataStep step) {
        super(group);
        this.step = step;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        if(slot instanceof ComponentAutomata.Slot && slot.isGroup(group)) {
            return true;
        }
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        ((ComponentAutomata.Slot) slot).addStep(group,step);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        return false;
    }
}
