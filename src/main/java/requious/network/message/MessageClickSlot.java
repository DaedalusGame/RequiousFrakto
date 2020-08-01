package requious.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import requious.gui.ContainerAssembly;
import requious.gui.slot.BaseSlot;

import java.io.IOException;

public class MessageClickSlot implements IMessage {
    int slot;
    ItemStack dragStack;
    int mouseButton;
    ClickType clickType;

    public MessageClickSlot() {
    }

    public MessageClickSlot(int slot, ItemStack dragStack, int mouseButton, ClickType clickType) {
        this.slot = slot;
        this.dragStack = dragStack;
        this.mouseButton = mouseButton;
        this.clickType = clickType;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        slot = buffer.readInt();
        try {
            dragStack = buffer.readItemStack();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mouseButton = buffer.readInt();
        clickType = readClickType(buffer.readInt());
    }

    private ClickType readClickType(int i) {
        ClickType[] values = ClickType.values();
        if(i < 0 || i >= values.length)
            return ClickType.PICKUP;
        return values[i];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeInt(slot);
        buffer.writeItemStack(dragStack);
        buffer.writeInt(mouseButton);
        buffer.writeInt(writeClickType(clickType));
    }

    private int writeClickType(ClickType clickType) {
        if(clickType == null)
            return 0;
        return clickType.ordinal();
    }

    public static class MessageHolder implements IMessageHandler<MessageClickSlot, IMessage> {
        @Override
        public IMessage onMessage(final MessageClickSlot message, final MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                Container container = player.openContainer;
                if(container instanceof ContainerAssembly) {
                    Slot slot = container.getSlot(message.slot);
                    if(slot instanceof BaseSlot)
                        ((BaseSlot) slot).serverClick(player, message.dragStack, message.mouseButton, message.clickType);
                }
            });
            return null;
        }
    }
}