package requious.entity.spark;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import requious.entity.EntitySpark;
import requious.entity.ISparkValue;
import requious.entity.SparkEffect;
import requious.util.FluidColorHelper;

import java.awt.*;

public class ValueFluid implements ISparkValue {
    public static class Deserializer implements ISparkValue.Deserializer {
        @Override
        public ISparkValue deserialize(NBTTagCompound compound) {
            if(compound.hasKey("fluid"))
                return new ValueFluid();
            return null;
        }
    }

    FluidStack fluid;

    private ValueFluid() {
    }

    public ValueFluid(FluidStack fluid) {
        this.fluid = fluid;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public int getAmount() {
        if(fluid == null)
            return 0;
        return fluid.amount;
    }

    @Override
    public float getSize(EntitySpark spark) {
        return 1;
    }

    @Override
    public Color getColor(EntitySpark spark) {
        return new Color(FluidColorHelper.getColor(fluid),true);
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
        compound.setTag("fluid",fluid.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        fluid = FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("fluid"));
    }
}
