package requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IVector3d;
import requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("crafttweaker.world.IVector3d")
@ZenRegister
public class ExpansionVector {
    @ZenCaster
    public static Parameter asParameter(IVector3d value) {
        return new Parameter.Constant(CraftTweakerMC.getVec3d(value));
    }
}
