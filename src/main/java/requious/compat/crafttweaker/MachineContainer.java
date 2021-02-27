package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.command.ICommandManager;
import crafttweaker.api.command.ICommandSender;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IVector3d;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.server.MCServer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidStack;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentEnergy;
import requious.data.component.ComponentFluid;
import requious.data.component.ComponentItem;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.requious.MachineContainer")
public class MachineContainer implements ICommandSender {
    public AssemblyProcessor assembly;
    public RandomCT random;

    public MachineContainer(AssemblyProcessor assembly) {
        this.assembly = assembly;
        this.random = new RandomCT();
    }

    public TileEntity getTile() {
        return assembly.getTile();
    }

    public EnumFacing getTileFacing() {
        return assembly.getFacing();
    }

    @Override
    public String getDisplayName() {
        return assembly.getCommandSender().getName();
    }

    @Override
    public IBlockPos getPosition() {
        return CraftTweakerMC.getIBlockPos(assembly.getCommandSender().getPosition());
    }

    @ZenGetter("world")
    public IWorld getWorld() {
        return CraftTweakerMC.getIWorld(getTile().getWorld());
    }

    @Override
    public IServer getServer() {
        return new MCServer(assembly.getCommandSender().getServer());
    }

    @Override
    public void sendMessage(String text) {
        assembly.getCommandSender().sendMessage(new TextComponentString(text));
    }

    @Override
    public Object getInternal() {
        return assembly.getCommandSender();
    }

    @ZenGetter("pos")
    public IBlockPos getPos() {
        return CraftTweakerMC.getIBlockPos(getTile().getPos());
    }

