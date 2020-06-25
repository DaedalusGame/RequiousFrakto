package requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IFacing;
import requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("crafttweaker.world.IFacing")
@ZenRegister
public class ExpansionFacing {
    @ZenCaster
    public static Parameter asParameter(IFacing value) {
        return new Parameter.Constant(value.getInternal());
    }
}
