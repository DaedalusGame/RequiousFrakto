package requious.compat.jei.ingredient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import requious.Requious;
import requious.util.Misc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnergyRenderer extends FakeIngredientRenderer<Energy> {
    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable IFakeIngredient ingredient) {
        if(ingredient != null) {
            GlStateManager.enableAlpha();
            minecraft.getTextureManager().bindTexture(new ResourceLocation(Requious.MODID, "textures/gui/assembly_slots.png"));
            Misc.drawTexturedModalRect(xPosition - 1, yPosition - 1, 0 * 18, 3 * 18, 18, 18);
        }
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, Energy ingredient, ITooltipFlag tooltipFlag) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("requious.unit."+ingredient.unit+".cost",ingredient.energy));
        return tooltip;
    }
}
