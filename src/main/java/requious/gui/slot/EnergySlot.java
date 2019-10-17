package requious.gui.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import requious.Requious;
import requious.data.component.ComponentEnergy;
import requious.gui.GuiAssembly;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EnergySlot extends BaseSlot<ComponentEnergy.Slot> {
    public EnergySlot(ComponentEnergy.Slot binding, int xPosition, int yPosition) {
        super(binding, xPosition, yPosition);
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
        assembly.mc.getTextureManager().bindTexture(binding.getTexture());
        int texX = binding.getTextureX();
        int texY = binding.getTextureY();
        assembly.drawTexturedModalRect(x-1, y-1, texX*18*2, texY*18, 18, 18);
        int energy = binding.getAmount();
        int capacity = binding.getCapacity();
        boolean inverse = binding.isInverse();
        if(capacity > 0) {
            float ratio = (float) energy / capacity;
            if (inverse)
                ratio = 1 - ratio;
            int fill = (int) (ratio * 18);
            if (binding.getAmount() > 0 && fill <= 0)
                fill = 1;
            int empty = 18 - fill;

            int ox = 0;
            int oy = 0;
            int ow = 18;
            int oh = 18;

            switch (binding.getTextureDirection()) {
                case UP:
                    oy = empty;
                    oh = fill;
                    break;
                case DOWN:
                    oh = fill;
                    break;
                case LEFT:
                    ox = empty;
                    ow = fill;
                    break;
                case RIGHT:
                    ow = fill;
                    break;
            }

            assembly.drawTexturedModalRect(x - 1 + ox, y - 1 + oy, texX * 18 * 2 + 18 + ox, texY * 18 + oy, ow, oh);
        }
    }

    @Override
    public void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey) {
        SlotVisual visual = binding.getForeground();
        if(visual != null)
            visual.render(assembly.mc,x-1, y-1);
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
}
