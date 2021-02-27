package requious.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import requious.data.AssemblyProcessor;

import javax.annotation.Nullable;

public class MachineCommandSender implements ICommandSender {
    AssemblyProcessor assembly;

    public MachineCommandSender(AssemblyProcessor assembly) {
        this.assembly = assembly;
    }

    @Override
    public String getName() {
        return assembly.getCommandName();
    }

    @Override
    public boolean canUseCommand(int permLevel, String commandName) {
        return true; //Yes.
    }

    @Override
    public World getEntityWorld() {
        return assembly.getTile().getWorld();
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return assembly.getTile().getWorld().getMinecraftServer();
    }

    @Override
    public Vec3d getPositionVector() {
        TileEntity tile = assembly.getTile();
        BlockPos pos = tile.getPos();
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    public BlockPos getPosition() {
        return assembly.getTile().getPos();
    }
}
