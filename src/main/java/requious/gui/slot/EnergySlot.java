package requious.gui.slot;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentEnergy;
import requious.gui.GuiAssembly;
import requious.util.Fill;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EnergySlot extends BaseSlot<ComponentEnergy.Slot> {
    public EnergySlot(AssemblyProcessor assembly, ComponentEnergy.Slot binding, int xPosition, int yPosition) {
        super(assembly, binding, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.hasCapability(CapabilityEnergy.ENERGY,null);
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
        SlotVisual visual = binding.getBackground();
        int energy = binding.getAmount();
        int capacity = binding.getCapacity();
        visual.render(assembly.mc,x-1, y-1, new Fill(energy,capacity));
    }

    @Override
    public void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey) {
        SlotVisual visual = binding.getForeground();
        int energy = binding.getAmount();
        int capacity = binding.getCapacity();
        if(visual != null)
            visual.render(assembly.mc,x-1, y-1, new Fill(energy,capacity));
    }

    @Override
    public boolean hasToolTip() {
        return shouldRender();
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        String unit = binding.getUnit();

        if(unit != null && I18n.hasKey("requious.unit."+unit))
            tooltip.add(I18n.format("requious.unit."+unit,binding.getAmount(),binding.getCapacity()));

        return tooltip;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return binding.getItem().extract(amount,false);
    }

    @Override
    public boolean isEnabled() {
        return binding.isBatteryAccepted() && (binding.canPut() || binding.canTake());
    }
}
