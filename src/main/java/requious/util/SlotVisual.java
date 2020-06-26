package requious.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import requious.Requious;
import requious.gui.GaugeDirection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SlotVisual {
    List<Part> parts = new ArrayList<>();

    public static ResourceLocation GUI_SLOTS = new ResourceLocation(Requious.MODID, "textures/gui/assembly_slots.png");
    public static ResourceLocation GUI_GAUGES = new ResourceLocation(Requious.MODID, "textures/gui/assembly_gauges.png");

    public static final SlotVisual EMPTY = new SlotVisual(1, 1);
    public static final SlotVisual ITEM_SLOT;
    public static final SlotVisual FLUID_SLOT;
    public static final SlotVisual ENERGY_SLOT;
    public static final SlotVisual INFO_SLOT;
    public static final SlotVisual SELECTION_SLOT;
    public static final SlotVisual ARROW_RIGHT;
    public static final SlotVisual ARROW_DOWN;
    public static final SlotVisual ARROW_LEFT;
    public static final SlotVisual ARROW_UP;

    static {
        ITEM_SLOT = new SlotVisual(1, 1);
        ITEM_SLOT.addPart(GUI_SLOTS, 0, 0, Color.WHITE);
        FLUID_SLOT = new SlotVisual(1, 1);
        FLUID_SLOT.addPart(GUI_SLOTS, 1, 0, Color.WHITE);
        ENERGY_SLOT = new SlotVisual(1, 1);
        ENERGY_SLOT.addGauge(GUI_GAUGES, 0, 0, 1, 0, Color.WHITE, GaugeDirection.UP, false);
        INFO_SLOT = new SlotVisual(1, 1);
        INFO_SLOT.addPart(GUI_SLOTS, 1, 2, Color.WHITE);
        SELECTION_SLOT = new SlotVisual(1, 1);
        SELECTION_SLOT.addPart(GUI_SLOTS, 0, 0, Color.WHITE);
        SELECTION_SLOT.addDirectionalPart(GUI_SLOTS, 1, 1, Color.WHITE, GaugeDirection.UP, false);

        ARROW_RIGHT = new SlotVisual(1, 1);
        ARROW_RIGHT.addGauge(GUI_GAUGES, 0, 8, 1, 8, Color.WHITE, GaugeDirection.RIGHT, false);
        ARROW_DOWN = new SlotVisual(1, 1);
        ARROW_DOWN.addGauge(GUI_GAUGES, 2, 8, 3, 8, Color.WHITE, GaugeDirection.DOWN, false);
        ARROW_LEFT = new SlotVisual(1, 1);
        ARROW_LEFT.addGauge(GUI_GAUGES, 4, 8, 5, 8, Color.WHITE, GaugeDirection.LEFT, false);
        ARROW_UP = new SlotVisual(1, 1);
        ARROW_UP.addGauge(GUI_GAUGES, 6, 8, 7, 8, Color.WHITE, GaugeDirection.UP, false);
    }

    int width, height;

    public SlotVisual(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addPart(ResourceLocation texture, int x, int y, Color color) {
        parts.add(new Part(texture, x, y, width, height, color));
    }

    public void addDirectionalPart(ResourceLocation texture, int x, int y, Color color, GaugeDirection direction, boolean inverse) {
        parts.add(new PartDirectional(texture, x, y, width, height, color, direction, inverse));
    }

    public void addGauge(ResourceLocation texture, int x1, int y1, int x2, int y2, Color color, GaugeDirection direction, boolean inverse) {
        parts.add(new Part(texture, x1, y1, width, height, Color.WHITE));
        parts.add(new PartDirectional(texture, x2, y2, width, height, color, direction, inverse));
    }

    public void render(Minecraft minecraft, int x, int y, int z, Fill fill) {
        for (Part part : parts) {
            part.render(minecraft, x, y, z, fill);
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    public SlotVisual copy() {
        SlotVisual copy = new SlotVisual(width, height);
        for (Part part : parts) {
            copy.parts.add(part.copy());
        }
        return copy;
    }

    public static class Part {
        protected ResourceLocation texture;
        protected int x, y;
        protected int width, height;
        protected Color color;

        public Part(ResourceLocation texture, int x, int y, int width, int height, Color color) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        public void render(Minecraft minecraft, int x, int y, int z, Fill fill) {
            minecraft.getTextureManager().bindTexture(texture);
            GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            Misc.drawTexturedModalRect(x, y, z, this.x * 18, this.y * 18, width * 18, height * 18);
        }

        public Part copy() {
            return new Part(texture, x, y, width, height, color);
        }
    }

    public static class PartDirectional extends Part {
        GaugeDirection direction;
        boolean inverse;

        public PartDirectional(ResourceLocation texture, int x, int y, int width, int height, Color color, GaugeDirection direction, boolean inverse) {
            super(texture, x, y, width, height, color);
            this.direction = direction;
            this.inverse = inverse;
        }

        public void render(Minecraft minecraft, int x, int y, int z, Fill fill) {
            minecraft.getTextureManager().bindTexture(texture);
            GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

            int widthTotal = width * 18;
            int heightTotal = height * 18;

            int fillX = fill.getFill(widthTotal, inverse);
            int fillY = fill.getFill(heightTotal, inverse);
            int emptyX = widthTotal - fillX;
            int emptyY = heightTotal - fillY;

            int ox = 0;
            int oy = 0;
            int ow = widthTotal;
            int oh = heightTotal;

            switch (direction) {
                case UP:
                    oy = emptyY;
                    oh = fillY;
                    break;
                case DOWN:
                    oh = fillY;
                    break;
                case LEFT:
                    ox = emptyX;
                    ow = fillX;
                    break;
                case RIGHT:
                    ow = fillX;
                    break;
            }

            Misc.drawTexturedModalRect(x + ox, y + oy, z, this.x * 18 + ox, this.y * 18 + oy, ow, oh);
        }

        public Part copy() {
            return new PartDirectional(texture, x, y, width, height, color, direction, inverse);
        }
    }
}
