package requious.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentItem;
import requious.gui.GuiAssembly;
import requious.util.Fill;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;

public class ItemSlot extends BaseSlot<ComponentItem.Slot> {

    public ItemSlot(AssemblyProcessor assembly, ComponentItem.Slot binding, int xPosition, int yPosition) {
        super(assembly, binding, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getStack()
    {
        return binding.getItem().getStack();
    }

    @Override
    public void putStack(ItemStack stack) {
        binding.getItem().setStack(stack);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {

    }

    @Override
    public int getSlotStackLimit() {
        return binding.getItem().getCapacity();
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if(!binding.canPut())
            return binding.getItem().getAmount();
        return binding.getItem().getCapacity();
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if(!binding.canTake())
            return false;
        return !binding.getItem().extract(1, true).isEmpty();
    }

    @Override
    public void incrStack(int n) {
        binding.getItem().insert(n,false);
    }

    @Override
    public void renderBackground(GuiAssembly assembly, int x, int y, float partialTicks, int mousex, int mousey) {
        SlotVisual background = binding.getBackground();
        background.render(assembly.mc,x-1, y-1, 100, new Fill(0,0));
    }

    @Override
    public void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey) {
        SlotVisual foreground = binding.getForeground();
        foreground.render(assembly.mc,x-1, y-1, 1000, new Fill(0,0));
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return binding.getItem().extract(amount,false);
    }

    @Override
    public boolean isEnabled() {
        return !binding.isHidden() && (binding.canPut() || binding.canTake());
    }

    @Override
    public boolean isHoverEnabled() {
        return true;
    }

    @Override
    public boolean canShiftPut() {
        return super.canShiftPut() && binding.canPut();
    }

    @Override
    public boolean canShiftTake() {
        return super.canShiftTake() && binding.canTake();
    }
}
