package requious.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import requious.tile.TileEntityAssembly;

public class GuiHandler  implements IGuiHandler {
    public static final int ASSEMBLY = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case ASSEMBLY:
                TileEntityAssembly assembly = (TileEntityAssembly) world.getTileEntity(new BlockPos(x, y, z));
                return new ContainerAssembly(player, assembly.getProcessor());
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case ASSEMBLY:
                TileEntityAssembly assembly = (TileEntityAssembly) world.getTileEntity(new BlockPos(x, y, z));
                return new GuiAssembly(player, assembly.getProcessor());
            default:
                return null;
        }
    }
}
