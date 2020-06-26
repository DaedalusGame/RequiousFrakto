package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import requious.data.AssemblyProcessor;
import requious.tile.TileEntityAssembly;
import requious.util.ComponentFace;
import requious.util.ILaserStorage;
import requious.util.LaserUtil;
import requious.util.LaserVisual;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;

@ZenRegister
@ZenClass("mods.requious.LaserSlot")
public class ComponentLaser extends ComponentBase {
    public boolean inputAllowed = true;
    public boolean outputAllowed = true;
    public int minReceive = 0;
    public int maxReceive = Integer.MAX_VALUE;
    public int minTargets = 1;
    public int maxTargets = 1;
    public HashSet<String> types = new HashSet<>();
    public Vec3i areaStart = Vec3i.NULL_VECTOR;
    public Vec3i areaEnd = Vec3i.NULL_VECTOR;

    public ComponentLaser(ComponentFace face) {
        super(face);
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentLaser setAccess(boolean input, boolean output) {
        inputAllowed = input;
        outputAllowed = output;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentLaser setType(String type) {
        types.add(type);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentLaser setLimit(int min, int max) {
        minReceive = min;
        maxReceive = max;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentLaser setMultiTarget(int minTargets, int maxTargets) {
        this.minTargets = minTargets;
        this.maxTargets = maxTargets;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentLaser setArea(int x1, int y1, int z1, int x2, int y2, int z2) {
        areaStart = new Vec3i(x1, y1, z1);
        areaEnd = new Vec3i(x2, y2, z2);
        return this;
    }

    public static class Slot extends ComponentBase.Slot<ComponentLaser> {
        HashMap<String, Integer> energyCharge = new HashMap<>();
        HashMap<String, Integer> energy = new HashMap<>();
        int currentFacing = 0;
        int emitAmount;
        String emitType;
        LaserVisual emitVisual;
        LaserUtil util = new LaserUtil();

        public Slot(ComponentLaser component) {
            super(component);
        }

        public String getEmitType() {
            return emitType;
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            Collector energy = new Collector(getFace());

            if (!collectors.contains(energy))
                collectors.add(energy);
        }

        @Override
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return null;
        }

        public void updateLaser(World world, BlockPos pos, EnumFacing tileFacing) {
            EnumFacing side = TileEntityAssembly.toSide(tileFacing,getFace().getSide(currentFacing));
            util.setEmitter(world, pos, side, emitType, emitVisual);
            if(canOutput()) {
                util.setTarget(component.areaStart, component.areaEnd);
                util.setMultiTarget(component.minTargets, component.maxTargets);
                if (util.failure()) {
                    util.setDirty();
                    currentFacing++;
                } else if (util.success()) {
                    if (util.foundNew())
                        util.pickTarget();
                    util.fire(emitAmount);
                    markDirty();
                    emitType = null;
                    emitAmount = 0;
                    if(!util.hasMaxTargets())
                        util.startSearch();
                } else {
                    util.next();
                }
            }
        }

        public void updateArea(EnumFacing facing) {
            util.setTarget(toBlockPos(component.areaStart, facing), toBlockPos(component.areaEnd, facing));
        }

        private BlockPos toBlockPos(Vec3i vec, EnumFacing facing) {
            EnumFacing x = TileEntityAssembly.toLocalSide(facing, EnumFacing.EAST);
            EnumFacing y = TileEntityAssembly.toLocalSide(facing, EnumFacing.UP);
            EnumFacing z = TileEntityAssembly.toLocalSide(facing, EnumFacing.SOUTH);

            int vx = vec.getX();
            int vy = vec.getY();
            int vz = vec.getZ();

            return new BlockPos(
                    vx * x.getFrontOffsetX() + vx * y.getFrontOffsetX() + vx * z.getFrontOffsetX(),
                    vy * x.getFrontOffsetY() + vy * y.getFrontOffsetY() + vy * z.getFrontOffsetY(),
                    vz * x.getFrontOffsetZ() + vz * y.getFrontOffsetZ() + vz * z.getFrontOffsetZ()
            );
        }

        @Override
        public void update() {
            util.render();
            energy.clear();
            energy.putAll(energyCharge);
            energyCharge.clear();
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            //NOOP
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("energy", writeEnergy());
            util.writeToNBT(compound);
            return compound;
        }

        private NBTTagList writeEnergy() {
            NBTTagList energyList = new NBTTagList();
            for (Map.Entry<String, Integer> entry : this.energy.entrySet()) {
                NBTTagCompound energy = new NBTTagCompound();
                energy.setString("type", entry.getKey());
                energy.setInteger("amount", entry.getValue());
                energyList.appendTag(energy);
            }
            return energyList;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {
            NBTTagList energyList = compound.getTagList("energy", 10);
            readEnergy(energyList);
            util.readFromNBT(compound);
        }

        private void readEnergy(NBTTagList energyList) {
            this.energy.clear();
            this.energyCharge.clear();
            for (NBTBase energy : energyList) {
                String type = ((NBTTagCompound) energy).getString("type");
                int amount = ((NBTTagCompound) energy).getInteger("amount");
                int charge = ((NBTTagCompound) energy).getInteger("charge");
                this.energy.put(type, amount);
                this.energyCharge.put(type, charge);
            }
        }

        public boolean canInput() {
            return component.inputAllowed;
        }

        public boolean canOutput() {
            return component.outputAllowed;
        }

        public boolean canAccept(String type) {
            return component.types.isEmpty() || component.types.contains(type);
        }

        public int getEnergy(String type) {
            return energy.getOrDefault(type, 0);
        }

        public int getTotalEnergy() {
            return energy.values().stream().mapToInt(x -> x).sum();
        }

        public int receive(String type, int i, boolean simulate) {
            if(type == null || !canInput())
                return 0;
            int received = i >= component.minReceive ? Math.min(i, component.maxReceive) : 0;
            if (!simulate) {
                energyCharge.compute(type, (k, v) -> v != null ? v + received : received);
                markDirty();
            }
            return received;
        }

        public void emit(String type, int energy, LaserVisual visual) {
            emitType = type;
            emitAmount = energy;
            emitVisual = visual;
        }
    }

    public static class Collector extends ComponentBase.Collector implements ILaserStorage {
        ComponentFace face;
        List<Slot> slots = new ArrayList<>();

        public Collector(ComponentFace face) {
            this.face = face;
        }

        public ComponentFace getFace() {
            return face;
        }

        private void addSlot(Slot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if (slot.getFace() == face && slot instanceof Slot) {
                addSlot((Slot) slot);
                return true;
            }
            return false;
        }

        @Override
        public boolean hasCapability() {
            return false;
        }

        @Override
        public void update() {
            for (Slot slot : slots) {
                slot.updateLaser(tile.getWorld(), tile.getPos(), getTileFacing());
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Collector)
                return face.equals(((Collector) obj).face);
            return false;
        }

        @Override
        public int receive(String type, int power, boolean simulate) {
            int received = 0;
            for (Slot slot : slots) {
                if (!slot.canAccept(type) && slot.canInput())
                    continue;
                received += slot.receive(type, power, simulate);
            }
            return received;
        }
    }
}
