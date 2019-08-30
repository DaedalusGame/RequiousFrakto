package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import requious.util.LaserVisual;
import requious.util.Misc;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.awt.*;

@ZenRegister
@ZenClass("mods.requious.LaserVisual")
public class LaserVisualCT {
    LaserVisual internal;

    public LaserVisualCT(LaserVisual internal) {
        this.internal = internal;
    }

    public LaserVisual get() {
        return internal;
    }

    @ZenMethod
    public static LaserVisualCT none() {
        return new LaserVisualCT(new LaserVisual.None());
    }

    @ZenMethod
    public static LaserVisualCT beam(int[] rgb, float thickness) {
        Color color = Misc.parseColor(rgb);
        return new LaserVisualCT(new LaserVisual.Beam(color,thickness));
    }

    @ZenMethod
    public static LaserVisualCT lightning(int[] rgb, float thickness, float wildness, int segments) {
        Color color = Misc.parseColor(rgb);
        return new LaserVisualCT(new LaserVisual.Lightning(color,thickness,wildness,segments));
    }

}
