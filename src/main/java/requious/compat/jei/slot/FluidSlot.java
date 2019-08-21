package requious.compat.jei.slot;

import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import requious.Requious;
import requious.compat.jei.IngredientCollector;
import requious.compat.jei.JEISlot;
import requious.util.Misc;

import java.util.ArrayList;
import java.util.List;

public class FluidSlot extends JEISlot {
    public List<FluidStack> fluids = new ArrayList<>();

    public FluidSlot(int x, int y, String group) {
        super(x, y, group);
    }

    @Override
    public boolean isFilled() {
        return !fluids.isEmpty();
    }

    @Override
    public void resetFill() {
        super.resetFill();
        fluids.clear();
    }

    @Override
    public JEISlot copy() {
        FluidSlot fluidSlot = new FluidSlot(x,y,group);
        return fluidSlot;
    }

    @Override
    public void getIngredients(IngredientCollector collector) {
        for (FluidStack fluid : fluids) {
            if(isInput())
                collector.addInput(VanillaTypes.FLUID,fluid);
            else
                collector.addOutput(VanillaTypes.FLUID,fluid);
        }
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(new ResourceLocation(Requious.MODID,"textures/gui/assembly_slots.png"));
        Misc.drawTexturedModalRect(x*18,y*18, 18, 0, 18,18);
    }
}
