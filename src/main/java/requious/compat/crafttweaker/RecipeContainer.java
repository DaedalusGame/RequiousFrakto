package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import requious.recipe.*;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.requious.RecipeContainer")
public class RecipeContainer {
    public Map<String,Object> inputs = new HashMap<>();
    public List<ResultBase> outputs = new ArrayList<>();
    public boolean jei;

    public RecipeContainer() {
    }

    public RecipeContainer(boolean jei) {
        this.jei = jei;
    }

    public void addInput(String mark, ItemStack object) {
        inputs.put(mark,object);
    }

    public void addInput(String mark, FluidStack object) {
        inputs.put(mark,object);
    }

    public void addInput(String mark, int object) {
        inputs.put(mark,object);
    }

    public List<ResultBase> getResults() {
        return outputs;
    }

    @ZenGetter("jei")
    public boolean isJEI() {
        return jei;
    }

    @ZenMethod
    public void addItemOutput(String group, IItemStack istack) {
        ItemStack stack = CraftTweakerMC.getItemStack(istack);
        outputs.add(new ResultItem(group,stack));
    }

    @ZenMethod
    public void addItemOutput(String group, IItemStack istack, int minInsert) {
        ItemStack stack = CraftTweakerMC.getItemStack(istack);
        outputs.add(new ResultItem(group,stack,minInsert));
    }

    @ZenMethod
    public void addFluidOutput(String group, ILiquidStack istack) {
        FluidStack stack = CraftTweakerMC.getLiquidStack(istack);
        outputs.add(new ResultFluid(group,stack));
    }

    @ZenMethod
    public void addFluidOutput(String group, ILiquidStack istack, int minInsert) {
        FluidStack stack = CraftTweakerMC.getLiquidStack(istack);
        outputs.add(new ResultFluid(group,stack,minInsert));
    }

    @ZenMethod
    public void addEnergyOutput(String group, int energy) {
        outputs.add(new ResultEnergy(group,energy));
    }

    @ZenMethod
    public void addEnergyOutput(String group, int energy, int minInsert) {
        outputs.add(new ResultEnergy(group,energy,minInsert));
    }

    @ZenMethod
    public void addLaserOutput(String group, String type, int amount, LaserVisualCT visual, @Optional SlotVisualCT slotVisual) {
        outputs.add(new ResultLaser(group,type,amount,visual.get(),SlotVisualCT.unpack(slotVisual)));
    }

    @ZenMethod
    public void stepAutomata(String group, IAutomataStep step) {
        outputs.add(new ResultAutomata(group,step));
    }

    @ZenMethod
    public IItemStack getItem(String mark) {
        ItemStack stack = (ItemStack) inputs.getOrDefault(mark,ItemStack.EMPTY);
        return CraftTweakerMC.getIItemStack(stack);
    }

    @ZenMethod
    public ILiquidStack getFluid(String mark) {
        FluidStack stack = (FluidStack) inputs.getOrDefault(mark,null);
        return CraftTweakerMC.getILiquidStack(stack);
    }

    @ZenMethod
    public int getEnergy(String mark) {
        return (int) inputs.getOrDefault(mark, 0);
    }
}
