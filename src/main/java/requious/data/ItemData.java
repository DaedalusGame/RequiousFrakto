package requious.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import requious.compat.crafttweaker.ColorCT;
import requious.util.color.ICustomColor;
import requious.util.color.NormalColor;
import stanhebben.zenscript.annotations.ZenMethod;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ItemData {
    @SerializedName("resourceName")
    public String resourceName;
    @SerializedName("model")
    public ResourceLocation model;
    @SerializedName("colors")
    public Color[] colorsSerialized = new Color[]{ Color.WHITE};
    @SerializedName("stackSize")
    public int stackSize = 64;

    @Expose(serialize = false, deserialize = false)
    private transient Map<Integer, ICustomColor> colors = new HashMap<>();
    @Expose(serialize = false, deserialize = false)
    private transient ICustomColor baseColor = new NormalColor(Color.WHITE);

    public void init() {
        for (int i = 0; i < colorsSerialized.length; i++)
            colors.put(i,new NormalColor(colorsSerialized[i]));
    }

    protected void setBaseColor(ICustomColor color) {
        baseColor = color;
    }

    @ZenMethod
    public void setColor(int index, ColorCT color) {
        colors.put(index,color.get());
    }

    public Color getColor(ItemStack stack, int index) {
        ICustomColor color = colors.get(index);
        if(color == null)
            return baseColor.get(stack);
        else
            return color.get(stack);
    }
}
