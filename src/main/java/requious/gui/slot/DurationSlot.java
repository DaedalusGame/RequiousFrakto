package requious.gui.slot;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import requious.Requious;
import requious.data.component.ComponentDuration;
import requious.data.component.ComponentEnergy;
import requious.gui.GuiAssembly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DurationSlot extends BaseSlot<ComponentDuration.Slot> {
    public DurationSlot(ComponentDuration.Slot binding, int xPosition, int yPosition) {
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
    public void renderBackground(GuiAssembly assembly, int x, int y, float partialTicks, int mousex, int mousey) {
        assembly.mc.getTextureManager().bindTexture(binding.getTexture());
        int texX = binding.getTextureX();
        int texY = binding.getTextureY();
        assembly.drawTexturedModalRect(x-1, y-1, texX*18*2, texY*18, 18, 18);
        int energy = binding.getTime();
        int capacity = binding.getDuration();
        if(capacity > 0) {
            float ratio = (float) energy / capacity;
            int fill = (int) (ratio * 18);
            if (energy > 0 && fill <= 0)
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
    }

    @Override
    public boolean hasToolTip() {
        return shouldRender();
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        /*String unit = binding.getUnit();

        if(unit != null && I18n.hasKey("unit."+unit))
            tooltip.add(I18n.format("unit."+unit,binding.getAmount(),binding.getCapacity()));*/

        return tooltip;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return ItemStack.EMPTY;
    }
}
