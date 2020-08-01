package requious.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentSelection;
import requious.gui.GuiAssembly;
import requious.network.PacketHandler;
import requious.network.message.MessageScrollSlot;
import requious.network.message.MessageSelectSlot;
import requious.util.Fill;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;

public class SelectSlot extends BaseSlot<ComponentSelection.Slot> {

    public SelectSlot(AssemblyProcessor assembly, ComponentSelection.Slot binding, int xPosition, int yPosition) {
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
        return binding.getSelection();
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
        //assembly.drawTexturedModalRect(x-1, y-1, 176, 0, 18, 18);
        SlotVisual background = binding.getBackground();
        if(background != null)
            background.render(assembly.mc,x-1, y-1, 100, new Fill(binding.isSelected() ? 1 : 0,1));
    }

    @Override
    public void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey) {
        SlotVisual foreground = binding.getForeground();
        if(foreground != null)
            foreground.render(assembly.mc,x-1, y-1, 1000, new Fill(binding.isSelected() ? 1 : 0,1));
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public void serverScroll(int i) {
        binding.scroll(i);
    }

    @Override
    public void clientScroll(int i) {
        PacketHandler.INSTANCE.sendToServer(new MessageScrollSlot(slotNumber,i));
    }

    @Override
    public void clientClick(EntityPlayer player, ItemStack dragStack, int mouseButton, ClickType type) {
        //System.out.println(type);
        if(type == ClickType.PICKUP) {
            if (mouseButton == 0)
                PacketHandler.INSTANCE.sendToServer(new MessageSelectSlot(slotNumber, true));
            if (mouseButton == 1)
                PacketHandler.INSTANCE.sendToServer(new MessageSelectSlot(slotNumber, false));
        }
    }

    public void setSelected(boolean selected) {
        if(binding.isSelected() && !selected)
            binding.unselect();
        else if(selected)
            binding.select();
    }
}
