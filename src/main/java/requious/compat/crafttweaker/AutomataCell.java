package requious.compat.crafttweaker;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import requious.data.component.ComponentAutomata;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

public class AutomataCell {
    ComponentAutomata.Collector collector;
    ComponentAutomata.Slot slot;

    public AutomataCell(ComponentAutomata.Collector collector, ComponentAutomata.Slot slot) {
        this.collector = collector;
        this.slot = slot;
    }

    @ZenGetter("x")
    public int getX() {
        return slot.getX();
    }

    @ZenGetter("y")
    public int getY() {
        return slot.getY();
    }

    @ZenGetter("stack")
    public IItemStack getStack() {
        return CraftTweakerMC.getIItemStack(slot.getItem().getStack());
    }

    @ZenMethod
    public IItemStack getStack(int dx, int dy) {
        if(dx == 0 && dy == 0)
            return getStack();
        return CraftTweakerMC.getIItemStack(collector.getStackAt(getX()+dx,getY()+dy));
    }

    @ZenMethod
    public boolean isOutOfBounds(int dx, int dy) {
        if(dx == 0 && dy == 0)
            return false;
        return collector.exists(getX()+dx,getY()+dy);
    }
}