    @ZenGetter("block")
    public IBlock getBlock() {
        BlockPos pos = getTile().getPos();
        return CraftTweakerMC.getBlock(getTile().getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    @ZenGetter("state")
    public IBlockState getBlockState() {
        return CraftTweakerMC.getBlockState(getTile().getWorld().getBlockState(getTile().getPos()));
    }

    @ZenGetter("facing")
    public IFacing getFacing() {
        return CraftTweakerMC.getIFacing(getTileFacing());
    }

    @ZenGetter("random")
    public RandomCT getRandom() {
        return random;
    }

    //Accessing variables
    @ZenMethod
    public int getInteger(String name) {
        Object value = assembly.getVariable(name);
        if(value instanceof Integer)
            return (int)value;
        else
            return 0;
    }

    @ZenMethod
    public double getDouble(String name) {
        Object value = assembly.getVariable(name);
        if(value instanceof Double)
            return (double)value;
        else
            return 0;
    }

    @ZenMethod
    public String getString(String name) {
        Object value = assembly.getVariable(name);
        if(value instanceof String)
            return (String)value;
        else
            return "";
    }

    @ZenMethod
    public IItemStack getItem(String name) {
        Object value = assembly.getVariable(name);
        if(value instanceof ItemStack)
            return CraftTweakerMC.getIItemStack((ItemStack) value);
        else
            return CraftTweakerMC.getIItemStack(ItemStack.EMPTY);
    }

    @ZenMethod
    public ILiquidStack getFluid(String name) {
        Object value = assembly.getVariable(name);
        if(value instanceof FluidStack)
            return CraftTweakerMC.getILiquidStack((FluidStack) value);
        else
            return CraftTweakerMC.getILiquidStack(null);
    }

    @ZenMethod
    public IVector3d getVector(String name) {
        Object value = assembly.getVariable(name);
        if(value instanceof Vec3d)
            return CraftTweakerMC.getIVector3d((Vec3d) value);
        else
            return CraftTweakerMC.getIVector3d(Vec3d.ZERO);
    }

    @ZenMethod
    public void setInteger(String name, int value) {
        assembly.setVariable(name,value);
    }

    @ZenMethod
    public void setDouble(String name, double value) {
        assembly.setVariable(name,value);
    }

    @ZenMethod
    public void setString(String name, String value) {
        assembly.setVariable(name,value);
    }

    @ZenMethod
    public void setFacing(String name, IFacing value) {
        assembly.setVariable(name,value.getInternal());
    }

    @ZenMethod
    public void setColor(String name, ColorCT value) {
        assembly.setVariable(name,value.get());
    }

    @ZenMethod
    public void setItem(String name, IItemStack value) {
        assembly.setVariable(name,CraftTweakerMC.getItemStack(value));
    }

    @ZenMethod
    public void setFluid(String name, ILiquidStack value) {
        assembly.setVariable(name,CraftTweakerMC.getLiquidStack(value));
    }

    @ZenMethod
    public void setVector(String name, double x, double y, double z) {
        assembly.setVariable(name,new Vec3d(x,y,z));
    }

    //Accessing specific slots
    @ZenMethod
    public IItemStack getItem(int x, int y) {
        ComponentBase.Slot slot = assembly.getSlot(x, y);
        ItemStack stack = ItemStack.EMPTY;
        if (slot instanceof ComponentItem.IItemSlot) {
            stack = ((ComponentItem.IItemSlot) slot).getItem().getStack();
        }
        return CraftTweakerMC.getIItemStack(stack);
    }

    @ZenMethod
    public ILiquidStack getFluid(int x, int y) {
        ComponentBase.Slot slot = assembly.getSlot(x,y);
        FluidStack stack = null;
        if(slot instanceof ComponentFluid.Slot) {
            stack = ((ComponentFluid.Slot) slot).getContents();
        }
        return CraftTweakerMC.getILiquidStack(stack);
    }

    @ZenMethod
    public int getEnergy(int x, int y) {
        ComponentBase.Slot slot = assembly.getSlot(x,y);
        if(slot instanceof ComponentEnergy.Slot) {
            return ((ComponentEnergy.Slot) slot).getAmount();
        }
        return 0;
    }

    @ZenMethod
    public void setItem(int x, int y, IItemStack stack) {
        ComponentBase.Slot slot = assembly.getSlot(x, y);
        if (slot instanceof ComponentItem.IItemSlot) {
            ((ComponentItem.IItemSlot) slot).getItem().setStack(CraftTweakerMC.getItemStack(stack));
        }
    }

    @ZenMethod
    public void setFluid(int x, int y, ILiquidStack stack) {
        ComponentBase.Slot slot = assembly.getSlot(x, y);
        if (slot instanceof ComponentFluid.Slot) {
            ((ComponentFluid.Slot) slot).setContents(CraftTweakerMC.getLiquidStack(stack));
        }
    }

    @ZenMethod
    public void setEnergy(int x, int y, int amount) {
        ComponentBase.Slot slot = assembly.getSlot(x, y);
        if (slot instanceof ComponentEnergy.Slot) {
            ((ComponentEnergy.Slot) slot).setAmount(amount);
        }
    }

    @ZenMethod
    public IItemStack insertItem(String group, IItemStack stack) {
        ItemStack remainder = assembly.insertItem(group, CraftTweakerMC.getItemStack(stack));
        return CraftTweakerMC.getIItemStack(remainder);
    }

    @ZenMethod
    public IItemStack extractItem(String group, IIngredient filter) {
        ItemStack extracted = assembly.extractItem(group, stack -> filter.matches(CraftTweakerMC.getIItemStack(stack)), filter.getAmount());
        return CraftTweakerMC.getIItemStack(extracted);
    }

    @ZenMethod
    public ILiquidStack insertFluid(String group, ILiquidStack stack) {
        FluidStack remainder = assembly.insertFluid(group, CraftTweakerMC.getLiquidStack(stack));
        return CraftTweakerMC.getILiquidStack(remainder);
    }

    @ZenMethod
    public ILiquidStack extractFluid(String group, IIngredient filter) {
        FluidStack extracted = assembly.extractFluid(group, stack -> filter.matches(CraftTweakerMC.getILiquidStack(stack)), filter.getAmount());
        return CraftTweakerMC.getILiquidStack(extracted);
    }

    @ZenMethod
    public int insertEnergy(String group, int energy) {
        int remainder = assembly.insertEnergy(group, energy);
        return remainder;
    }

    @ZenMethod
    public int extractEnergy(String group, int energy) {
        int extracted = assembly.extractEnergy(group, energy);
        return extracted;
    }
}
