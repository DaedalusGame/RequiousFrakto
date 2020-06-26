package requious.util;


import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import requious.data.AssemblyProcessor;
import requious.util.color.ICustomColor;
import stanhebben.zenscript.annotations.ZenClass;

import java.awt.Color;

//Helper class for making a parameter either constant or variable
@ZenRegister
@ZenClass("mods.requious.Parameter")
public abstract class Parameter {
    private static int toInt(Object value) {
        if(value instanceof Number)
            return ((Number) value).intValue();
        return 0;
    }

    private static double toDouble(Object value) {
        if(value instanceof Number)
            return ((Number) value).doubleValue();
        return 0;
    }

    private static String toString(Object value) {
        if(value instanceof String)
            return (String) value;
        return "";
    }

    private static Vec3d toVector(Object value) {
        if(value instanceof Vec3d)
            return (Vec3d) value;
        return Vec3d.ZERO;
    }

    private static EnumFacing toFacing(Object value) {
        if(value instanceof EnumFacing)
            return (EnumFacing) value;
        return EnumFacing.UP;
    }

    private static Color toColor(Object value) {
        if(value instanceof ICustomColor)
            return ((ICustomColor) value).get();
        return Color.WHITE;
    }

    private static ItemStack toItem(Object value) {
        if(value instanceof ItemStack)
            return (ItemStack) value;
        if(value instanceof IItemStack)
            return CraftTweakerMC.getItemStack((IItemStack) value);
        return ItemStack.EMPTY;
    }

    private static FluidStack toFluid(Object value) {
        if(value instanceof FluidStack)
            return (FluidStack) value;
        return null;
    }

    public int getInteger(AssemblyProcessor assembly) {
        return toInt(getValue(assembly));
    }

    public int getInteger(AssemblyProcessor assembly, float partialTicks) {
        int current = toInt(getValue(assembly));
        int last = toInt(getLastValue(assembly));

        return (int) MathHelper.clampedLerp(last,current,partialTicks);
    }

    public double getDouble(AssemblyProcessor assembly) {
        return toDouble(getValue(assembly));
    }

    public double getDouble(AssemblyProcessor assembly, float partialTicks) {
        double current = toDouble(getValue(assembly));
        double last = toDouble(getLastValue(assembly));

        return MathHelper.clampedLerp(last,current,partialTicks);
    }

    public String getString(AssemblyProcessor assembly) {
        return toString(getValue(assembly));
    }

    public Vec3d getVector(AssemblyProcessor assembly) {
        return toVector(getValue(assembly));
    }

    public Vec3d getVector(AssemblyProcessor assembly, float partialTicks) {
        Vec3d current = toVector(getValue(assembly));
        Vec3d last = toVector(getLastValue(assembly));

        double x = MathHelper.clampedLerp(last.x, current.x, partialTicks);
        double y = MathHelper.clampedLerp(last.y, current.y, partialTicks);
        double z = MathHelper.clampedLerp(last.z, current.z, partialTicks);

        return new Vec3d(x,y,z);
    }

    public EnumFacing getFacing(AssemblyProcessor assembly) {
        return toFacing(getValue(assembly));
    }

    public Color getColor(AssemblyProcessor assembly) {
        return toColor(getValue(assembly));
    }

    public Color getColor(AssemblyProcessor assembly, float partialTicks) {
        Color current = toColor(getValue(assembly));
        Color last = toColor(getLastValue(assembly));

        return Misc.lerpColorRGB(last,current,partialTicks);
    }

    public ItemStack getItem(AssemblyProcessor assembly) {
        return toItem(getValue(assembly));
    }

    public FluidStack getFluid(AssemblyProcessor assembly) {
        return toFluid(getValue(assembly));
    }

    public abstract Object getValue(AssemblyProcessor assembly);

    public abstract Object getLastValue(AssemblyProcessor assembly);

    public abstract void stashValue(AssemblyProcessor assembly);

    @ZenRegister
    @ZenClass("mods.requious.Variable")
    public static class Variable extends Parameter {
        String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public Object getValue(AssemblyProcessor assembly) {
            return assembly.getVariable(name);
        }

        @Override
        public Object getLastValue(AssemblyProcessor assembly) {
            return assembly.getHistory(name);
        }

        @Override
        public void stashValue(AssemblyProcessor assembly) {
            assembly.stashVariable(name);
        }
    }

    public static class Constant extends Parameter {
        Object value;

        public Constant(Object value) {
            this.value = value;
        }

        @Override
        public Object getValue(AssemblyProcessor assembly) {
            return value;
        }

        @Override
        public Object getLastValue(AssemblyProcessor assembly) {
            return value;
        }

        @Override
        public void stashValue(AssemblyProcessor assembly) {
            //NOOP
        }
    }
}
