package requious.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import requious.gui.ContainerAssembly;
import requious.gui.slot.BaseSlot;
import requious.gui.slot.SelectSlot;

public class MessageSelectSlot implements IMessage {
    int slot;
    boolean selected;

    public MessageSelectSlot() {
    }

    public MessageSelectSlot(int slot, boolean selected) {
        this.slot = slot;
        this.selected = selected;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        selected = buffer.readBoolean();
        slot = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBoolean(selected);
        buffer.writeInt(slot);
    }

    public static class MessageHolder implements IMessageHandler<MessageSelectSlot, IMessage> {
        @Override
        public IMessage onMessage(final MessageSelectSlot message, final MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                Container container = player.openContainer;
                if(container instanceof ContainerAssembly) {
                    Slot slot = container.getSlot(message.slot);
                    if(slot instanceof SelectSlot)
                        ((SelectSlot)slot).setSelected(message.selected);
                }
            });
            return null;
        }
    }
}