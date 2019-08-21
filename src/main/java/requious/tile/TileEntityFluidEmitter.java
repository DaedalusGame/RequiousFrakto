package requious.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import requious.block.BlockFluidEmitter;
import requious.data.FluidEmitterData;
import requious.entity.EntitySpark;
import requious.entity.ISparkValue;
import requious.entity.spark.TargetTile;
import requious.entity.spark.ValueFluid;

import javax.annotation.Nullable;

public class TileEntityFluidEmitter extends TileEntityEmitter {
    FluidStack fluid;
    EntitySpark currentSpark;

    public FluidEmitterData getData() {
        BlockFluidEmitter emitter = (BlockFluidEmitter) this.getBlockType();
        return (FluidEmitterData) emitter.getData();
    }

    public int getCapacity() {
        return getData().capacity;
    }

    public int getAmount() {
        if(fluid == null)
            return 0;
        return fluid.amount;
    }

    FluidTank capability = new FluidTank(0) {
        @Override
        public void setCapacity(int capacity) {
            //NOOP
        }

        @Override
        public int getCapacity() {
            return TileEntityFluidEmitter.this.getCapacity();
        }

        @Nullable
        @Override
        public FluidStack drainInternal(int maxDrain, boolean doDrain) {
            capacity = TileEntityFluidEmitter.this.getCapacity();
            return super.drainInternal(maxDrain, doDrain);
        }

        @Override
        public int fillInternal(FluidStack resource, boolean doFill) {
            capacity = TileEntityFluidEmitter.this.getCapacity();
            return super.fillInternal(resource, doFill);
        }
    };

    @Override
    public void setTarget(World world, BlockPos pos, EnumFacing facing) {
        if(world != this.world)
            return;
        if(pos.equals(this.pos))
            return;
        target = pos;
        markDirty();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return getFacing().getOpposite() == facing;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && getFacing().getOpposite() == facing)
            return (T) capability;
        return super.getCapability(capability, facing);
    }

    @Override
    void ioEnergy(EnumFacing dir, TileEntity attachedTile) {
        if(attachedTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,dir)) {
            IFluidHandler storage = attachedTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,dir);
            if(isEmitter()) {
                FluidStack extracted = storage.drain(getCapacity() - getAmount(),true);
                if(extracted != null)
                    capability.fillInternal(extracted,true);
                markDirty();
            }
            if(isReceiver()) {
                FluidStack extracted = capability.drain(getAmount(),false);
                if(extracted != null) {
                    int inserted = storage.fill(extracted, true);
                    capability.drain(inserted,true);
                }
                markDirty();
            }
        }
    }

    @Override
    void sendPacket() {
        if(getAmount() >= getCapacity() && isPacketDead()) {
            TileEntity targetTile = getTargetTile();
            ISparkValue value = new ValueFluid(fluid);
            if (targetTile instanceof ISparkAcceptor && ((ISparkAcceptor) targetTile).canAccept(value)) {
                currentSpark = new EntitySpark(world);
                Vec3d velocity = getBurstVelocity(getFacing()).addVector((random.nextDouble() - 0.5) * 0.3, (random.nextDouble() - 0.5) * 0.3, (random.nextDouble() - 0.5) * 0.3);
                currentSpark.init(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, velocity.x, velocity.y, velocity.z, new TargetTile(targetTile), value);
                currentSpark.pushHistory(pos);
                world.spawnEntity(currentSpark);
                fluid = null;
                markDirty();
            }
        }
    }

    private boolean isPacketDead() {
        return currentSpark == null || currentSpark.isDead;
    }

    @Override
    void receivePacket(EntitySpark spark) {
        ISparkValue value = spark.value;
        if(value instanceof ValueFluid) {
            capability.fill(((ValueFluid) value).getFluid(),false);
        }
    }

    @Override
    public boolean canAccept(ISparkValue value) {
        if(value instanceof ValueFluid)
            return ((ValueFluid) value).getAmount() <= getCapacity() - getAmount();
        return false;
    }

    @Override
    int getInterval() {
        return getData().interval;
    }
}
