package requious.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public abstract class BaseData {
    @SerializedName("resourceName")
    public String resourceName;
    @SerializedName("model")
    public ResourceLocation model;
    @SerializedName("colorA")
    public Color colorA = Color.WHITE;
    @SerializedName("colorB")
    public Color colorB = Color.WHITE;
}
