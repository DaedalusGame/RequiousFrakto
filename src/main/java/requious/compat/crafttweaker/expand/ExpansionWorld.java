package requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockAccess;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IVector3d;
import crafttweaker.api.world.IWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import requious.compat.crafttweaker.MachineContainer;
import requious.data.AssemblyProcessor;
import requious.tile.TileEntityAssembly;
import requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenExpansion("crafttweaker.world.IWorld")
@ZenRegister
public class ExpansionWorld {
    @ZenMethod
    public MachineContainer getMachine(IWorld iworld, IBlockPos ipos) {
        World world = CraftTweakerMC.getWorld(iworld);
        BlockPos pos = CraftTweakerMC.getBlockPos(ipos);

        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileEntityAssembly) {
            AssemblyProcessor processor = ((TileEntityAssembly) tile).getProcessor();
            if(processor != null)
                return new MachineContainer(processor);
        }

        return null;
    }
}
