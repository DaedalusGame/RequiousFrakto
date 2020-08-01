package requious.gui.slot;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentBase;
import requious.gui.GuiAssembly;

import java.util.List;

public abstract class BaseSlot<T extends ComponentBase.Slot> extends Slot {
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);

    protected final AssemblyProcessor assembly;
    protected final T binding;

    public BaseSlot(AssemblyProcessor assembly, T binding, int xPosition, int yPosition) {
        super(emptyInventory, 0, xPosition, yPosition);
        this.binding = binding;
        this.assembly = assembly;
    }

    public void incrStack(int n) {
        getStack().grow(n);
    }

    @Override
    public boolean isSameInventory(Slot other) {
        if(other instanceof BaseSlot) //TODO: better check
            return true;
        return false;
    }

    @SideOnly(Side.CLIENT)
    public abstract void renderBackground(GuiAssembly assembly, int x, int y, float partialTicks, int mousex, int mousey);

    @SideOnly(Side.CLIENT)
    public abstract void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey);

    public void clientScroll(int i) {
        //NOOP
    }

    public void serverScroll(int i) {
        //NOOP
    }

    public void clientClick(EntityPlayer player, ItemStack dragStack, int mouseButton, ClickType type) {
        //NOOP
    }

    public void serverClick(EntityPlayerMP player, ItemStack dragStack, int mouseButton, ClickType clickType) {
        //NOOP
    }

    public boolean hasToolTip() {
        return false;
    }

    public List<String> getTooltip() {
        return Lists.newArrayList();
    }

    @Override
    public boolean isEnabled() {
        return shouldRender();
    }

    public boolean shouldRender() {
        return !binding.isHidden();
    }

    public boolean canShiftPut() {
        return binding.canShift();
    }

    public boolean canShiftTake() {
        return binding.canShift();
    }

    public boolean isHoverEnabled() {
        return isEnabled();
    }

    public Vec3i getSize() {
        return new Vec3i(16,16,0);
    }
}
