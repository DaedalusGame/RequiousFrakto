package requious.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemShape extends Item {
    public ItemStack create(Shape shape) {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound tag = stack.getOrCreateSubCompound("shape");
        shape.writeToNBT(tag);
        return stack;
    }
}
