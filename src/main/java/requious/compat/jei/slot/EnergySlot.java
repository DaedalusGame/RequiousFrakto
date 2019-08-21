package requious.compat.jei.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import requious.Requious;
import requious.compat.jei.IngredientCollector;
import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.Energy;
import requious.compat.jei.ingredient.IngredientTypes;
import requious.util.Misc;

import java.util.ArrayList;
import java.util.List;

public class EnergySlot extends JEISlot {
    public Energy input;
    public Energy output;
    public String unit;

    public EnergySlot(int x, int y, String group, String unit) {
        super(x, y, group);
        this.unit = unit;
    }

    @Override
    public boolean isInput() {
        return false;
    }

    @Override
    public void resetFill() {
        super.resetFill();
        input = null;
        output = null;
    }

    @Override
    public JEISlot copy() {
        EnergySlot energySlot = new EnergySlot(x, y, group, unit);
        return energySlot;
    }

    @Override
    public void getIngredients(IngredientCollector collector) {
        if(input != null) {
            collector.addInput(IngredientTypes.ENERGY,input);
        }
        if(output != null) {
            collector.addOutput(IngredientTypes.ENERGY,output);
        }
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(new ResourceLocation(Requious.MODID, "textures/gui/assembly_slots.png"));
        Misc.drawTexturedModalRect(x * 18, y * 18, 18, 18 * 4, 18, 18);
    }

    public Energy getEnergy() {
        int energy = 0;
        if(input != null) {
            energy -= input.energy;
        }
        if(output != null) {
            energy += output.energy;
        }
        return new Energy(energy,unit);
    }
}
