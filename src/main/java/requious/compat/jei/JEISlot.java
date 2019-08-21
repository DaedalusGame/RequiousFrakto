package requious.compat.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;

import java.util.List;

public abstract class JEISlot {
    public int x, y;
    public String group;
    boolean filled;
    boolean input;

    public JEISlot(int x, int y, String group) {
        this.x = x;
        this.y = y;
        this.group = group;
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    public boolean isFilled() {
        return filled;
    }

    public void resetFill() {
        filled = false;
    }

    public abstract JEISlot copy();

    public abstract void getIngredients(IngredientCollector collector);

    public abstract void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY);

    public void getTooltip(List<String> tooltip, ITooltipFlag.TooltipFlags tooltipFlag) {
        //NOOP
    }
}
