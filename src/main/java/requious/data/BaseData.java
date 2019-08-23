package requious.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public abstract class BaseData {
    @SerializedName("resourceName")
    public String resourceName;
    @SerializedName("model")
    public ResourceLocation model;
    @SerializedName("colors")
    public Color[] colors = new Color[]{ Color.WHITE};
    @SerializedName("hardness")
    public float hardness = 5.0f;
    @SerializedName("blastResistance")
    public float blastResistance = 5.0f;
    @SerializedName("aabb")
    public AxisAlignedBB aabb = Block.FULL_BLOCK_AABB;
}
