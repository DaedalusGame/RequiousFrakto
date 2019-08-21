package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import requious.compat.crafttweaker.AutomataCell;
import requious.compat.crafttweaker.IAutomataStep;
import requious.gui.slot.AutomataSlot;
import requious.util.ComponentFace;
import requious.util.IOParameters;
import requious.util.ItemComponentHelper;
import stanhebben.zenscript.annotations.ZenClass;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.AutomataSlot")
public class ComponentAutomata extends ComponentItem {
    public ComponentAutomata(ComponentFace face, int capacity) {
        super(face,capacity);
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    public static class Slot extends ComponentBase.Slot<ComponentAutomata> implements IItemSlot {
        ItemComponentHelper item;
        ItemStack nextItem;
        List<GroupStep> steps = new ArrayList<>();

        public Slot(ComponentAutomata component) {
            super(component);
            item = new ItemComponentHelper() {
                @Override
                public int getCapacity() {
                    return component.capacity;
                }
            };
        }

        public int getX() {
            return component.x;
        }

        public int getY() {
            return component.y;
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {

            ComponentItem.Collector item = new ComponentItem.Collector(getFace());

            for (String group : component.groups) {
                Collector automata = new Collector(group);
                if(!collectors.contains(automata))
                    collectors.add(automata);
            }
            if(!collectors.contains(item))
                collectors.add(item);
        }
        @Override
        public net.minecraft.inventory.Slot createGui(int x, int y) {
            return new AutomataSlot(this,x,y);
        }

        @Override
        public void update() {
            //NOOP
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            //NOOP
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

        @Override
        public boolean isDirty() {
            return super.isDirty() || item.isDirty();
        }

        @Override
        public void markClean() {
            super.markClean();
            item.markClean();
        }

        public void addStep(String group, IAutomataStep step) {
            steps.add(new GroupStep(group, step));
        }

        @Override
        public IOParameters getPushItem() {
            return component.pushItem;
        }

        @Override
        public boolean canSplit() {
            return component.splitAllowed;
        }
    }

    public static class GroupStep {
        public IAutomataStep step;
        public String group;

        public GroupStep(String group, IAutomataStep step) {
            this.step = step;
            this.group = group;
        }
    }

    public static class Collector extends ComponentBase.Collector implements IItemHandler {
        String group;
        List<Slot> slots = new ArrayList<>();

        public Collector(String group) {
            this.group = group;
        }

        private void addSlot(Slot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if(slot instanceof Slot && slot.isGroup(group)) {
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
            List<IAutomataStep> steps = new ArrayList<>();
            for (Slot slot : slots) {
                Iterator<GroupStep> iterator = slot.steps.iterator();
                while(iterator.hasNext())
                {
                    GroupStep step = iterator.next();
                    if(step.group.equals(group)) {
                        steps.add(step.step);
                        iterator.remove();
                    }
                }
            }
            for(IAutomataStep step : steps) {
                for (Slot slot : slots) {
                    AutomataCell cell = new AutomataCell(this, slot);
                    slot.nextItem = CraftTweakerMC.getItemStack(step.getResult(cell));
                }
                for (Slot slot : slots) {
                    slot.item.setStack(slot.nextItem);
                }
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

        @Nonnull
        @Override
        public ItemStack insertItem(int i, @Nonnull ItemStack stack, boolean simulate) {
            IItemSlot slot = slots.get(i);
            if(slot.canInput())
                return slot.getItem().insert(stack,simulate);
            else
                return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int i, int amount, boolean simulate) {
            IItemSlot slot = slots.get(i);
            if(slot.canOutput())
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
                return group.equals(((Collector) obj).group);
            return false;
        }

        public boolean exists(int x, int y) {
            for (Slot slot : slots) {
                if(slot.getX() == x && slot.getY() == y)
                    return true;
            }

            return false;
        }

        public ItemStack getStackAt(int x, int y) {
            for (Slot slot : slots) {
                if(slot.getX() == x && slot.getY() == y)
                    return slot.item.getStack();
            }

            return ItemStack.EMPTY;
        }
    }
}
