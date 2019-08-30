package requious.item;

import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import requious.Requious;
import requious.data.FluidCellData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class ItemFluidCell extends Item implements IDynamicItemModel {
    FluidCellData data;

    public ItemFluidCell(FluidCellData data) {
        this.data = data;
        this.addPropertyOverride(new ResourceLocation(Requious.MODID, "fluid"), (stack, worldIn, entityIn) -> {
            FluidStack fluid = FluidUtil.getFluidContained(stack);
            if (fluid != null)
                return (float) fluid.amount / getCapacity();
            return 0;
        });
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        FluidStack fluid = getFluid(stack);
        int capacity = getCapacity();

        if(data.showToolip) {
            if (fluid == null)
                tooltip.add(I18n.format("requious.fluid.empty"));
            else
                tooltip.add(I18n.format("requious.fluid", fluid.getLocalizedName(), fluid.amount, capacity));
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return data.stackSize;
    }

    @Override
    public boolean getHasSubtypes() {
        return data.generateSubItems;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(getEmpty());
            if (data.generateSubItems)
                for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
                    FluidStack stack = new FluidStack(fluid, data.capacity);
                    if (data.filter == null || data.filter.matches(CraftTweakerMC.getILiquidStack(stack)))
                        items.add(getFilled(stack));
                }
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        FluidStack fluid = getFluid(stack);
        return fluid != null && data.hasBar();
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        double lerp = 1.0 - getDurabilityForDisplay(stack);
        return data.barColor.get(lerp).getRGB();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        FluidStack fluid = getFluid(stack);
        if (fluid != null)
            return 1.0 - (double) fluid.amount / getCapacity();
        else
            return 1.0;
    }


    public ItemStack getEmpty() {
        return new ItemStack(this);
    }

    public ItemStack getFilled(FluidStack fluid) {
        ItemStack container = getEmpty();
        NBTTagCompound fluidTag = new NBTTagCompound();
        if (fluid != null)
            fluidTag = fluid.writeToNBT(fluidTag);
        NBTTagCompound compound = container.getTagCompound();
        if (compound == null)
            compound = new NBTTagCompound();
        compound.setTag("fluidContent", fluidTag);
        container.setTagCompound(compound);
        return container;
    }

    public FluidStack getFluid(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("fluidContent"))
            return FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("fluidContent"));
        return null;
    }

    private int getCapacity() {
        return data.capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {
        return new FluidCellCapability(stack);
    }

    @Override
    public Color getTint(ItemStack stack, int tintIndex) {
        return data.getColor(stack, tintIndex);
    }

    public class FluidCellCapability implements IFluidHandlerItem, ICapabilityProvider {
        @Nonnull
        protected ItemStack container;

        public FluidCellCapability(@Nonnull ItemStack container) {
            this.container = container;
        }

        @Nonnull
        @Override
        public ItemStack getContainer() {
            return container;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[]{new FluidTankProperties(getFluid(container), getCapacity())};
        }

        private void setFluid(FluidStack resource) {
            if (resource == null || resource.amount <= 0)
                container = getEmpty();
            else
                container = getFilled(resource);
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (container.getCount() != 1 || resource == null)
                return 0;
            FluidStack fluid = getFluid(container);
            int maxFill = Math.min(resource.amount, getMaxInput());
            if(fluid != null) {
                if(!fluid.isFluidEqual(resource))
                    return 0;
                maxFill = Math.min(maxFill,getCapacity() - fluid.amount);
            } else {
                maxFill = Math.min(maxFill,getCapacity());
                fluid = resource.copy();
                fluid.amount = 0;
            }

            if(maxFill < getMinInput())
                return 0;

            fluid.amount += maxFill;

            if (doFill) {
                setFluid(fluid);
            }

            return maxFill;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (container.getCount() != 1 || resource == null)
                return null;
            FluidStack fluid = getFluid(container);
            int maxDrain = Math.min(resource.amount, getMaxOutput());
            if (fluid != null && fluid.isFluidEqual(resource) && resource.amount >= getMinOutput()) {
                FluidStack drained = fluid.copy();
                drained.amount = Math.min(maxDrain, fluid.amount);
                fluid.amount -= drained.amount;
                if (fluid.amount < 0)
                    fluid = null;
                if (doDrain)
                    setFluid(fluid);
                return drained;
            }
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if (container.getCount() != 1)
                return null;
            FluidStack fluid = getFluid(container);
            maxDrain = Math.min(maxDrain, getMaxOutput());
            if (fluid != null && maxDrain >= getMinOutput()) {
                FluidStack drained = fluid.copy();
                drained.amount = Math.min(maxDrain, fluid.amount);
                fluid.amount -= drained.amount;
                if (fluid.amount < 0)
                    fluid = null;
                if (doDrain)
                    setFluid(fluid);
                return drained;
            }
            return null;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
            }
            return null;
        }
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
