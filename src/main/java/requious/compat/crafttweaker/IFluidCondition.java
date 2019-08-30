package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("requious.fluid.IFluidCondition")
@ZenRegister
public interface IFluidCondition {
    boolean matches(ILiquidStack stack);
}
