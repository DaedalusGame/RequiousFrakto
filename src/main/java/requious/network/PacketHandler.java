package requious.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import requious.Requious;
import requious.network.message.MessageClickSlot;
import requious.network.message.MessageScrollSlot;
import requious.network.message.MessageSelectSlot;

public class PacketHandler {
    public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Requious.MODID);

    private static int id = 0;

    public static void registerMessages() {
        INSTANCE.registerMessage(MessageSelectSlot.MessageHolder.class, MessageSelectSlot.class, id++, Side.SERVER);
        INSTANCE.registerMessage(MessageScrollSlot.MessageHolder.class, MessageScrollSlot.class, id++, Side.SERVER);
        INSTANCE.registerMessage(MessageClickSlot.MessageHolder.class, MessageClickSlot.class, id++, Side.SERVER);
    }
}
