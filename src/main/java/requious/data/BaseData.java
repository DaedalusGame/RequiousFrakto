package requious.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import requious.compat.crafttweaker.ColorCT;
import requious.item.ItemFluidCell;
import requious.util.color.ICustomColor;
import requious.util.color.NormalColor;
import stanhebben.zenscript.annotations.ZenMethod;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseData {
    @SerializedName("resourceName")
    public String resourceName;
    @SerializedName("model")
    public ResourceLocation model;
    @SerializedName("colors")
    public Color[] colorsSerialized = new Color[]{ Color.WHITE};
    @SerializedName("hardness")
    public float hardness = 5.0f;
    @SerializedName("blastResistance")
    public float blastResistance = 5.0f;
    @SerializedName("aabb")
    public AxisAlignedBB aabb = Block.FULL_BLOCK_AABB;

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

    public Color getColor(int index) {
        ICustomColor color = colors.get(index);
        if(color == null)
            return baseColor.get();
        else
            return color.get();
    }
}
