package requious.compat.jei.slot;

import net.minecraft.client.Minecraft;
import requious.compat.jei.IngredientCollector;
import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.IngredientTypes;
import requious.compat.jei.ingredient.JEIInfo;
import requious.compat.jei.ingredient.Laser;

import java.util.ArrayList;
import java.util.List;

public class JEIInfoSlot extends JEISlot {
    public JEIInfo info;

    public JEIInfoSlot(int x, int y, String group) {
        super(x, y, group);
    }

    @Override
    public boolean isFilled() {
        return info != null;
    }

    @Override
    public JEISlot copy() {
        return new JEIInfoSlot(x,y,group);
    }

    @Override
    public void getIngredients(IngredientCollector collector) {
        //NOOP
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }
}
