package requious.recipe;

import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.Energy;
import requious.compat.jei.ingredient.Laser;
import requious.compat.jei.slot.EnergySlot;
import requious.compat.jei.slot.LaserSlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentLaser;
import requious.util.LaserVisual;
import requious.util.SlotVisual;

public class ResultLaser extends ResultBase {
    String type;
    int energy;
    LaserVisual visual;
    SlotVisual slotVisual;

    public ResultLaser(String group, String type, int energy, LaserVisual visual, SlotVisual slotVisual) {
        super(group);
        this.type = type;
        this.energy = energy;
        this.visual = visual;
        this.slotVisual = slotVisual;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        if(slot instanceof ComponentLaser.Slot && slot.isGroup(group)) {
            ComponentLaser.Slot laserSlot = (ComponentLaser.Slot) slot;
            if(laserSlot.getEmitType() == null || laserSlot.getEmitType().equals(type))
                laserSlot.emit(type,energy,visual);
                return true;
        }
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        ((ComponentLaser.Slot) slot).emit(type ,energy, visual);
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
