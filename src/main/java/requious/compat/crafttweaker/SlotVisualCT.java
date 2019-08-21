package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.ResourceLocation;
import requious.util.LaserVisual;
import requious.util.SlotVisual;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.awt.*;

@ZenRegister
@ZenClass("mods.requious.SlotVisual")
public class SlotVisualCT {
    SlotVisual internal;

    public SlotVisualCT(SlotVisual internal) {
        this.internal = internal;
    }

    public static SlotVisual unpack(SlotVisualCT visual) {
        if(visual != null)
            return visual.get();
        return new SlotVisual();
    }

    public SlotVisual get() {
        return internal;
    }

    @ZenMethod
    public static SlotVisualCT create() {
        return new SlotVisualCT(new SlotVisual());
    }

    @ZenMethod
    public SlotVisualCT addPart(String texture, int x, int y, @Optional int[] rgb) {
        Color color = parseColor(rgb);
        internal.addPart(new ResourceLocation(texture), x, y, color);
        return this;
    }

    private static Color parseColor(int[] rgb) {
        Color color = Color.WHITE;
        if (rgb != null && rgb.length >= 3 && rgb.length <= 4)
            color = new Color(rgb[0], rgb[1], rgb[2], rgb.length == 4 ? rgb[3] : 255);
        return color;
    }
}
