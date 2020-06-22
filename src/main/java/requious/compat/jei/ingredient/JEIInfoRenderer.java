package requious.compat.jei.ingredient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import requious.util.Fill;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class JEIInfoRenderer extends FakeIngredientRenderer<JEIInfo> {
    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable JEIInfo ingredient) {
        if(ingredient != null) {
            GlStateManager.enableAlpha();
            ingredient.visual.render(minecraft,xPosition-1,yPosition-1, new Fill(0,0));
        }
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, JEIInfo ingredient, ITooltipFlag tooltipFlag) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format(ingredient.langKey));
        return tooltip;
    }
}
