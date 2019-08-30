package requious.compat.crafttweaker;

import com.google.common.collect.Lists;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.ResourceLocation;
import requious.util.LaserVisual;
import requious.util.Misc;
import requious.util.color.*;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.awt.*;
import java.util.ArrayList;

@ZenRegister
@ZenClass("mods.requious.Color")
public class ColorCT {
    ICustomColor internal;

    public ColorCT(ICustomColor internal) {
        this.internal = internal;
    }

    public ICustomColor get() {
        return internal;
    }

    @ZenMethod
    public static ColorCT normal(int[] rgb) {
        return new ColorCT(new NormalColor(Misc.parseColor(rgb)));
    }

    @ZenMethod
    public static ColorCT fluid() {
        return new ColorCT(new FluidColor());
    }

    @ZenMethod
    public static ColorCT energy(int[][] rgbs, boolean hsb) {
        Color[] colors = parseColors(rgbs);
        return new ColorCT(new EnergyColor(Lists.newArrayList(colors),hsb));
    }

    @ZenMethod
    public static ColorCT variable(int[][] rgbs, String override, boolean hsb) {
        Color[] colors = parseColors(rgbs);
        return new ColorCT(new VariableColor(Lists.newArrayList(colors),new ResourceLocation(override),hsb));
    }

    @ZenMethod
    public static ColorCT random(int[][] rgbs) {
        Color[] colors = parseColors(rgbs);
        return new ColorCT(new RandomColor(Lists.newArrayList(colors)));
    }

    @ZenMethod
    public static ColorCT time(int[][] rgbs, double pulseLength, boolean hsb) {
        Color[] colors = parseColors(rgbs);
        return new ColorCT(new TimeColor(Lists.newArrayList(colors),pulseLength,false,hsb));
    }

    @ZenMethod
    public static ColorCT timePulse(int[][] rgbs, double pulseLength, boolean hsb) {
        Color[] colors = parseColors(rgbs);
        return new ColorCT(new TimeColor(Lists.newArrayList(colors),pulseLength,true,hsb));
    }

    @ZenMethod
    public static ColorCT lerp(int[][] rgbs, boolean hsb) {
        Color[] colors = parseColors(rgbs);
        return new ColorCT(new LerpColor(Lists.newArrayList(colors),hsb));
    }

    private static Color[] parseColors(int[][] rgbs) {
        Color[] colors = new Color[rgbs.length];
        for(int i = 0; i<rgbs.length; i++) {
            colors[i] = Misc.parseColor(rgbs[i]);
        }
        return colors;
    }
}
