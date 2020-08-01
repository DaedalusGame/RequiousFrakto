package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import requious.compat.crafttweaker.SlotVisualCT;
import requious.data.AssemblyProcessor;
import requious.gui.slot.SelectSlot;
import requious.util.ComponentFace;
import requious.util.SlotVisual;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.SelectionSlot")
public class ComponentSelection extends ComponentBase {
    int index;
    String selectionGroup;
    Integer maxSelection;

    public SlotVisual background = SlotVisual.SELECTION_SLOT;
    public SlotVisual foreground = SlotVisual.EMPTY;

    public ComponentSelection(String selectionGroup, int index) {
        super(ComponentFace.None);
        this.selectionGroup = selectionGroup;
        this.index = index;
    }

    @ZenMethod
    public ComponentSelection setMaxSelection(int max) {
        maxSelection = max;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentSelection setBackground(SlotVisualCT visual) {
        this.background = SlotVisualCT.unpack(visual);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentSelection setForeground(SlotVisualCT visual) {
        this.foreground = SlotVisualCT.unpack(visual);
        return this;
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    public static class Slot extends ComponentBase.Slot<ComponentSelection> {
        SelectionList selectionList;

        public Slot(ComponentSelection component) {
            super(component);
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            Collector item = new Collector(this, getSelectionGroup());

            if(!collectors.contains(item)) {
                collectors.add(item);
            }
        }
        @Override
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return new SelectSlot(assembly,this,x,y);
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

            if(isMaster()) {
                selectionList.serializeNBT(compound);
            }

            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {
            if(isMaster()) {
                selectionList.deserializeNBT(compound);
            }
        }

        @Override
        public boolean isDirty() {
            return super.isDirty();
        }

        @Override
        public void markClean() {
            super.markClean();
        }

        public String getSelectionGroup() {
            return component.selectionGroup;
        }

        public void setSelectionList(SelectionList selectionList) {
            this.selectionList = selectionList;
        }

        public boolean isMaster() {
            return selectionList.master == this;
        }

        public SelectionList getSelectionList() {
            return selectionList;
        }

        public ItemStack getSelection() {
            return selectionList.get(component.index);
        }

        public boolean isSelected() {
            return selectionList.isSelected(component.index);
        }

        public void select() {
            selectionList.select(getSelection());
        }

        public void unselect() {
            selectionList.unselect(getSelection());
        }

        public void scroll(int i) {
            selectionList.scroll(i);
        }

        public void addSelection(ItemStack icon) {
            selectionList.add(icon);
        }

        public SlotVisual getBackground() {
            return component.background;
        }

        public SlotVisual getForeground() {
            return component.foreground;
        }
    }

    public static class Collector extends ComponentBase.Collector {
        String selectionGroup;
        SelectionList selectionList;
        List<Slot> slots = new ArrayList<>();

        public Collector(Slot master, String selectionGroup) {
            this.selectionList = new SelectionList(master);
            this.selectionGroup = selectionGroup;
        }

        private void addSlot(Slot slot) {
            slots.add(slot);
            slot.setSelectionList(selectionList);
            selectionList.setProperties(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if(slot instanceof Slot && selectionGroup.equals(((Slot) slot).getSelectionGroup())) {
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
            selectionList.reset();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Collector)
                return selectionGroup.equals(((Collector) obj).selectionGroup);
            return false;
        }
    }

    public static class SelectionList {
        Slot master;
        List<ItemStack> selectedItems = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();
        int maxSelection = Integer.MAX_VALUE;
        int scroll;

        public SelectionList(Slot master) {
            this.master = master;
            setProperties(master);
        }

        public void setProperties(Slot slot) {
            if(slot.component.maxSelection != null)
                maxSelection = slot.component.maxSelection;
        }

        public void reset() {
            items.clear();
        }

        public boolean isSelected(int index) {
            if(index < selectedItems.size())
                return true;
            else
                return false;
        }

        public ItemStack get(int index) {
            int maxIndex = 0;
            for (ItemStack stack : items) {
                if(!listContains(selectedItems,stack))
                    maxIndex++;
            }
            maxIndex += selectedItems.size();
            if(index >= maxIndex)
                return ItemStack.EMPTY;
            if(index < selectedItems.size())
                return getSelected(index);
            else
                return getUnselected(index - selectedItems.size());
        }

        public ItemStack getSelected(int index) {
            return selectedItems.get(index);
        }

        private boolean isSelected(ItemStack stack) {
            for (ItemStack selection : selectedItems) {
                if(ItemStack.areItemStacksEqual(selection,stack) && selection.getCount() == stack.getCount())
                    return true;
            }
            return false;
        }

        public ItemStack getUnselected(int index) {
            //if(selectedItems.size() > items.size()) //prevent division by 0
            //    return ItemStack.EMPTY;
            index = positiveModulo(index + scroll, Math.max(items.size() - selectedItems.size(),1));
            for (ItemStack selection : items) {
                if (isSelected(selection))
                    continue;
                if(index <= 0)
                    return selection;
                index--;
            }
            return ItemStack.EMPTY;
        }

        private int positiveModulo(int n, int mod) {
            int i = n % mod;
            return i < 0 ? i + mod : i;
        }

        public void scroll(int i) {
            scroll += i;
            master.markDirty();
        }

        public void add(ItemStack item) {
            if(!listContains(items, item))
                items.add(item);
        }

        public void select(ItemStack item) {
            if(!item.isEmpty() && !listContains(selectedItems, item)) {
                if(selectedItems.size() >= maxSelection)
                    selectedItems.remove(0);
                selectedItems.add(item);
                master.markDirty();
            }
        }

        private boolean listContains(List<ItemStack> items, ItemStack check) {
            for (ItemStack stack : items) {
                if(ItemStack.areItemStacksEqual(stack,check) && stack.getCount() == check.getCount())
                    return true;
            }
            return false;
        }

        public void unselect(ItemStack item) {
            selectedItems.remove(item);
            master.markDirty();
        }


        public NBTTagCompound serializeNBT(NBTTagCompound compound) {
            NBTTagList list = new NBTTagList();
            for (ItemStack stack : selectedItems) {
                list.appendTag(stack.serializeNBT());
            }
            compound.setTag("selected",list);
            compound.setInteger("scroll",scroll);
            return compound;
        }

        public void deserializeNBT(NBTTagCompound compound) {
            selectedItems.clear();
            NBTTagList list = compound.getTagList("selected", 10);
            for(NBTBase element : list) {
                selectedItems.add(new ItemStack((NBTTagCompound) element));
            }
            scroll = compound.getInteger("scroll");
        }
    }
}
