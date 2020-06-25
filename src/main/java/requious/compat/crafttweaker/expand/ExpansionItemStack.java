package requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("crafttweaker.item.IItemStack")
@ZenRegister
public class ExpansionItemStack {
    @ZenCaster
    public static Parameter asParameter(IItemStack value) {
        return new Parameter.Constant(CraftTweakerMC.getItemStack(value));
    }
}
