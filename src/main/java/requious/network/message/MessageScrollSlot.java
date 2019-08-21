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

public class MessageScrollSlot implements IMessage {
    int slot;
    int scroll;

    public MessageScrollSlot() {
    }

    public MessageScrollSlot(int slot, int scroll) {
        this.slot = slot;
        this.scroll = scroll;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        slot = buffer.readInt();
        scroll = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeInt(slot);
        buffer.writeInt(scroll);
    }

    public static class MessageHolder implements IMessageHandler<MessageScrollSlot, IMessage> {
        @Override
        public IMessage onMessage(final MessageScrollSlot message, final MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                Container container = player.openContainer;
                if(container instanceof ContainerAssembly) {
                    Slot slot = container.getSlot(message.slot);
                    if(slot instanceof BaseSlot)
                        ((BaseSlot)slot).serverScroll(message.scroll);
                }
            });
            return null;
        }
    }
}