package requious.recipe;

import requious.compat.jei.JEISlot;
import requious.data.component.ComponentBase;

public abstract class ResultBase {
    String group;

    public ResultBase(String group) {
        this.group = group;
    }

    public abstract boolean matches(ComponentBase.Slot slot);

    public abstract void produce(ComponentBase.Slot slot);

    public abstract boolean fillJEI(JEISlot slot);
}
