package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import ic2.api.energy.EnergyNet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import requious.Requious;
import requious.compat.crafttweaker.GaugeDirectionCT;
import requious.compat.crafttweaker.SlotVisualCT;
import requious.gui.GaugeDirection;
import requious.gui.slot.EnergySlot;
import requious.tile.TileEntityAssembly;
import requious.util.*;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.EnergySlot")
public class ComponentEnergy extends ComponentBase {
    public boolean batteryAllowed;
    public boolean inputAllowed = true;
    public boolean outputAllowed = true;
    public boolean putAllowed = true;
    public boolean takeAllowed = true;
    public boolean dropsOnBreak = true;
    public boolean canOverfill = false;
    public IOParameters pushItem = new IOParameters();
    public IOParameters pushEnergy = new IOParameters();
    public int capacity;
    public float powerLoss;

    public int maxInput = Integer.MAX_VALUE;
    public int maxOutput = 0;

    public boolean acceptsFE = true;
    public boolean acceptsEU = false;

    public RatioConversion conversionEU = new RatioConversion(4, 1);

    public ResourceLocation tex = new ResourceLocation(Requious.MODID, "textures/gui/assembly_gauges.png");
    public int texX, texY;
    public GaugeDirection texDirection = GaugeDirection.UP;
    public String unit = "fe";

    public SlotVisual foreground = SlotVisual.EMPTY;

