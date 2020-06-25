package requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("float")
@ZenRegister
public class ExpansionFloat {
    @ZenCaster
    public static Parameter asParameter(float value) {
        return new Parameter.Constant(value);
    }
}
