package requious.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import requious.tile.ISparkAcceptor;

public interface ISparkTarget {
    interface Deserializer {
        ISparkTarget deserialize(NBTTagCompound compound);
    }

    ISparkAcceptor getAcceptor(EntitySpark spark);

    Vec3d getPosition(EntitySpark spark);

    boolean isValid(EntitySpark spark);

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);
}
