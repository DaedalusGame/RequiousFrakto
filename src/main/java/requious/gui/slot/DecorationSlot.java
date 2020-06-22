package requious.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentDecoration;
import requious.gui.GuiAssembly;
import requious.util.Fill;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;

public class DecorationSlot extends BaseSlot<ComponentDecoration.Slot> {
    public DecorationSlot(AssemblyProcessor assembly, ComponentDecoration.Slot binding, int xPosition, int yPosition) {
        super(assembly, binding, xPosition, yPosition);
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
            visual.render(assembly.mc,x-1, y-1, 100, new Fill(0,0));
    }

    @Override
    public void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
