package requious.tile;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import requious.util.ILaserStorage;

public interface ILaserAcceptor {
    ILaserStorage getLaserStorage(EnumFacing laserDirection);

    boolean isValid();

    BlockPos getPosition();
}
