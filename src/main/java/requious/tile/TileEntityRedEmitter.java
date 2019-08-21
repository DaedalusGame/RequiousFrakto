package requious.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import requious.block.BlockRedEmitter;
import requious.data.RedEmitterData;
import requious.entity.EntitySpark;
import requious.entity.ISparkValue;
import requious.entity.spark.TargetTile;
import requious.entity.spark.ValueForgeEnergy;

import javax.annotation.Nullable;

public class TileEntityRedEmitter extends TileEntityEmitter {
    int energy;

    public RedEmitterData getData() {
        BlockRedEmitter emitter = (BlockRedEmitter) this.getBlockType();
        return (RedEmitterData) emitter.getData();
    }

    public int getCapacity() {
        return getData().capacity;
    }

    IEnergyStorage capability = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = Math.min(getCapacity() - energy, maxReceive);
            if(!simulate) {
                energy += received;
                markDirty();
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = Math.min(energy,maxExtract);
            if(!simulate) {
                energy -= extracted;
                markDirty();
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return getCapacity();
        }

        @Override
        public boolean canExtract() {
            return isReceiver();
        }

        @Override
        public boolean canReceive() {
            return isEmitter();
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
        if(capability == CapabilityEnergy.ENERGY)
            return getFacing().getOpposite() == facing;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityEnergy.ENERGY && getFacing().getOpposite() == facing)
            return (T) capability;
        return super.getCapability(capability, facing);
    }

    @Override
    void ioEnergy(EnumFacing dir, TileEntity attachedTile) {
        if(attachedTile.hasCapability(CapabilityEnergy.ENERGY,dir)) {
            IEnergyStorage storage = attachedTile.getCapability(CapabilityEnergy.ENERGY,dir);
            if(isEmitter() && storage.canExtract()) {
                int extracted = storage.extractEnergy(getCapacity() - energy, true);
                extracted = capability.receiveEnergy(extracted, false);
                storage.extractEnergy(extracted, false);
                markDirty();
            }
            if(isReceiver() && storage.canReceive()) {
                int inserted = storage.receiveEnergy(energy, true);
                inserted = capability.extractEnergy(inserted, false);
                storage.receiveEnergy(inserted, false);
                markDirty();
            }
        }
    }

    @Override
    void sendPacket() {
        if(energy >= getCapacity()) {
            TileEntity targetTile = getTargetTile();
            ISparkValue value = new ValueForgeEnergy(energy);
            if (targetTile instanceof ISparkAcceptor && ((ISparkAcceptor) targetTile).canAccept(value)) {
                EntitySpark spark = new EntitySpark(world);
                Vec3d velocity = getBurstVelocity(getFacing()).addVector((random.nextDouble() - 0.5) * 0.3, (random.nextDouble() - 0.5) * 0.3, (random.nextDouble() - 0.5) * 0.3);
                spark.init(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, velocity.x, velocity.y, velocity.z, new TargetTile(targetTile), value);
                spark.pushHistory(pos);
                world.spawnEntity(spark);
                energy = 0;
                markDirty();
            }
        }
    }

    @Override
    void receivePacket(EntitySpark spark) {
        ISparkValue value = spark.value;
        if(value instanceof ValueForgeEnergy) {
            capability.receiveEnergy(((ValueForgeEnergy) value).getEnergy(),false);
        }
    }

    @Override
    public boolean canAccept(ISparkValue value) {
        if(value instanceof ValueForgeEnergy)
            return ((ValueForgeEnergy) value).getEnergy() <= getCapacity() - energy;
        return false;
    }

    @Override
    int getInterval() {
        return getData().interval;
    }
}
