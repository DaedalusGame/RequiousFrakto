package requious.compat.jei.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import requious.Requious;
import requious.compat.jei.JEISlot;
import requious.util.Misc;
import requious.util.SlotVisual;

public class SelectionSlot extends ItemSlot {
    public SelectionSlot(int x, int y, String group, SlotVisual visual) {
        super(x, y, group, visual);
    }

    @Override
    public JEISlot copy() {
        SelectionSlot selectionSlot = new SelectionSlot(x,y,group,visual);
        return selectionSlot;
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(new ResourceLocation(Requious.MODID,"textures/gui/assembly_slots.png"));
        Misc.drawTexturedModalRect(x*18,y*18, 0, 0, 18,18);
        visual.render(minecraft,x*18,y*18);
        Misc.drawTexturedModalRect(x*18,y*18, 18, 18, 18,18);
    }
}
