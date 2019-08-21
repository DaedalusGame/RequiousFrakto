package requious.tile;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITargetable {
    void setTarget(World world, BlockPos pos, EnumFacing facing);
}
