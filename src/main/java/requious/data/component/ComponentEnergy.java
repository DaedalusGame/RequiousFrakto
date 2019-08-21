package requious.data.component;

import crafttweaker.annotations.ZenRegister;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import requious.Requious;
import requious.compat.crafttweaker.GaugeDirectionCT;
import requious.gui.GaugeDirection;
import requious.gui.slot.EnergySlot;
import requious.recipe.ConsumptionResult;
import requious.tile.TileEntityAssembly;
import requious.util.ComponentFace;
import requious.util.IOParameters;
import requious.util.ItemComponentHelper;
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

    public ResourceLocation tex = new ResourceLocation(Requious.MODID, "textures/gui/assembly_gauges.png");
    public int texX, texY;
    public GaugeDirection texDirection = GaugeDirection.UP;
    public String unit = "fe";

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
        setTexture(x,y,direction);
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
    public ComponentEnergy pushItem(int size, int slot) {
        this.pushItem = new IOParameters(size,slot);
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

    public static class Slot extends ComponentBase.Slot<ComponentEnergy> implements ComponentItem.IItemSlot {
        int energy;
        float powerLoss;
        ItemComponentHelper battery;
        boolean active;

        public Slot(ComponentEnergy component) {
            super(component);
            battery = new ItemComponentHelper() {
                @Override
                public int getCapacity() {
                    return 1;
                }
            };
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            Collector energy = new Collector(getFace());
            ComponentItem.Collector item = new ComponentItem.Collector(getFace());

            if(!collectors.contains(energy))
                collectors.add(energy);
            if(!collectors.contains(item))
                collectors.add(item);
        }

        @Override
        public net.minecraft.inventory.Slot createGui(int x, int y) {
            return new EnergySlot(this,x,y);
        }

        @Override
        public void update() {
            if(!active && energy > 0) {
                powerLoss += component.powerLoss;
                int intLoss = (int)powerLoss;
                if(intLoss > 0) {
                    energy = Math.max(0, energy - intLoss);
                    powerLoss -= intLoss;
                }
                markDirty();
            }
            active = false;
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            if(component.dropsOnBreak) {
                battery.spawnInWorld(world, position);
                battery.setStack(ItemStack.EMPTY);
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("energy",energy);
            compound.setFloat("loss",powerLoss);
            compound.setTag("battery",battery.writeToNBT(new NBTTagCompound()));
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
            if(canOverfill() && energy <= 0)
                return Integer.MAX_VALUE;
            return component.capacity;
        }

        public int getAmount(){
            return energy;
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

        public int receive(int i, boolean simulate) {
            int received = Math.min(i,getCapacity()-energy);
            if(!simulate) {
                energy += received;
                active = true;
                markDirty();
            }
            return received;
        }

        public int extract(int i, boolean simulate) {
            int extracted = Math.min(i,energy);
            if(!simulate) {
                energy -= extracted;
                active = false;
                markDirty();
            }
            return extracted;
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
            if(slot.getFace() == face && slot instanceof Slot) {
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
            if(capability == CapabilityEnergy.ENERGY && face.matches(facing))
                return true;
            return super.hasCapability(capability, facing);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if(capability == CapabilityEnergy.ENERGY && face.matches(facing))
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
                received += slot.receive(maxReceive - received,simulate);
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = 0;
            for (Slot slot : slots) {
                extracted += slot.extract(maxExtract - extracted,simulate);
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
                if(slot.canOutput())
                    return true;
            }
            return false;
        }

        @Override
        public boolean canReceive() {
            for (Slot slot : slots) {
                if(slot.canInput())
                    return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Collector)
                return face.equals(((Collector) obj).face);
            return false;
        }
    }
}
