package requious.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import requious.tile.ILaserAcceptor;

import java.util.*;

public class LaserUtil {
    static class Target {
        ILaserAcceptor target;
        int sent;

        public Target(ILaserAcceptor target) {
            this.target = target;
        }

        public Target(ILaserAcceptor target, int sent) {
            this.target = target;
            this.sent = sent;
        }

        public BlockPos getPosition() {
            return target.getPosition();
        }
    }

    static class LazyTarget {
        BlockPos targetPos;
        int sent;

        public LazyTarget(BlockPos targetPos, int sent) {
            this.targetPos = targetPos;
            this.sent = sent;
        }
    }

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
    private int minTargets = 1, maxTargets = 1;
    private List<ILaserAcceptor> acceptors = new ArrayList<>();
    private LaserVisual visual;
    private List<Target> targets = new ArrayList<>();

    State state = State.SEARCHING;
    List<ILaserAcceptor> searchAcceptors = new ArrayList<>();
    List<LazyTarget> lazyTargets = new ArrayList<>(); //For deserialization only

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

    public void setMultiTarget(int minTargets, int maxTargets) {
        this.minTargets = minTargets;
        this.maxTargets = maxTargets;
    }

    public void setDirty() {
        targets.clear();
        acceptors.clear();
        startSearch();
    }

    public void startSearch() {
        state = State.SEARCHING;
        x = x1;
        y = y1;
        z = z1;
        searchAcceptors.clear();
    }

    public boolean hasTargets() {
        return targets.size() >= minTargets && targets.size() <= maxTargets;
    }

    public boolean hasMaxTargets() {
        return targets.size() == maxTargets;
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
                    searchAcceptors.add((ILaserAcceptor) target);
                }
            }

            boolean looped = moveCursor();
            if (looped) {
                if (searchAcceptors.isEmpty())
                    state = State.NOT_FOUND;
                else
                    state = State.FOUND;
            }
        }
    }

    public boolean foundNew() {
        HashSet<ILaserAcceptor> test = new HashSet<>(acceptors);
        for (ILaserAcceptor acceptor : searchAcceptors) {
            if(!test.contains(acceptor))
                return true;
        }
        return false;
    }

    public void pickTarget() {
        acceptors.clear();
        acceptors.addAll(searchAcceptors);
        searchAcceptors.clear();
        targets.clear();
        int required = maxTargets;
        List<ILaserAcceptor> acceptorSet = new ArrayList<>(acceptors);
        while(targets.size() < required && !acceptorSet.isEmpty()) {
            ILaserAcceptor acceptor = acceptorSet.get(random.nextInt(acceptorSet.size()));
            //ILaserAcceptor acceptor = acceptorSet.stream().min(Comparator.comparingDouble(this::getDistance)).get();
            targets.add(new Target(acceptor));
            acceptorSet.remove(acceptor);
        }
    }

    private double getDistance(ILaserAcceptor acceptor) {
        BlockPos pos = acceptor.getPosition();
        return pos.distanceSq(emitPos);
    }

    public void fire(int power) {
        readTarget();
        boolean dirty = false;
        if(hasTargets()) {
            for (Target currentTarget : targets) {
                if (currentTarget.target.isValid()) {
                    ILaserStorage storage = currentTarget.target.getLaserStorage(emitFacing);
                    if (storage != null && power > 0) {
                        int sent = storage.receive(type, power, false);
                        currentTarget.sent = sent;
                    }
                } else {
                    dirty = true;
                }
            }
        }
        if(dirty) {
            setDirty();
        }
    }

    private void readTarget() { //Deserialize only
        if(world != null && !lazyTargets.isEmpty()) {
            targets.clear();
            Iterator<LazyTarget> iterator = lazyTargets.iterator();
            while(iterator.hasNext()) {
                LazyTarget target = iterator.next();
                TileEntity tile = world.getTileEntity(target.targetPos);
                if(tile instanceof ILaserAcceptor)
                    targets.add(new Target((ILaserAcceptor) tile,target.sent));
                iterator.remove();
            }
        }
    }

    public void render() {
        readTarget();
        if (visual != null && world != null && world.isRemote) {
            for (Target target : targets) {
                if(target.sent <= 0)
                    System.out.println("Test2");
                if (target.target.isValid() && target.sent > 0) {
                    visual.render(world, emitPos, target.getPosition(), target.sent);
                }
            }
        }
    }

    public boolean success() {
        return state == State.FOUND;
    }

    public boolean failure() {
        return state == State.NOT_FOUND;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList targetList = new NBTTagList();
        for (Target target : targets) {
            BlockPos pos = target.getPosition();
            NBTTagCompound targetCompound = new NBTTagCompound();
            targetCompound.setInteger("targetX", pos.getX());
            targetCompound.setInteger("targetY", pos.getY());
            targetCompound.setInteger("targetZ", pos.getZ());
            targetCompound.setInteger("sent", target.sent);
            targetList.appendTag(targetCompound);
        }

        compound.setTag("targets", targetList);
        compound.setInteger("x1",x1);
        compound.setInteger("y1",y1);
        compound.setInteger("z1",z1);
        compound.setInteger("x2",x2);
        compound.setInteger("y2",y2);
        compound.setInteger("z2",z2);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        state = State.NOT_FOUND;
        if(compound.hasKey("targets")) {
            lazyTargets.clear();
            NBTTagList targetList = compound.getTagList("targets", 10);
            for(int i = 0; i < targetList.tagCount(); i++){
                NBTTagCompound targetCompound = targetList.getCompoundTagAt(i);
                BlockPos targetPos = new BlockPos(targetCompound.getInteger("targetX"), targetCompound.getInteger("targetY"), targetCompound.getInteger("targetZ"));

                lazyTargets.add(new LazyTarget(targetPos,targetCompound.getInteger("sent")));
                state = State.FOUND;
            }
        }
        x1 = compound.getInteger("x1");
        y1 = compound.getInteger("y1");
        z1 = compound.getInteger("z1");
        x2 = compound.getInteger("x2");
        y2 = compound.getInteger("y2");
        z2 = compound.getInteger("z2");

    }
}
