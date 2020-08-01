package requious.compat.crafttweaker;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import requious.item.Shape;

public class ShapeItemStack implements IShape {
    ItemStack stack;
    Shape shape;

    public ShapeItemStack(ItemStack stack) {
        this.stack = stack;
        NBTTagCompound tagCompound = stack.getTagCompound();
        this.shape = tagCompound != null ? new Shape(tagCompound.getCompoundTag("shape")) : new Shape();
    }

    @Override
    public Shape getShape() {


        return shape;
    }

    @Override
    public IItemStack toItem() {
        if(shape.isEmpty())
            return CraftTweakerMC.getIItemStack(stack);
        else
            return IShape.super.toItem();
    }
}
