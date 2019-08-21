package requious.recipe;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import requious.compat.jei.JEISlot;
import requious.compat.jei.slot.ItemSlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentItem;

public class ResultItem extends ResultBase {
    ItemStack stack;
    int minInsert;

    public ResultItem(String group, ItemStack stack) {
        this(group,stack,stack.getCount());
    }

    public ResultItem(String group, ItemStack stack, int minInsert) {
        super(group);
        this.stack = stack;
        this.minInsert = minInsert;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        if(slot instanceof ComponentItem.Slot && slot.isGroup(group)) {
            ItemStack remainder = ((ComponentItem.Slot) slot).getItem().insert(stack, true);
            int filled = stack.getCount() - remainder.getCount();
            if(filled >= minInsert)
                return true;
        }
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        ((ComponentItem.Slot) slot).getItem().insert(stack,false);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if(slot instanceof ItemSlot && slot.group.equals(group) && !slot.isFilled()) {
            ItemSlot itemSlot = (ItemSlot) slot;
            itemSlot.items.add(stack);
            return true;
        }

        return false;
    }
}
