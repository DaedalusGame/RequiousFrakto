package requious.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import requious.data.ToolData;

import java.util.Set;

public class ItemTool extends Item {
    ToolData data;

    public ItemTool(ToolData data) {
        this.data = data;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return super.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return super.showDurabilityBar(stack);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return super.getToolClasses(stack);
    }
}
