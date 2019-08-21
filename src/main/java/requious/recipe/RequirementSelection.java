package requious.recipe;

import net.minecraft.item.ItemStack;
import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.compat.jei.slot.SelectionSlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentSelection;

public class RequirementSelection extends RequirementBase {
    ItemStack icon;
    boolean reset;

    public RequirementSelection(String group, ItemStack icon, boolean reset) {
        super(group);
        this.icon = icon;
        this.reset = reset;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentSelection.Slot && slot.isGroup(group)) {
            ComponentSelection.Slot selectionSlot = (ComponentSelection.Slot) slot;
            selectionSlot.addSelection(icon);
            ItemStack stack = selectionSlot.getSelection();
            if(selectionSlot.isSelected() && ItemStack.areItemStacksEqual(stack,icon) && stack.getCount() == icon.getCount()) {
                return MatchResult.MATCHED;
            }
        }
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {

    }

    @Override
    public void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentSelection.Slot && slot.isGroup(group)) {
            ComponentSelection.Slot selectionSlot = (ComponentSelection.Slot) slot;
            ItemStack stack = selectionSlot.getSelection();
            if(reset && selectionSlot.isSelected() && ItemStack.areItemStacksEqual(stack,icon) && stack.getCount() == icon.getCount()) {
                selectionSlot.unselect();
            }
        }
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Integer(this,0);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if(slot instanceof SelectionSlot && slot.group.equals(group) && !slot.isFilled()) {
            SelectionSlot selectionSlot = (SelectionSlot) slot;
            selectionSlot.items.add(icon);
            return true;
        }

        return false;
    }
}
