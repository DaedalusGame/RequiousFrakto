package requious.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentBase;
import requious.gui.slot.BaseSlot;

import javax.annotation.Nullable;

public class ContainerAssembly extends Container {
    private AssemblyProcessor processor;
    int machineSlots;

    public ContainerAssembly(EntityPlayer player, AssemblyProcessor processor) {
        this.processor = processor;

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 5; y++) {
                ComponentBase.Slot slot = processor.getSlot(x,y);
                if(slot != null) {
                    Slot guiSlot = slot.createGui(processor,8 + x * 18, 7 + y * 18);
                    if (guiSlot != null) {
                        addSlotToContainer(guiSlot);
                        machineSlots++;
                    }
                }
            }
        }

        bindPlayerInventory(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null));
    }

    protected void bindPlayerInventory(IItemHandler inventoryPlayer) {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                addSlotToContainer(new SlotItemHandler(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            addSlotToContainer(new SlotItemHandler(inventoryPlayer, i, 8 + i * 18, 160));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    private static boolean canShiftPut(Slot slot) {
        if(slot instanceof BaseSlot)
            return ((BaseSlot) slot).canShiftPut();
        return true;
    }

    private static boolean canShiftTake(Slot slot) {
        if(slot instanceof BaseSlot)
            return ((BaseSlot) slot).canShiftTake();
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        int slots = machineSlots;

        if(slot != null && slot.getHasStack() && canShiftTake(slot))
        {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if(index < slots) {
                if(!mergeItemStack(stack1, slots, this.inventorySlots.size(), true))
                    return ItemStack.EMPTY;
            } else if(!mergeItemStack(stack1, 0, slots, false))
                return ItemStack.EMPTY;

            slot.putStack(stack1);
            if (!stack1.isEmpty()) {
                slot.onSlotChanged();
            }
        }
        return stack;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        //listener.sendWindowProperty(this, 0, this.tileChute.power);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : this.listeners) {
            //if (this.lastMechPower != this.tileChute.power)
            //    listener.sendWindowProperty(this, 0, this.tileChute.power);
        }
    }

    @Override
    public void updateProgressBar(int index, int value) {
        //if (index == 0)
        //    this.tileChute.power = (byte) value;
    }

    public static boolean canAddItemToSlot(@Nullable Slot slotIn, ItemStack stack, boolean stackSizeMatters) {
        boolean flag = slotIn == null || !slotIn.getHasStack();
        ItemStack slotStack = slotIn.getStack();

        if (!flag && stack.isItemEqual(slotStack) && ItemStack.areItemStackTagsEqual(slotStack, stack)) {
            return slotStack.getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= slotIn.getItemStackLimit(slotStack);
        }

        return flag;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        InventoryPlayer inventoryplayer = player.inventory;

        if (clickTypeIn == ClickType.QUICK_CRAFT)
        {
            int j1 = this.dragEvent;
            this.dragEvent = getDragEvent(dragType);

            if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent)
            {
                this.resetDrag();
            }
            else if (inventoryplayer.getItemStack().isEmpty())
            {
                this.resetDrag();
            }
            else if (this.dragEvent == 0)
            {
                this.dragMode = extractDragMode(dragType);

                if (isValidDragMode(this.dragMode, player))
                {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                }
                else
                {
                    this.resetDrag();
                }
            }
            else if (this.dragEvent == 1)
            {
                Slot slot7 = this.inventorySlots.get(slotId);
                ItemStack itemstack12 = inventoryplayer.getItemStack();

                if (slot7 != null && canAddItemToSlot(slot7, itemstack12, true) && slot7.isItemValid(itemstack12) && (this.dragMode == 2 || itemstack12.getCount() > this.dragSlots.size()) && this.canDragIntoSlot(slot7))
                {
                    this.dragSlots.add(slot7);
                }
            }
            else if (this.dragEvent == 2)
            {
                if (!this.dragSlots.isEmpty())
                {
                    ItemStack dragStack = inventoryplayer.getItemStack().copy();
                    int k1 = inventoryplayer.getItemStack().getCount();

                    for (Slot slot8 : this.dragSlots)
                    {
                        ItemStack itemstack13 = inventoryplayer.getItemStack();

                        if (slot8 != null && canAddItemToSlot(slot8, itemstack13, true) && slot8.isItemValid(itemstack13) && (this.dragMode == 2 || itemstack13.getCount() >= this.dragSlots.size()) && this.canDragIntoSlot(slot8))
                        {
                            ItemStack itemstack14 = dragStack.copy();
                            int j3 = slot8.getHasStack() ? slot8.getStack().getCount() : 0;
                            computeStackSize(this.dragSlots, this.dragMode, itemstack14, j3);
                            int k3 = slot8.getItemStackLimit(itemstack14);

                            if (itemstack14.getCount() > k3)
                            {
                                itemstack14.setCount(k3);
                            }

                            k1 -= itemstack14.getCount() - j3;
                            slot8.putStack(itemstack14);
                        }
                    }

                    dragStack.setCount(k1);
                    inventoryplayer.setItemStack(dragStack);
                }

                this.resetDrag();
            }
            else
            {
                this.resetDrag();
            }
        }
        else if (this.dragEvent != 0)
        {
            this.resetDrag();
        }
        else if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1))
        {
            if (slotId == -999)
            {
                if (!inventoryplayer.getItemStack().isEmpty())
                {
                    if (dragType == 0)
                    {
                        player.dropItem(inventoryplayer.getItemStack(), true);
                        inventoryplayer.setItemStack(ItemStack.EMPTY);
                    }

                    if (dragType == 1)
                    {
                        player.dropItem(inventoryplayer.getItemStack().splitStack(1), true);
                    }
                }
            }
            else if (clickTypeIn == ClickType.QUICK_MOVE)
            {
                if (slotId < 0)
                {
                    return ItemStack.EMPTY;
                }

                Slot slot5 = this.inventorySlots.get(slotId);

                if (slot5 == null || !slot5.canTakeStack(player))
                {
                    return ItemStack.EMPTY;
                }

                for (ItemStack itemstack7 = this.transferStackInSlot(player, slotId); !itemstack7.isEmpty() && ItemStack.areItemsEqual(slot5.getStack(), itemstack7); itemstack7 = this.transferStackInSlot(player, slotId))
                {
                    itemstack = itemstack7.copy();
                }
            }
            else
            {
                if (slotId < 0)
                {
                    return ItemStack.EMPTY;
                }

                Slot slot6 = this.inventorySlots.get(slotId);

                if (slot6 != null)
                {
                    ItemStack itemstack8 = slot6.getStack();
                    ItemStack dragStack = inventoryplayer.getItemStack();

                    if (!itemstack8.isEmpty())
                    {
                        itemstack = itemstack8.copy();
                        itemstack.setCount(Math.min(itemstack.getCount(),itemstack.getMaxStackSize()));
                    }

                    if (itemstack8.isEmpty())
                    {
                        if (!dragStack.isEmpty() && slot6.isItemValid(dragStack))
                        {
                            int i3 = dragType == 0 ? dragStack.getCount() : 1;

                            if (i3 > slot6.getItemStackLimit(dragStack))
                            {
                                i3 = slot6.getItemStackLimit(dragStack);
                            }

                            slot6.putStack(dragStack.splitStack(i3));
                        }
                    }
                    else
                    {
                        if (dragStack.isEmpty() && slot6.canTakeStack(player))
                        {
                            if (itemstack8.isEmpty())
                            {
                                slot6.putStack(ItemStack.EMPTY);
                                inventoryplayer.setItemStack(ItemStack.EMPTY);
                            }
                            else
                            {
                                int l2 = dragType == 0 ? itemstack8.getCount() : (itemstack8.getCount() + 1) / 2;
                                inventoryplayer.setItemStack(slot6.decrStackSize(Math.min(l2,itemstack8.getMaxStackSize())));

                                if (slot6.getStack().isEmpty())
                                {
                                    slot6.putStack(ItemStack.EMPTY);
                                }

                                slot6.onTake(player, inventoryplayer.getItemStack());
                            }
                        }
                        else if (slot6.isItemValid(dragStack))
                        {
                            if (itemstack8.getItem() == dragStack.getItem() && itemstack8.getMetadata() == dragStack.getMetadata() && ItemStack.areItemStackTagsEqual(itemstack8, dragStack))
                            {
                                int k2 = dragType == 0 ? dragStack.getCount() : 1;

                                if (k2 > slot6.getItemStackLimit(dragStack) - itemstack8.getCount())
                                {
                                    k2 = slot6.getItemStackLimit(dragStack) - itemstack8.getCount();
                                }

                                /*if (k2 > dragStack.getMaxStackSize() - itemstack8.getCount())
                                {
                                    k2 = dragStack.getMaxStackSize() - itemstack8.getCount();
                                }*/

                                dragStack.shrink(k2);
                                if(slot6 instanceof BaseSlot)
                                    ((BaseSlot) slot6).incrStack(k2);
                                else
                                    itemstack8.grow(k2);
                            }
                            else if (slot6.canTakeStack(player) && dragStack.getCount() <= slot6.getItemStackLimit(dragStack) && itemstack8.getCount() <= itemstack8.getMaxStackSize())
                            {
                                slot6.putStack(dragStack);
                                inventoryplayer.setItemStack(itemstack8);
                            }
                        }
                        else if (itemstack8.getItem() == dragStack.getItem() && dragStack.getMaxStackSize() > 1 && (!itemstack8.getHasSubtypes() || itemstack8.getMetadata() == dragStack.getMetadata()) && ItemStack.areItemStackTagsEqual(itemstack8, dragStack) && !itemstack8.isEmpty())
                        {
                            int j2 = itemstack8.getCount();

                            if (j2 + dragStack.getCount() <= dragStack.getMaxStackSize())
                            {
                                dragStack.grow(j2);
                                itemstack8 = slot6.decrStackSize(j2);

                                if (itemstack8.isEmpty())
                                {
                                    slot6.putStack(ItemStack.EMPTY);
                                }

                                slot6.onTake(player, inventoryplayer.getItemStack());
                            }
                        }
                    }

                    slot6.onSlotChanged();
                }
            }
        }
        else if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9)
        {
            Slot slot4 = this.inventorySlots.get(slotId);
            ItemStack itemstack6 = inventoryplayer.getStackInSlot(dragType);
            ItemStack itemstack10 = slot4.getStack();

            if (!itemstack6.isEmpty() || !itemstack10.isEmpty())
            {
                if (itemstack6.isEmpty())
                {
                    if (slot4.canTakeStack(player))
                    {
                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
                        slot4.onSwapCraft(itemstack10.getCount());
                        slot4.putStack(ItemStack.EMPTY);
                        slot4.onTake(player, itemstack10);
                    }
                }
                else if (itemstack10.isEmpty())
                {
                    if (slot4.isItemValid(itemstack6))
                    {
                        int l1 = slot4.getItemStackLimit(itemstack6);

                        if (itemstack6.getCount() > l1)
                        {
                            slot4.putStack(itemstack6.splitStack(l1));
                        }
                        else
                        {
                            slot4.putStack(itemstack6);
                            inventoryplayer.setInventorySlotContents(dragType, ItemStack.EMPTY);
                        }
                    }
                }
                else if (slot4.canTakeStack(player) && slot4.isItemValid(itemstack6))
                {
                    int i2 = slot4.getItemStackLimit(itemstack6);

                    if (itemstack6.getCount() > i2)
                    {
                        slot4.putStack(itemstack6.splitStack(i2));
                        slot4.onTake(player, itemstack10);

                        if (!inventoryplayer.addItemStackToInventory(itemstack10))
                        {
                            player.dropItem(itemstack10, true);
                        }
                    }
                    else
                    {
                        slot4.putStack(itemstack6);
                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
                        slot4.onTake(player, itemstack10);
                    }
                }
            }
        }
        else if (clickTypeIn == ClickType.CLONE && player.capabilities.isCreativeMode && inventoryplayer.getItemStack().isEmpty() && slotId >= 0)
        {
            Slot slot3 = this.inventorySlots.get(slotId);

            if (slot3 != null && slot3.getHasStack())
            {
                ItemStack itemstack5 = slot3.getStack().copy();
                itemstack5.setCount(itemstack5.getMaxStackSize());
                inventoryplayer.setItemStack(itemstack5);
            }
        }
        else if (clickTypeIn == ClickType.THROW && inventoryplayer.getItemStack().isEmpty() && slotId >= 0)
        {
            Slot slot2 = this.inventorySlots.get(slotId);

            if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(player))
            {
                ItemStack itemstack4 = slot2.decrStackSize(dragType == 0 ? 1 : slot2.getStack().getCount());
                slot2.onTake(player, itemstack4);
                player.dropItem(itemstack4, true);
            }
        }
        else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0)
        {
            Slot slot = this.inventorySlots.get(slotId);
            ItemStack dragStack = inventoryplayer.getItemStack();

            if (!dragStack.isEmpty() && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player)))
            {
                int i = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
                int j = dragType == 0 ? 1 : -1;

                for (int k = 0; k < 2; ++k)
                {
                    for (int l = i; l >= 0 && l < this.inventorySlots.size() && dragStack.getCount() < dragStack.getMaxStackSize(); l += j)
                    {
                        Slot slot1 = this.inventorySlots.get(l);

                        if (slot1.getHasStack() && canAddItemToSlot(slot1, dragStack, true) && slot1.canTakeStack(player) && this.canMergeSlot(dragStack, slot1))
                        {
                            ItemStack itemstack2 = slot1.getStack();

                            if (k != 0 || itemstack2.getCount() != itemstack2.getMaxStackSize())
                            {
                                int i1 = Math.min(dragStack.getMaxStackSize() - dragStack.getCount(), itemstack2.getCount());
                                ItemStack itemstack3 = slot1.decrStackSize(i1);
                                dragStack.grow(i1);

                                if (itemstack3.isEmpty())
                                {
                                    slot1.putStack(ItemStack.EMPTY);
                                }

                                slot1.onTake(player, itemstack3);
                            }
                        }
                    }
                }
            }

            this.detectAndSendChanges();
        }

        return itemstack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
    {
        boolean flag = false;
        int i = startIndex;

        if (reverseDirection)
        {
            i = endIndex - 1;
        }

        if (stack.isStackable())
        {
            while (!stack.isEmpty())
            {
                if (reverseDirection)
                {
                    if (i < startIndex)
                    {
                        break;
                    }
                }
                else if (i >= endIndex)
                {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();

                if (slot.isItemValid(stack) && canShiftPut(slot) && !itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack))
                {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

                    if (j <= maxSize)
                    {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.putStack(itemstack);
                        slot.onSlotChanged();
                        flag = true;
                    }
                    else if (itemstack.getCount() < maxSize)
                    {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.putStack(itemstack);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty())
        {
            if (reverseDirection)
            {
                i = endIndex - 1;
            }
            else
            {
                i = startIndex;
            }

            while (true)
            {
                if (reverseDirection)
                {
                    if (i < startIndex)
                    {
                        break;
                    }
                }
                else if (i >= endIndex)
                {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();

                if (canShiftPut(slot1) && itemstack1.isEmpty() && slot1.isItemValid(stack) && slot1.getItemStackLimit(stack) > 0)
                {
                    if (stack.getCount() > slot1.getItemStackLimit(stack))
                    {
                        slot1.putStack(stack.splitStack(slot1.getItemStackLimit(stack)));
                    }
                    else
                    {
                        slot1.putStack(stack.splitStack(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        return flag;
    }
}