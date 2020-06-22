package requious.recipe;

import requious.compat.jei.JEISlot;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentBase;

public abstract class ResultBase {
    String group;

    public ResultBase(String group) {
        this.group = group;
    }

    //Machine global matching
    public boolean matches(AssemblyProcessor assembly) {
        return false;
    }

    public void produce(AssemblyProcessor assembly) {
        //NOOP
    }

    //Slot local matching
    public abstract boolean matches(ComponentBase.Slot slot);

    public abstract void produce(ComponentBase.Slot slot);

    public abstract boolean fillJEI(JEISlot slot);
}
