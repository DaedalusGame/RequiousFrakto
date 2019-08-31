package requious.gui.slot;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import requious.data.component.ComponentFluid;
import requious.gui.GuiAssembly;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FluidSlot extends BaseSlot<ComponentFluid.Slot> {
    public FluidSlot(ComponentFluid.Slot binding, int xPosition, int yPosition) {
        super(binding, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null);
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
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
        return binding.getCapacity();
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
        assembly.drawTexturedModalRect(x-1, y-1, 176+18, 0, 18, 18);
    }

    @Override
    public void renderForeground(GuiAssembly assembly,int x, int y, int mousex, int mousey) {
        SlotVisual visual = binding.getForeground();
        if(visual != null)
            visual.render(assembly.mc,x-1, y-1);
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return binding.getItem().extract(amount,false);
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        FluidStack fluid = binding.getContents();
        int capacity = binding.getCapacity();

        if(fluid == null)
            tooltip.add(I18n.format("requious.fluid.empty"));
        else
            tooltip.add(I18n.format("requious.fluid",fluid.getLocalizedName(),fluid.amount,capacity));

        return tooltip;
    }
}
