package requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("crafttweaker.liquid.ILiquidStack")
@ZenRegister
public class ExpansionFluidStack {
    @ZenCaster
    public static Parameter asParameter(ILiquidStack value) {
        return new Parameter.Constant(CraftTweakerMC.getLiquidStack(value));
    }
}
