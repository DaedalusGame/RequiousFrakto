package requious.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import crafttweaker.annotations.ZenRegister;
import requious.compat.crafttweaker.ColorCT;
import requious.compat.crafttweaker.IFluidCondition;
import requious.item.ItemFluidCell;
import requious.util.color.FluidColor;
import requious.util.color.ICustomColor;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.requious.FluidCell")
public class FluidCellData extends ItemData {
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

    public boolean hasBar() {
        return barColor != null;
    }
}
