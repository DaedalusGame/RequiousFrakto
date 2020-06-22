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
        return SlotVisual.EMPTY;
    }

    public SlotVisual get() {
        return internal;
    }

    @ZenMethod
    public static SlotVisualCT itemSlot() {
        return new SlotVisualCT(SlotVisual.ITEM_SLOT);
    }

    @ZenMethod
    public static SlotVisualCT fluidSlot() {
        return new SlotVisualCT(SlotVisual.FLUID_SLOT);
    }

    @ZenMethod
    public static SlotVisualCT energySlot() {
        return new SlotVisualCT(SlotVisual.ENERGY_SLOT);
    }

    @ZenMethod
    public static SlotVisualCT infoSlot() {
        return new SlotVisualCT(SlotVisual.INFO_SLOT);
    }

    @ZenMethod
    public static SlotVisualCT selectionSlot() {
        return new SlotVisualCT(SlotVisual.SELECTION_SLOT);
    }

    @ZenMethod
    public static SlotVisualCT arrowRight() {
        return new SlotVisualCT(SlotVisual.ARROW_RIGHT);
    }

    @ZenMethod
    public static SlotVisualCT arrowDown() {
        return new SlotVisualCT(SlotVisual.ARROW_DOWN);
    }

    @ZenMethod
    public static SlotVisualCT arrowLeft() {
        return new SlotVisualCT(SlotVisual.ARROW_LEFT);
    }

    @ZenMethod
    public static SlotVisualCT arrowUp() {
        return new SlotVisualCT(SlotVisual.ARROW_UP);
    }

    @ZenMethod
    public static SlotVisualCT create(int width, int height) {
        SlotVisual visual = new SlotVisual(width, height);
        return new SlotVisualCT(visual);
    }

    @ZenMethod
    public static SlotVisualCT createSimple(String texture, int x, int y) {
        SlotVisual visual = new SlotVisual(1, 1);
        visual.addPart(new ResourceLocation(texture), x, y, Color.WHITE);
        return new SlotVisualCT(visual);
    }

    @ZenMethod
    public static SlotVisualCT createGauge(String texture, int x1, int y1, int x2, int y2, GaugeDirectionCT direction, boolean inverse, @Optional(valueLong = 1) int width, @Optional(valueLong = 1) int height, @Optional int[] rgb) {
        Color color = Misc.parseColor(rgb);
        SlotVisual visual = new SlotVisual(width, height);
        visual.addGauge(new ResourceLocation(texture), x1, y1, x2, y2, color, direction.get(), inverse);
        return new SlotVisualCT(visual);
    }

    @ZenMethod
    public SlotVisualCT addPart(String texture, int x, int y, @Optional int[] rgb) {
        Color color = Misc.parseColor(rgb);
        internal = internal.copy();
        internal.addPart(new ResourceLocation(texture), x, y, color);
        return this;
    }

    @ZenMethod
    public SlotVisualCT addDirectional(String texture, int x, int y, GaugeDirectionCT direction, boolean inverse, @Optional int[] rgb) {
        Color color = Misc.parseColor(rgb);
        internal = internal.copy();
        internal.addDirectionalPart(new ResourceLocation(texture), x, y, color, direction.get(), inverse);
        return this;
    }
}
