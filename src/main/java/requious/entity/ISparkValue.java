package requious.entity;

import net.minecraft.nbt.NBTTagCompound;

import java.awt.*;

public interface ISparkValue {
    interface Deserializer {
        ISparkValue deserialize(NBTTagCompound compound);
    }

    float getSize(EntitySpark spark);

    Color getColor(EntitySpark spark);

    SparkEffect getEffect(EntitySpark spark);

    int getLifetime(EntitySpark spark);

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);
}
