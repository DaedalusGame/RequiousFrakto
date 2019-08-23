package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenClass("mods.requious.Laser")
public class LaserPowerCT {
    String type;
    int energy;

    public LaserPowerCT(String type, int energy) {
        this.type = type;
        this.energy = energy;
    }

    @ZenGetter("energy")
    public int getEnergy() {
        return energy;
    }

    @ZenGetter("type")
    public String getType() {
        return type;
    }
}
