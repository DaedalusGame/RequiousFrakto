package requious.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.item.MCItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import requious.compat.crafttweaker.ColorCT;
import requious.compat.crafttweaker.IFluidCondition;
import requious.item.ItemFluidCell;
import requious.util.IConversion;
import requious.util.RatioConversion;
import requious.util.color.FluidColor;
import requious.util.color.ICustomColor;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;

@ZenRegister
@ZenClass("mods.requious.FluidCell")
public class FluidCellData extends ItemData {
    static class FuelData {
        FluidStack fluidStack;
        int fuel;

        public FuelData(FluidStack fluidStack, int fuel) {
            this.fluidStack = fluidStack;
            this.fuel = fuel;
        }

        public boolean matches(FluidStack check) {
            return fluidStack.isFluidEqual(check);
        }

        public int getUnit() {
            return fluidStack.amount;
        }

        public int getFuel() {
            return fuel;
        }
    }

    @SerializedName("generateSubItems")
    public boolean generateSubItems;
    @SerializedName("capacity")
    public int capacity;
    @SerializedName("minInput")
    public int minInput = 0;
    @SerializedName("maxInput")
    public int maxInput = Integer.MAX_VALUE;
    @SerializedName("minOutput")
    public int minOutput = 0;
    @SerializedName("maxOutput")
    public int maxOutput = Integer.MAX_VALUE;
    @SerializedName("showTooltip")
    public boolean showToolip = true;

    @Expose(serialize = false, deserialize = false)
    public transient IFluidCondition filter;
    @Expose(serialize = false, deserialize = false)
    public transient ICustomColor barColor;
    @Expose(serialize = false, deserialize = false)
    public transient List<FuelData> fuelValues = new ArrayList<>();

    @Expose(serialize = false, deserialize = false)
    private transient ItemFluidCell item;

    public FluidCellData() {
        setBaseColor(new FluidColor());
    }

    public ItemFluidCell getItem() {
        return item;
    }

    public void setItem(ItemFluidCell item) {
        this.item = item;
    }

    @ZenMethod
    public void addFilter(IFluidCondition filter) {
        this.filter = filter;
    }

    @ZenMethod
    public void showBar(ColorCT color) {
        this.barColor = color.get();
    }

    @ZenMethod
    public void addFuelValue(ILiquidStack liquid, int value) {
        FluidStack fluid = CraftTweakerMC.getLiquidStack(liquid);
        fuelValues.add(new FuelData(fluid,value));
    }

    public boolean hasBar() {
        return barColor != null;
    }

    public int getFuelValue(FluidStack fluid) {
        for (FuelData fuelData : fuelValues) {
            if(fuelData.matches(fluid)) {
                int units = fluid.amount / fuelData.getUnit();
                return units * fuelData.getFuel();
            }
        }
        return -1;
    }


}
