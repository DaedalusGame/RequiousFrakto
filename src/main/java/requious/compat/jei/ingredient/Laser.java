package requious.compat.jei.ingredient;

import requious.util.SlotVisual;

public class Laser implements IFakeIngredient {
    public int energy;
    public String type;
    public SlotVisual visual;

    public Laser(int energy, String type, SlotVisual visual) {
        this.energy = energy;
        this.type = type;
        this.visual = visual;
    }

    @Override
    public String getDisplayName() {
        return "Laser";
    }

    @Override
    public String getUniqueID() {
        return "laser";
    }

    @Override
    public boolean isValid() {
        return energy > 0;
    }
}
