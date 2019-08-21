package requious.compat.jei.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import requious.compat.jei.IngredientCollector;
import requious.compat.jei.JEISlot;
import requious.util.SlotVisual;

import java.util.List;

public class DurationSlot extends JEISlot {
    SlotVisual visual;
    public int duration;

    public DurationSlot(int x, int y, String group, SlotVisual visual) {
        super(x, y, group);
        this.visual = visual;
    }

    @Override
    public JEISlot copy() {
        return new DurationSlot(x,y,group,visual);
    }

    @Override
    public void getIngredients(IngredientCollector collector) {

    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        visual.render(minecraft,x*18,y*18);
    }

    @Override
    public void getTooltip(List<String> tooltip, ITooltipFlag.TooltipFlags tooltipFlag) {
        if(duration <= 0)
            tooltip.add(I18n.format("requious.duration.instant"));
        else {
            if(duration >= 20) {
                if (duration < 1200)
                    tooltip.add(I18n.format("requious.duration.seconds", String.format("%.1f", duration / 20.0)));
                else if (duration < 72000)
                    tooltip.add(I18n.format("requious.duration.minutes", String.format("%.1f", duration / 1200.0)));
                else
                    tooltip.add(I18n.format("requious.duration.hours", String.format("%.1f", duration / 72000.0)));
            }
            tooltip.add(TextFormatting.GRAY+I18n.format("requious.duration.ticks",duration));
        }
    }
}
