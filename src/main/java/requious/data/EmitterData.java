package requious.data;

import com.google.gson.annotations.SerializedName;

public class EmitterData extends BaseData {
    @SerializedName("interval")
    public int interval;

    public boolean canEmit;
    public boolean canReceive;
    public boolean canRelay;
}
