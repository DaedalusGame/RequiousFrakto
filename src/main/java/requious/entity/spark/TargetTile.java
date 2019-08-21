package requious.entity.spark;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import requious.entity.EntitySpark;
import requious.entity.ISparkTarget;
import requious.tile.ISparkAcceptor;

public class TargetTile implements ISparkTarget {
    public static class Deserializer implements ISparkTarget.Deserializer {
        @Override
        public ISparkTarget deserialize(NBTTagCompound compound) {
            if(compound.hasKey("tileX"))
                return new TargetTile();
            return null;
        }
    }

    BlockPos pos;

    private TargetTile() {

    }

    public TargetTile(BlockPos pos) {
        this.pos = pos;
    }

    public TargetTile(TileEntity tile) {
        this.pos = tile.getPos();
    }

    @Override
    public ISparkAcceptor getAcceptor(EntitySpark spark) {
        TileEntity tile = spark.getEntityWorld().getTileEntity(pos);
        if(tile instanceof ISparkAcceptor)
            return (ISparkAcceptor) tile;
        return null;
    }

    @Override
    public Vec3d getPosition(EntitySpark spark) {
        TileEntity tile = spark.getEntityWorld().getTileEntity(pos);
        if(tile != null) {
            BlockPos pos = tile.getPos();
            return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
        return Vec3d.ZERO;
    }

    @Override
    public boolean isValid(EntitySpark spark) {
        TileEntity tile = spark.getEntityWorld().getTileEntity(pos);
        return tile != null && !tile.isInvalid();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("tileX",pos.getX());
        compound.setInteger("tileY",pos.getY());
        compound.setInteger("tileZ",pos.getZ());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        pos = new BlockPos(compound.getInteger("tileX"),compound.getInteger("tileY"),compound.getInteger("tileZ"));
    }
}
