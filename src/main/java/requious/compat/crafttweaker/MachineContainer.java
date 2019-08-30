package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenClass("mods.requious.MachineContainer")
public class MachineContainer {
    public TileEntity tile;
    public EnumFacing facing;
    public RandomCT random;

    public MachineContainer(TileEntity tile, EnumFacing facing) {
        this.tile = tile;
        this.facing = facing;
        this.random = new RandomCT();
    }

    @ZenGetter("world")
    public IWorld getWorld() {
        return CraftTweakerMC.getIWorld(tile.getWorld());
    }

    @ZenGetter("pos")
    public IBlockPos getPos() {
        return CraftTweakerMC.getIBlockPos(tile.getPos());
    }

    @ZenGetter("block")
    public IBlock getBlock() {
        BlockPos pos = tile.getPos();
        return CraftTweakerMC.getBlock(tile.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    @ZenGetter("state")
    public IBlockState getBlockState() {
        return CraftTweakerMC.getBlockState(tile.getWorld().getBlockState(tile.getPos()));
    }

    @ZenGetter("facing")
    public IFacing getFacing() {
        return CraftTweakerMC.getIFacing(facing);
    }

    @ZenGetter("random")
    public RandomCT getRandom() {
        return random;
    }
}
