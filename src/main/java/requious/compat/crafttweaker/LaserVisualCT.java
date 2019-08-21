package requious.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import requious.util.LaserVisual;
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
        Color color = parseColor(rgb);
        return new LaserVisualCT(new LaserVisual.Beam(color,thickness));
    }

    @ZenMethod
    public static LaserVisualCT lightning(int[] rgb, float thickness, float wildness, int segments) {
        Color color = parseColor(rgb);
        return new LaserVisualCT(new LaserVisual.Lightning(color,thickness,wildness,segments));
    }

    private static Color parseColor(int[] rgb) {
        Color color = Color.WHITE;
        if (rgb != null && rgb.length >= 3 && rgb.length <= 4)
            color = new Color(rgb[0], rgb[1], rgb[2], rgb.length == 4 ? rgb[3] : 255);
        return color;
    }
}
