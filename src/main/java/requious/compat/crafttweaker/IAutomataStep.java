package requious.compat.crafttweaker;

import crafttweaker.api.item.IItemStack;

public interface IAutomataStep {
    IItemStack getResult(AutomataCell cell);
}
