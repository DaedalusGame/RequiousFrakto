package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.ResourceLocation;
import requious.util.LaserVisual;
import requious.util.Misc;
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
        Color color = Misc.parseColor(rgb);
        internal.addPart(new ResourceLocation(texture), x, y, color);
        return this;
    }
}
