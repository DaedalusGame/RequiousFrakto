package requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import requious.compat.crafttweaker.ColorCT;
import requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("mods.requious.Color")
@ZenRegister
public class ExpansionColor {
    @ZenCaster
    public static Parameter asParameter(ColorCT value) {
        return new Parameter.Constant(value.get());
    }
}
