package requious.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import requious.Requious;
import requious.data.BatteryData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class ItemBattery extends Item implements IDynamicItemModel {
    BatteryData data;

    public ItemBattery(BatteryData data) {
        this.data = data;
        this.addPropertyOverride(new ResourceLocation(Requious.MODID, "energy"), (stack, worldIn, entityIn) -> {
            IEnergyStorage battery = stack.getCapability(CapabilityEnergy.ENERGY,null);
            if(battery != null)
                return (float)battery.getEnergyStored() / battery.getMaxEnergyStored();
            return 0;
        });
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return data.stackSize;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int energy = getEnergy(stack);
        int capacity = getCapacity();

        if(data.showToolip)
            tooltip.add(I18n.format("requious.unit."+getUnit(),energy,capacity));
    }

    private String getUnit() {
        return "fe"; //For now
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(getEmpty());
            /*for(int i = 1; i <= 10; i++) { //DEBUG
                items.add(getFull((double)i / 10));
            }*/
            items.add(getFull());
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return data.hasBar();
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        double lerp = 1.0-getDurabilityForDisplay(stack);
        return data.barColor.get(lerp).getRGB();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int energy = getEnergy(stack);
        return 1.0 - (double) energy / getCapacity();
    }

    public ItemStack getEmpty() {
        return new ItemStack(this);
    }

    public ItemStack getFull() {
        return getFull(1.0);
    }

    public ItemStack getFull(double ratio) {
        ItemStack stack = getEmpty();
        setEnergy(stack, (int) (getCapacity() * ratio));
        return stack;
    }

    private int getCapacity() {
        return data.capacity;
    }

    private int getEnergy(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null)
            return compound.getInteger("energy");
        return 0;
    }

    private void setEnergy(ItemStack stack, int energy) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null)
            compound = new NBTTagCompound();
        compound.setInteger("energy", energy);
        stack.setTagCompound(compound);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {
        return new ItemBattery.BatteryCapability(stack);
    }

    @Override
    public Color getTint(ItemStack stack, int tintIndex) {
        return data.getColor(stack,tintIndex);
    }

    private class BatteryCapability implements ICapabilityProvider, IEnergyStorage {
        ItemStack stack;

        public BatteryCapability(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityEnergy.ENERGY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if(capability == CapabilityEnergy.ENERGY)
                return (T) this;
            return null;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int energy = getEnergyStored();
            int received = Math.min(maxReceive,getCapacity() - energy);
            if(received < getMinInput())
                return 0;
            received = Math.min(received,getMaxInput());
            if(!simulate)
                setEnergy(stack,energy + received);
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int energy = getEnergyStored();
            int extracted = Math.min(maxExtract, energy);
            if(extracted < getMinOutput())
                return 0;
            extracted = Math.min(extracted,getMaxOutput());
            if(!simulate)
                setEnergy(stack,energy - extracted);
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return getEnergy(stack);
        }

        @Override
        public int getMaxEnergyStored() {
            return getCapacity();
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        private int getMinInput() {
            return data.minInput;
        }

        private int getMaxInput() {
            return data.maxInput;
        }

        private int getMinOutput() {
            return data.minOutput;
        }

        private int getMaxOutput() {
            return data.maxOutput;
        }
    }
}
