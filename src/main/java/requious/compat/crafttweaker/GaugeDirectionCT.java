package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import requious.gui.GaugeDirection;
import requious.util.ComponentFace;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.requious.GaugeDirection")
public class GaugeDirectionCT {
    private static final GaugeDirectionCT UP = new GaugeDirectionCT(GaugeDirection.UP);
    private static final GaugeDirectionCT LEFT = new GaugeDirectionCT(GaugeDirection.LEFT);
    private static final GaugeDirectionCT DOWN = new GaugeDirectionCT(GaugeDirection.DOWN);
    private static final GaugeDirectionCT RIGHT = new GaugeDirectionCT(GaugeDirection.RIGHT);

    GaugeDirection internal;

    public GaugeDirectionCT(GaugeDirection internal) {
        this.internal = internal;
    }

    public GaugeDirection get() {
        return internal;
    }

    @ZenMethod
    public static GaugeDirectionCT up() {
        return UP;
    }

    @ZenMethod
    public static GaugeDirectionCT left() {
        return LEFT;
    }

    @ZenMethod
    public static GaugeDirectionCT down() {
        return DOWN;
    }

    @ZenMethod
    public static GaugeDirectionCT right() {
        return RIGHT;
    }
}
