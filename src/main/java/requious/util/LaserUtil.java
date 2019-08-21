package requious.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import requious.Requious;
import requious.particle.IParticleAnchor;
import requious.tile.ILaserAcceptor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LaserUtil {
    public enum State {
        SEARCHING,
        FOUND,
        NOT_FOUND,
    }

    static Random random = new Random();

    private World world;
    private BlockPos emitPos;
    private EnumFacing emitFacing;
    private String type;
    private int x1, y1, z1;
    private int x2, y2, z2;
    private int x, y, z;
    private List<ILaserAcceptor> acceptors = new ArrayList<>();
    private ILaserAcceptor currentTarget;
    private LaserVisual visual;
    private int sent;

    State state = State.SEARCHING;
    BlockPos targetPos; //For deserialization only

    public World getWorld() {
        return world;
    }

    public void setEmitter(World world, BlockPos pos, EnumFacing facing, String type, LaserVisual visual) {
        this.world = world;
        this.emitPos = pos;
        this.emitFacing = facing;
        this.type = type;
        this.visual = visual;
        readTarget();
    }

    public void setTarget(Vec3i zoneA, Vec3i zoneB) {
        int xMin = Math.min(zoneA.getX(),zoneB.getX());
        int yMin = Math.min(zoneA.getY(),zoneB.getY());
        int zMin = Math.min(zoneA.getZ(),zoneB.getZ());
        int xMax = Math.max(zoneA.getX(),zoneB.getX());
        int yMax = Math.max(zoneA.getY(),zoneB.getY());
        int zMax = Math.max(zoneA.getZ(),zoneB.getZ());
        if (xMin != x1 || xMax != x2 || yMin != y1 || yMax != y2 || zMin != z1 || zMax != z2) {
            x1 = xMin;
            y1 = yMin;
            z1 = zMin;
            x2 = xMax;
            y2 = yMax;
            z2 = zMax;
            setDirty();
        }
    }

    public void setDirty() {
        state = State.SEARCHING;
        sent = 0;
        currentTarget = null;
        x = x1;
        y = y1;
        z = z1;
        acceptors.clear();
    }

    public boolean hasTarget() {
        return currentTarget != null;
    }

    private boolean moveCursor() {
        x++;
        if(x > x2) {
            x = x1;
            y++;
        }
        if(y > y2) {
            y = y1;
            z++;
        }
        if(z > z2) {
            z = z1;
            return true;
        }
        return false;
    }

    public void next() {
        for(int i = 0; i < 88 && state == State.SEARCHING; i++) {
            BlockPos targetPos = Misc.posOffset(emitPos, new Vec3i(x, y, z), emitFacing);
            TileEntity target = world.getTileEntity(targetPos);
            if (!targetPos.equals(emitPos) && target instanceof ILaserAcceptor) {
                ILaserStorage laserStorage = ((ILaserAcceptor) target).getLaserStorage(emitFacing);
                if (laserStorage != null && laserStorage.receive(type, Integer.MAX_VALUE, true) > 0) {
                    acceptors.add((ILaserAcceptor) target);
                }
            }

            boolean looped = moveCursor();
            if (looped) {
                if (acceptors.isEmpty())
                    state = State.NOT_FOUND;
                else
                    state = State.FOUND;
            }
        }
    }

    public void pickTarget() {
        ILaserAcceptor acceptor = acceptors.get(random.nextInt(acceptors.size()));
        currentTarget = acceptor;
    }

    private ILaserAcceptor getTarget() {
        return currentTarget;
    }

    private BlockPos getTargetPosition() {
        if(currentTarget != null && currentTarget.isValid()) {
            return currentTarget.getPosition();
        }
        return null;
    }

    private void setTargetPosition(BlockPos pos) {
        targetPos = pos;
    }

    public void fire(int power) {
        readTarget();
        if(currentTarget != null && currentTarget.isValid()) {
            ILaserStorage storage = currentTarget.getLaserStorage(emitFacing);
            if (storage != null && power > 0) {
                sent = storage.receive(type, power, false);
            }
        } else {
            setDirty();
        }
    }

    private void readTarget() { //Deserialize only
        if(targetPos != null && world != null) {
            TileEntity tile = world.getTileEntity(targetPos);
            if(tile instanceof ILaserAcceptor)
                currentTarget = (ILaserAcceptor) tile;
            targetPos = null;
        }
    }

    public void render() {
        readTarget();
        if (visual != null && world != null && world.isRemote && currentTarget != null && currentTarget.isValid() && sent > 0) {
            BlockPos target = getTargetPosition();
            visual.render(world,emitPos,target,sent);
        }
    }

    public boolean success() {
        return state == State.FOUND;
    }

    public boolean failure() {
        return state == State.NOT_FOUND;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        BlockPos pos = getTargetPosition();
        if (pos != null) {
            compound.setInteger("targetX", pos.getX());
            compound.setInteger("targetY", pos.getY());
            compound.setInteger("targetZ", pos.getZ());
        }
        compound.setInteger("sent", sent);
        compound.setInteger("x1",x1);
        compound.setInteger("y1",y1);
        compound.setInteger("z1",z1);
        compound.setInteger("x2",x2);
        compound.setInteger("y2",y2);
        compound.setInteger("z2",z2);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("targetX")) {
            targetPos = new BlockPos(compound.getInteger("targetX"), compound.getInteger("targetY"), compound.getInteger("targetZ"));
            state = State.FOUND;
        } else {
            state = State.NOT_FOUND;
        }
        sent = compound.getInteger("sent");
        x1 = compound.getInteger("x1");
        y1 = compound.getInteger("y1");
        z1 = compound.getInteger("z1");
        x2 = compound.getInteger("x2");
        y2 = compound.getInteger("y2");
        z2 = compound.getInteger("z2");

    }
}
