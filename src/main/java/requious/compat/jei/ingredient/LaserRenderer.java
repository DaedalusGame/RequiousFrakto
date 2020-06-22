package requious.compat.jei.ingredient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import requious.util.Fill;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LaserRenderer extends FakeIngredientRenderer<Laser> {
    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable Laser ingredient) {
        if(ingredient != null) {
            GlStateManager.enableAlpha();
            ingredient.visual.render(minecraft,xPosition-1,yPosition-1, new Fill(0,0));
        }
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, Laser ingredient, ITooltipFlag tooltipFlag) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("requious.unit.laser."+ingredient.type,ingredient.energy));
        return tooltip;
    }
}