    public ComponentEnergy(ComponentFace face, int capacity) {
        super(face);
        this.capacity = capacity;
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setAccess(boolean input, boolean output) {
        inputAllowed = input;
        outputAllowed = output;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy allowBattery(boolean input, boolean output, boolean drops) {
        batteryAllowed = true;
        putAllowed = input;
        takeAllowed = output;
        dropsOnBreak = drops;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setPowerLoss(float loss) {
        powerLoss = loss;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setTexture(int x, int y, GaugeDirectionCT direction) {
        texX = x;
        texY = y;
        texDirection = direction.get();
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setTexture(String resource, int x, int y, GaugeDirectionCT direction) {
        tex = new ResourceLocation(resource);
        setTexture(x, y, direction);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy allowOverfill() {
        this.canOverfill = true;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy noDrop() {
        this.dropsOnBreak = false;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setLimits(int input, int output) {
        this.maxInput = input;
        this.maxOutput = output;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy pushItem(int size, int slot) {
        this.pushItem = new IOParameters(size, slot);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy pushItem(int size) {
        this.pushItem = new IOParameters(size);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy pushEnergy(int size) {
        this.pushEnergy = new IOParameters(size);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setForeground(SlotVisualCT visual) {
        this.foreground = SlotVisualCT.unpack(visual);
        return this;
    }

    public static class Slot extends ComponentBase.Slot<ComponentEnergy> implements ComponentItem.IItemSlot {
        int energy;
        float powerLoss;
        ItemComponentHelper battery;
        boolean active;

        static IEnergyStorage fakeBattery = new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return 0;
            }

            @Override
            public int getMaxEnergyStored() {
                return 0;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        };

        public Slot(ComponentEnergy component) {
            super(component);
            battery = new ItemComponentHelper() {
                @Override
                public int getCapacity() {
                    return 1;
                }
            };
        }

        public boolean acceptsFE() {
            return component.acceptsFE;
        }

        public boolean acceptsEU() {
            return component.acceptsEU;
        }

        public RatioConversion getEUConversion() {
            return component.conversionEU;
        }

        public int getMaxInput() {
            return component.maxInput;
        }

        public int getMaxOutput() {
            return component.maxOutput;
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            if (component.batteryAllowed) {
                ComponentItem.Collector item = new ComponentItem.Collector(getFace());
                if (!collectors.contains(item))
                    collectors.add(item);
            }

            if (acceptsFE()) {
                Collector energy = new Collector(getFace());
                if (!collectors.contains(energy))
                    collectors.add(energy);
            }

            if (acceptsEU()) {
                CollectorIC2 energy = new CollectorIC2();
                if (!collectors.contains(energy))
                    collectors.add(energy);
            }
        }

        @Override
        public net.minecraft.inventory.Slot createGui(int x, int y) {
            return new EnergySlot(this, x, y);
        }

        @Override
        public void update() {
            if (!active && energy > 0) {
                powerLoss += component.powerLoss;
                int intLoss = (int) powerLoss;
                if (intLoss > 0) {
                    energy = Math.max(0, energy - intLoss);
                    powerLoss -= intLoss;
                }
                markDirty();
            }
            active = false;
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            if (component.dropsOnBreak) {
                battery.spawnInWorld(world, position);
                battery.setStack(ItemStack.EMPTY);
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("energy", energy);
            compound.setFloat("loss", powerLoss);
            compound.setTag("battery", battery.writeToNBT(new NBTTagCompound()));
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {
            energy = compound.getInteger("energy");
            powerLoss = compound.getFloat("loss");
            battery.readFromNBT(compound.getCompoundTag("battery"));
        }

        public boolean canInput() {
            return component.inputAllowed;
        }

        public boolean canOutput() {
            return component.outputAllowed;
        }

        public boolean canPut() {
            return component.putAllowed;
        }

        public boolean canTake() {
            return component.takeAllowed;
        }

        public boolean canOverfill() {
            return component.canOverfill;
        }

        public int getCapacity() {
            if (canOverfill() && getAmount() <= 0)
                return Integer.MAX_VALUE;
            IEnergyStorage battery = getBatteryStorage();
            return component.capacity + battery.getMaxEnergyStored();
        }

        public int getAmount() {
            IEnergyStorage battery = getBatteryStorage();
            return energy + battery.getEnergyStored();
        }

        private IEnergyStorage getBatteryStorage() {
            if (component.batteryAllowed) {
                ItemStack battery = getItem().getStack();
                if (battery.hasCapability(CapabilityEnergy.ENERGY, null)) {
                    return battery.getCapability(CapabilityEnergy.ENERGY, null);
                }
            }
            return fakeBattery;
        }

        public ResourceLocation getTexture() {
            return component.tex;
        }

        public int getTextureX() {
            return component.texX;
        }

        public int getTextureY() {
            return component.texY;
        }

        public GaugeDirection getTextureDirection() {
            return component.texDirection;
        }

        public String getUnit() {
            return component.unit;
        }

        @Override
        public ItemComponentHelper getItem() {
            return battery;
        }

        public int receive(int amount, boolean simulate) {
            IEnergyStorage battery = getBatteryStorage();
            int internalReceived = Math.min(amount, component.capacity - energy);
            int batteryReceived = battery.receiveEnergy(Math.max(amount - internalReceived, 0), simulate);
            if (!simulate) {
                energy += internalReceived;
                active = true;
                markDirty();
            }
            return internalReceived + batteryReceived;
        }

        public int extract(int amount, boolean simulate) {
            IEnergyStorage battery = getBatteryStorage();
            int internalExtracted = Math.min(amount, energy);
            int batteryExtracted = battery.extractEnergy(Math.max(amount - internalExtracted, 0), simulate);
            if (!simulate) {
                energy -= internalExtracted;
                active = false;
                markDirty();
            }
            return internalExtracted + batteryExtracted;
        }

        @Override
        public boolean isDirty() {
            return super.isDirty() || battery.isDirty();
        }

        @Override
        public void markClean() {
            super.markClean();
            battery.markClean();
        }

        public IOParameters getPushEnergy() {
            return component.pushEnergy;
        }

        @Override
        public IOParameters getPushItem() {
            return component.pushItem;
        }

        @Override
        public boolean canSplit() {
            return true;
        }

        public SlotVisual getForeground() {
            return component.foreground;
        }
    }

    //This has a pretty good chance that it will work poorly.
    public static class CollectorIC2 extends ComponentBase.Collector {
        List<Slot> slots = new ArrayList<>();
        double extraDraw = 0; //We need to stash this

        public CollectorIC2() {
        }

        private void addSlot(Slot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if (slot instanceof Slot && ((Slot) slot).acceptsEU()) {
                addSlot((Slot) slot);
                return true;
            }
            return false;
        }

        @Override
        public void update() {

        }

        public void draw(double amount) {
            amount += extraDraw;
            for (Slot slot : slots) {
                if (slot.canOutput()) {
                    int extracted = slot.extract(slot.getEUConversion().getBase((int) Math.ceil(amount)), false);
                    amount -= slot.getEUConversion().getUnit(extracted);
                }
            }
            extraDraw = amount;
        }

        public double inject(EnumFacing side, double amount, double voltage) {
            for (Slot slot : slots) {
                if (slot.canInput() && slot.getFace().matches(side)) {
                    int inserted = slot.receive(slot.getEUConversion().getBase((int) Math.floor(amount)), false);
                    amount -= slot.getEUConversion().getUnit(inserted);
                }
            }
            return amount;
        }

        public int getInputTier() {
            int maxVoltage = 0;
            for (Slot slot : slots) {
                maxVoltage = Math.max(maxVoltage, slot.getEUConversion().getUnit(slot.getMaxInput()));
            }
            return EnergyNet.instance.getTierFromPower(maxVoltage);
        }

        public int getOutputTier() {
            int maxVoltage = 0;
            for (Slot slot : slots) {
                maxVoltage = Math.max(maxVoltage, slot.getEUConversion().getUnit(slot.getMaxOutput()));
            }
            return EnergyNet.instance.getTierFromPower(maxVoltage);
        }

        public boolean canInputEnergy(EnumFacing side) {
            for (Slot slot : slots) {
                if (slot.canInput() && slot.getFace().matches(side))
                    return true;
            }
            return false;
        }

        public boolean canOutputEnergy(EnumFacing side) {
            for (Slot slot : slots) {
                if (slot.canOutput() && slot.getFace().matches(side))
                    return true;
            }
            return false;
        }

        public double getOutputEnergy() {
            int toSend = 0;
            for (Slot slot : slots) {
                if (slot.canOutput())
                    toSend += Math.min( slot.getEUConversion().getUnit(slot.getMaxOutput()), slot.energy);
            }
            return toSend;
        }

        public double getInputEnergy() {
            int toReceive = 0;
            for (Slot slot : slots) {
                if (slot.canInput())
                    toReceive +=  slot.getEUConversion().getUnit(slot.getCapacity() - slot.energy);
            }
            return toReceive;
        }
    }

    public static class Collector extends ComponentBase.Collector implements IEnergyStorage {
        ComponentFace face;
        List<Slot> slots = new ArrayList<>();
        int pushIndex;

        public Collector(ComponentFace face) {
            this.face = face;
        }

        private void addSlot(Slot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if (slot.getFace() == face && slot instanceof Slot && ((Slot) slot).acceptsFE()) {
                addSlot((Slot) slot);
                return true;
            }
            return false;
        }

        @Override
        public boolean hasCapability() {
            return true;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityEnergy.ENERGY && face.matches(facing))
                return true;
            return super.hasCapability(capability, facing);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityEnergy.ENERGY && face.matches(facing))
                return CapabilityEnergy.ENERGY.cast(this);
            return super.getCapability(capability, facing);
        }

        private boolean canAutoOutput() {
            for (Slot slot : slots) {
                if (slot.getPushEnergy().active)
                    return true;
            }
            return false;
        }

        @Override
        public void update() {
            if (canAutoOutput() && tile instanceof TileEntityAssembly) {
                World world = tile.getWorld();
                BlockPos pos = tile.getPos();
                EnumFacing facing = TileEntityAssembly.toGlobalSide(((TileEntityAssembly) tile).getFacing(), face.getSide(pushIndex));

                TileEntity checkTile = world.getTileEntity(pos.offset(facing));
                if (checkTile != null && checkTile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                    IEnergyStorage battery = checkTile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                    for (Slot slot : slots) {
                        if (slot.getPushEnergy().active) {
                            int maxSize = slot.getPushEnergy().size;
                            int energy = slot.extract(maxSize, true);
                            int filled = battery.receiveEnergy(energy, false);
                            if (filled > 0) {
                                slot.extract(filled, false);
                            }
                        }
                    }
                }
                pushIndex++;
            }
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = 0;
            for (Slot slot : slots) {
                received += slot.receive(Math.min(maxReceive - received, slot.getMaxInput()), simulate);
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = 0;
            for (Slot slot : slots) {
                extracted += slot.extract(Math.min(maxExtract - extracted, slot.getMaxOutput()), simulate);
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            int energy = 0;
            for (Slot slot : slots) {
                energy += slot.energy;
            }
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            int capacity = 0;
            for (Slot slot : slots) {
                capacity += slot.getCapacity();
            }
            return capacity;
        }

        @Override
        public boolean canExtract() {
            for (Slot slot : slots) {
                if (slot.canOutput())
                    return true;
            }
            return false;
        }

        @Override
        public boolean canReceive() {
            for (Slot slot : slots) {
                if (slot.canInput())
                    return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Collector)
                return face.equals(((Collector) obj).face);
            return false;
        }

        public ComponentFace getFace() {
            return face;
        }
    }
}
