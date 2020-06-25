package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IVector3d;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import requious.util.MachineVisual;
import requious.util.MachineVisualBeacon;
import requious.util.Parameter;
import requious.util.color.NormalColor;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.awt.*;

@ZenRegister
@ZenClass("mods.requious.MachineVisual")
public class MachineVisualCT {
    MachineVisual internal;

    public MachineVisualCT(MachineVisual internal) {
        this.internal = internal;
    }

    public MachineVisual get() {
        return internal;
    }

    @ZenMethod
    public static MachineVisualCT displayItem(Parameter active, Parameter itemStack, Parameter position, Parameter scale, Parameter rotation, @Optional boolean global) {
        return new MachineVisualCT(new MachineVisual.DisplayItem(active,itemStack,position,scale,rotation,global));
    }

    @ZenMethod
    public static MachineVisualCT displayFluid(Parameter active, Parameter fluidStack, Parameter capacity, Parameter facing, Parameter start, Parameter end, @Optional boolean global) {
        return new MachineVisualCT(new MachineVisual.DisplayFluid(active,fluidStack,capacity,facing,start,end,global));
    }

    @ZenMethod
    public static MachineVisualCT displayCube(Parameter active, Parameter texture, Parameter amount, Parameter capacity, Parameter facing, Parameter start, Parameter end, @Optional boolean global) {
        return new MachineVisualCT(new MachineVisual.DisplayCube(active,texture,amount,capacity,facing,start,end,global));
    }

    @ZenMethod
    public static MachineVisualCT displayModel(Parameter active, Parameter model, Parameter position, Parameter scale, Parameter rotation, @Optional boolean global) {
        return new MachineVisualCT(new MachineVisual.DisplayModel(active,model,position,scale,rotation,global));
    }

    @ZenMethod
    public static MachineVisualCT flame(Parameter active, Parameter begin, Parameter end, Parameter velocity, Parameter minSize, Parameter maxSize, Parameter color, Parameter lifetime, @Optional boolean global) {
        return new MachineVisualCT(new MachineVisual.Flame(active, begin, end, velocity, minSize, maxSize, color, lifetime, global));
    }

    @ZenMethod
    public static MachineVisualCT smoke(Parameter active, Parameter begin, Parameter end, Parameter velocity, Parameter color, Parameter lifetime, boolean fullBright, @Optional boolean global) {
        return new MachineVisualCT(new MachineVisual.Smoke(active, begin, end, velocity, lifetime, color, fullBright, global));
    }

    @ZenMethod
    public static MachineVisualCT beacon(Parameter active, Parameter facing, Parameter length, boolean cancelOnHit, @Optional boolean global) {
        return new MachineVisualCT(new MachineVisualBeacon(active, facing, length, cancelOnHit, global));
    }
}
