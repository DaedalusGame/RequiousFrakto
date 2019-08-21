package requious.compat.jei.ingredient;

public class Energy implements IFakeIngredient {
    public int energy;
    public String unit;

    public Energy(int energy, String unit) {
        this.energy = energy;
        this.unit = unit;
    }

    @Override
    public String getDisplayName() {
        return "Energy";
    }

    @Override
    public String getUniqueID() {
        return "energy";
    }

    @Override
    public boolean isValid() {
        return energy > 0;
    }
}
