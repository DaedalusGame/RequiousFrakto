package requious.data;

import com.google.gson.annotations.SerializedName;

public class FluidEmitterData extends EmitterData {
    @SerializedName("capacity")
    public int capacity;
    @SerializedName("allowLiquid")
    public boolean allowLiquid;
    @SerializedName("allowGas")
    public boolean allowGas;
    @SerializedName("minTemperature")
    public int minTemperature;
    @SerializedName("maxTemperature")
    public int maxTemperature;
}
