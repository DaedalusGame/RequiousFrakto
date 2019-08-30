package requious.recipe;

import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.JEIInfo;
import requious.compat.jei.slot.JEIInfoSlot;
import requious.data.component.ComponentBase;
import requious.util.SlotVisual;

public class ResultJEI extends ResultBase {
    String langKey;
    SlotVisual slotVisual;

    public ResultJEI(String group, String langKey, SlotVisual slotVisual) {
        super(group);
        this.langKey = langKey;
        this.slotVisual = slotVisual;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        return true;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {

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
