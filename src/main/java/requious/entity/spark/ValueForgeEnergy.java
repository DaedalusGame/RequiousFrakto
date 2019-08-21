package requious.entity.spark;

import net.minecraft.nbt.NBTTagCompound;
import requious.entity.EntitySpark;
import requious.entity.ISparkValue;
import requious.entity.SparkEffect;

import java.awt.*;

public class ValueForgeEnergy implements ISparkValue {
    public static class Deserializer implements ISparkValue.Deserializer {
        @Override
        public ISparkValue deserialize(NBTTagCompound compound) {
            if(compound.hasKey("fe"))
                return new ValueForgeEnergy();
            return null;
        }
    }

    int energy;

    public int getEnergy() {
        return energy;
    }

    private ValueForgeEnergy() {
    }

    public ValueForgeEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public float getSize(EntitySpark spark) {
        return 1;
    }

    @Override
    public Color getColor(EntitySpark spark) {
        return new Color(255,16,16);
    }

    @Override
    public SparkEffect getEffect(EntitySpark spark) {
        return SparkEffect.LightningBall;
    }

    @Override
    public int getLifetime(EntitySpark spark) {
        return 80;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("fe",energy);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        energy = compound.getInteger("fe");
    }
}
