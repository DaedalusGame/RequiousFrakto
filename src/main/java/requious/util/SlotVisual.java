package requious.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import requious.Requious;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SlotVisual {
    List<Part> parts = new ArrayList<>();

    public void addPart(ResourceLocation texture, int x, int y, Color color) {
        parts.add(new Part(texture, x, y, color));
    }

    public void render(Minecraft minecraft, int x, int y) {
        for (Part part : parts) {
            minecraft.getTextureManager().bindTexture(part.texture);
            GlStateManager.color(part.color.getRed() / 255f, part.color.getGreen() / 255f, part.color.getBlue() / 255f, part.color.getAlpha() / 255f);
            Misc.drawTexturedModalRect(x, y, part.x * 18, part.y * 18, 18, 18);
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    public static class Part {
        ResourceLocation texture;
        int x, y;
        Color color;

        public Part(ResourceLocation texture, int x, int y, Color color) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
}
