package requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenExpansion("string")
@ZenRegister
public class ExpansionString {
    @ZenMethod
    public static Parameter.Variable asVariable(String value) {
        return new Parameter.Variable(value);
    }

    @ZenCaster
    public static Parameter asParameter(String value) {
        return new Parameter.Constant(value);
    }
}
