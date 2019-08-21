package requious.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import requious.Requious;
import requious.block.BlockEmitter;
import requious.entity.EntitySpark;
import requious.entity.spark.TargetTile;
import requious.particle.IParticleAnchor;
import requious.util.Misc;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

public abstract class TileEntityEmitter extends TileEntity implements ITickable, ISparkAcceptor, ITargetable {
    Random random = new Random();

    public long ticksExisted = 0;
    int offset = random.nextInt(60);
    public BlockPos target = null;
    PathVisual visual = null;

    static class PathVisual {
        double posX, posY, posZ;
        double motionX, motionY, motionZ;
        boolean arrived;

        public PathVisual(double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
        }

        public void update(double targetX, double targetY, double targetZ)
        {
            Vec3d targetVector = new Vec3d(targetX-posX,targetY-posY,targetZ-posZ);
            double length = targetVector.lengthVector();
            targetVector = targetVector.scale(0.3/length);
            double weight  = 0;
            if (length <= 3){
                weight = 0.9*((3.0-length)/3.0);
            }
            motionX = (0.9-weight)*motionX+(0.1+weight)*targetVector.x;
            motionY = (0.9-weight)*motionY+(0.1+weight)*targetVector.y;
            motionZ = (0.9-weight)*motionZ+(0.1+weight)*targetVector.z;
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            double distanceSq = this.getDistanceSq(targetX, targetY, targetZ);
            if (distanceSq < 0.1){
                arrived = true;
            }
        }

        private double getDistanceSq(double targetX, double targetY, double targetZ) {
            double dx = targetX - posX;
            double dy = targetY - posY;
            double dz = targetZ - posZ;
            return dx*dx+dy*dy+dz*dz;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        if (target != null){
            tag.setInteger("targetX", target.getX());
            tag.setInteger("targetY", target.getY());
            tag.setInteger("targetZ", target.getZ());
        }
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        if (tag.hasKey("targetX")){
            target = new BlockPos(tag.getInteger("targetX"), tag.getInteger("targetY"), tag.getInteger("targetZ"));
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this, false);
    }

    @Override
    public void update() {
        this.ticksExisted ++;
        EnumFacing facing = getFacing();
        TileEntity attachedTile = getWorld().getTileEntity(getPos().offset(facing.getOpposite()));
        if (attachedTile != null && !getWorld().isRemote){
            ioEnergy(facing,attachedTile);
        }
        if ((this.ticksExisted+offset) % getInterval() == 0 && target != null && !getWorld().isRemote){
            sendPacket();
        }
        if(world.isRemote && target != null && Requious.PROXY.shouldRenderArcs()) {
            double targetX = target.getX()+0.5;
            double targetY = target.getY()+0.5;
            double targetZ = target.getZ()+0.5;
            if(visual != null) {
                int segments = 4;
                for(int i = 0; i < segments; i++) {
                    float partial = 1 - (float)i / segments;
                    visual.update(targetX, targetY, targetZ);
                    Requious.PROXY.emitGlow(world, IParticleAnchor.zero(), visual.posX, visual.posY, visual.posZ, 0, 0, 0, new Color(255, 255, 255), 0.5f, 2.0f, 20, partial);
                }
                if(visual.arrived)
                    visual = null;
            } else {
                Vec3d velocity = getBurstVelocity(facing);
                visual = new PathVisual(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,velocity.x,velocity.y,velocity.z);
            }
        } else {
            visual = null;
        }
    }

    public TileEntity getTargetTile() {
        if(target == null)
            return null;
        return world.getTileEntity(target);
    }

    public boolean isReceiver() {
        return target == null;
    }

    public boolean isEmitter() {
        return target != null;
    }

    abstract void ioEnergy(EnumFacing dir, TileEntity attachedTile);

    abstract void sendPacket();

    abstract void receivePacket(EntitySpark spark);

    abstract int getInterval();

    protected EnumFacing getFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        return state.getValue(BlockEmitter.facing);
    }

    protected Vec3d getBurstVelocity(EnumFacing facing) {
        switch(facing)
        {
            case DOWN:
                return new Vec3d(0, -0.5, 0);
            case UP:
                return new Vec3d(0, 0.5, 0);
            case NORTH:
                return new Vec3d(0, -0.01, -0.5);
            case SOUTH:
                return new Vec3d(0, -0.01, 0.5);
            case WEST:
                return new Vec3d(-0.5, -0.01, 0);
            case EAST:
                return new Vec3d(0.5, -0.01, 0);
            default:
                return Vec3d.ZERO;
        }
    }

    @Override
    public void receive(EntitySpark spark) {
        if(target != null) {
            spark.target = new TargetTile(target);
            spark.lifetime = spark.value.getLifetime(spark);
            spark.pushHistory(pos);
        } else {
            receivePacket(spark);
            spark.setReceived(true);
        }
    }
}
