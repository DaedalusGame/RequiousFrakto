package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import requious.compat.crafttweaker.SlotVisualCT;
import requious.data.AssemblyProcessor;
import requious.gui.slot.ItemSlot;
import requious.tile.TileEntityAssembly;
import requious.util.*;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.ItemSlot")
public class ComponentItem extends ComponentBase {
    public boolean inputAllowed = true;
    public boolean outputAllowed = true;
    public boolean shiftAllowed = true;
    public boolean putAllowed = true;
    public boolean takeAllowed = true;
    public boolean dropsOnBreak = true;
    public boolean canOverfill = false;
    public boolean splitAllowed = false;
    public Ingredient filter = new IngredientAny();
    public IOParameters pushItem = new IOParameters();
    public int capacity;

    public SlotVisual background = SlotVisual.ITEM_SLOT;
    public SlotVisual foreground = SlotVisual.EMPTY;

    public ComponentItem(ComponentFace face, int capacity) {
        super(face);
        this.capacity = capacity;
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem setAccess(boolean input, boolean output) {
        inputAllowed = input;
        outputAllowed = output;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem setHandAccess(boolean input, boolean output, @Optional(valueBoolean = true) boolean shift) {
        putAllowed = input;
        takeAllowed = output;
        shiftAllowed = shift;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem setFilter(IIngredient ingredient) {
        filter = CraftTweakerMC.getIngredient(ingredient);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem allowOverfill() {
        this.canOverfill = true;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem allowSplit() {
        this.splitAllowed = true;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem noDrop() {
        this.dropsOnBreak = false;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem pushItem(int size, int slot) {
        this.pushItem = new IOParameters(size,slot);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem pushItem(int size) {
        this.pushItem = new IOParameters(size);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem setBackground(SlotVisualCT visual) {
        this.background = SlotVisualCT.unpack(visual);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentItem setForeground(SlotVisualCT visual) {
        this.foreground = SlotVisualCT.unpack(visual);
        return this;
    }

    public static class Slot extends ComponentBase.Slot<ComponentItem> implements IItemSlot {
        ItemComponentHelper item;

        public Slot(ComponentItem component) {
            super(component);
            item = new ItemComponentHelper() {
                @Override
                public int getCapacity() {
                    if(canOverfill() && getAmount() == 0)
                        return Integer.MAX_VALUE;
                    return component.capacity;
                }
            };
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            Collector item = new Collector(getFace());

            if(!collectors.contains(item))
                collectors.add(item);
        }
        @Override
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return new ItemSlot(assembly,this,x,y);
        }

        @Override
        public void update() {

        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            if(component.dropsOnBreak) {
                item.spawnInWorld(world, position);
                item.setStack(ItemStack.EMPTY);
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("item",item.writeToNBT(new NBTTagCompound()));
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {
            item.readFromNBT(compound.getCompoundTag("item"));
        }

        @Override
        public ItemComponentHelper getItem() {
            return item;
        }

        public boolean acceptsItem(ItemStack stack) {
            return component.filter.apply(stack);
        }

        public boolean canInputItem() {
            return component.inputAllowed;
        }

        public boolean canOutputItem() {
            return component.outputAllowed;
        }

        public boolean canPut() {
            return !component.hidden && component.putAllowed;
        }

        public boolean canTake() {
            return !component.hidden && component.takeAllowed;
        }

        public boolean canOverfill() {
            return component.canOverfill;
        }

        public IOParameters getPushItem() {
            return component.pushItem;
        }

        public boolean canSplit() {
            return component.splitAllowed;
        }

        @Override
        public boolean canShift() {
            return component.shiftAllowed;
        }

        @Override
        public boolean isDirty() {
            return super.isDirty() || item.isDirty();
        }

        @Override
        public void markClean() {
            super.markClean();
            item.markClean();
        }

        public SlotVisual getBackground() {
            return component.background;
        }

        public SlotVisual getForeground() {
            return component.foreground;
        }
    }

    public interface IItemSlot {
        ItemComponentHelper getItem();

        boolean canInputItem();

        boolean canOutputItem();

        IOParameters getPushItem();

        boolean canSplit();

        boolean acceptsItem(ItemStack stack);
    }

    public static class Collector extends ComponentBase.Collector implements IItemHandler {
        ComponentFace face;
        List<IItemSlot> slots = new ArrayList<>();
        int pushIndex;

        public Collector(ComponentFace face) {
            this.face = face;
        }

        private void addSlot(IItemSlot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if(slot.getFace() == face && slot instanceof IItemSlot) {
                addSlot((IItemSlot) slot);
                return true;
            }
            return false;
        }

        @Override
        public boolean hasCapability() {
            return true;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && face.matches(localSide,globalSide))
                return true;
            return super.hasCapability(capability, localSide,globalSide);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && face.matches(localSide,globalSide))
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
            return super.getCapability(capability, localSide,globalSide);
        }

        private boolean canAutoOutput() {
            for (IItemSlot slot : slots) {
                if(slot.getPushItem().active)
                    return true;
            }
            return false;
        }

        @Override
        public void update() {
            if(canAutoOutput() && tile instanceof TileEntityAssembly) {
                World world = tile.getWorld();
                BlockPos pos = tile.getPos();
                EnumFacing facing = TileEntityAssembly.toSide(((TileEntityAssembly) tile).getFacing(),face.getSide(pushIndex));

                TileEntity checkTile = world.getTileEntity(pos.offset(facing));
                if(checkTile != null && checkTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing.getOpposite())) {
                    IItemHandler inventory = checkTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing.getOpposite());
                    for (IItemSlot slot : slots) {
                        if(slot.getPushItem().active) {
                            int maxSize = slot.getPushItem().size;
                            int targetSlot = slot.getPushItem().slot;
                            ItemStack insertStack = slot.getItem().extract(maxSize,true);
                            int startSize = insertStack.getCount();
                            if(targetSlot < 0) {
                                for(int i = 0; i < inventory.getSlots(); i++) {
                                    insertStack = inventory.insertItem(i,insertStack,false);
                                    if(insertStack.isEmpty())
                                        break;
                                }
                            } else {
                                insertStack = inventory.insertItem(targetSlot,insertStack,false);
                            }
                            if(insertStack.getCount() < startSize) {
                                slot.getItem().extract(startSize - insertStack.getCount(),false);
                            }
                        }
                    }
                }
                pushIndex++;
            }
        }

        @Override
        public int getSlots() {
            return slots.size();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int i) {
            IItemSlot slot = slots.get(i);
            return slot.getItem().getStack();
        }

        private boolean hasItemStored(ItemStack stack) {
            for (IItemSlot slot : slots) {
                if(!slot.getItem().isEmpty() && slot.getItem().canStack(stack))
                    return true;
            }
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int i, @Nonnull ItemStack stack, boolean simulate) {
            IItemSlot slot = slots.get(i);
            if(slot.canInputItem() && slot.acceptsItem(stack) && (slot.canSplit() || (!slot.getItem().isEmpty() && slot.getItem().canStack(stack)) || !hasItemStored(stack)))
                return slot.getItem().insert(stack,simulate);
            else
                return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int i, int amount, boolean simulate) {
            IItemSlot slot = slots.get(i);
            if(slot.canOutputItem())
                return slot.getItem().extract(amount,simulate);
            else
                return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int i) {
            IItemSlot slot = slots.get(i);
            return slot.getItem().getCapacity();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Collector)
                return face.equals(((Collector) obj).face);
            return false;
        }
    }
}
