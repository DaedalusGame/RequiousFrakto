package requious.gui.slot;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentDecoration;
import requious.data.component.ComponentText;
import requious.gui.GuiAssembly;
import requious.util.Fill;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextSlot extends BaseSlot<ComponentText.Slot> {
    public TextSlot(ComponentText.Slot binding, int xPosition, int yPosition) {
        super(binding, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack getStack()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void putStack(ItemStack stack) {
        //NOOP
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {

    }

    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }

    @Override
    public void incrStack(int n) {
        //NOOP
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public void renderBackground(GuiAssembly assembly, int x, int y, float partialTicks, int mousex, int mousey) {
        SlotVisual visual = binding.getVisual();
        if(visual != null)
            visual.render(assembly.mc,x-1, y-1, new Fill(0,0));
    }

    @Override
    public void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey) {
        SlotVisual visual = binding.getVisual();

    }

    @Override
    public boolean hasToolTip() {
        return binding.hasToolTip();
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        for(ComponentText.TextPart part : binding.getToolTip()) {

        }
        return tooltip;
    }

    private String localize(AssemblyProcessor assembly, ComponentText.TextPart part) {
        String[] variableNames = part.getVariables();
        Object[] variableValues = new Object[variableNames.length];
        for(int i = 0; i < variableNames.length; i++) {
            variableValues[i] = assembly.getVariable(variableNames[i]);
        }
        return I18n.format(part.getText(), variableValues);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
