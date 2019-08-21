package requious.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class ItemComponentHelper {
    ItemStack item = ItemStack.EMPTY;
    int amount;
    boolean dirty;

    public ItemStack getStack() {
        ItemStack copy = item.copy();
        copy.setCount(amount);
        return copy; //This stack isn't safe to serialize because mojang is dumb
    }

    public void setStack(ItemStack stack) {
        item = stack.copy();
        item.setCount(1);
        amount = stack.getCount();
        markDirty();
    }

    public int getAmount() {
        if(item.isEmpty())
            return 0;
        return Math.max(0,amount);
    }

    public abstract int getCapacity();

    public ItemStack extract(int maxExtract, boolean simulate) {
        ItemStack copy = item.copy();
        copy.setCount(Math.min(maxExtract,amount));
        if(!simulate) {
            amount -= Math.min(maxExtract,amount);
            if(amount <= 0)
                item = ItemStack.EMPTY;
            markDirty();
        }
        return copy;
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if(!canStack(stack) || stack.isEmpty())
            return stack;
        stack = stack.copy();
        ItemStack inserted = stack.splitStack(getCapacity() - amount);
        if(!simulate) {
            amount += inserted.getCount();
            item = inserted;
            item.setCount(1);
            markDirty();
        }
        return stack;
    }

    public int insert(int stack, boolean simulate) {
        int inserted = Math.min(stack,getCapacity() - amount);
        if(!simulate) {
            amount += inserted;
            markDirty();
        }
        return stack - inserted;
    }

    public boolean canStack(ItemStack stack) {
        return item.isEmpty() || ItemHandlerHelper.canItemStacksStack(stack, item);
    }

    public boolean isEmpty() {
        return item.isEmpty();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        dirty = true;
    }

    public void markClean() { dirty = false; }

    public void spawnInWorld(World world, Vec3d pos) {
        for(int i = amount; i > 0; i -= item.getMaxStackSize()) {
            ItemStack stack = item.copy();
            stack.setCount(Math.min(i,item.getMaxStackSize()));
            EntityItem item = new EntityItem(world,pos.x,pos.y,pos.z,stack);
            world.spawnEntity(item);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("stack", item.serializeNBT());
        nbt.setInteger("amount",amount);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        item = new ItemStack(nbt.getCompoundTag("stack"));
        amount = nbt.getInteger("amount");
    }
}
