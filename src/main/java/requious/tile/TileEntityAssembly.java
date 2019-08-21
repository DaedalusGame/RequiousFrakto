package requious.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import requious.block.BlockAssembly;
import requious.data.AssemblyData;
import requious.data.AssemblyProcessor;
import requious.recipe.AssemblyRecipe;
import requious.util.ILaserStorage;
import requious.util.Misc;

import javax.annotation.Nullable;
import java.util.Random;

public class TileEntityAssembly extends TileEntity implements ITickable, ILaserAcceptor {
    Random random = new Random();
    AssemblyProcessor processor;
    ResourceLocation block;
    boolean shouldSync;

    public void setBlock(BlockAssembly block) {
        this.block = block.getRegistryName();
    }

    public BlockAssembly getBlock() {
        return (BlockAssembly) Block.REGISTRY.getObject(block);
    }

    public AssemblyProcessor getProcessor() {
        return processor;
    }

    public AssemblyData getData() {
        BlockAssembly assembly = this.getBlock();
        return assembly.getData();
    }

    public static EnumFacing toLocalSide(EnumFacing facing, EnumFacing side) {
        switch (facing) {
            case DOWN:
                return side.getOpposite();
            case UP:
                return side;
            case SOUTH:
                return side.rotateAround(EnumFacing.Axis.X);
            case NORTH:
                return side.getOpposite().rotateAround(EnumFacing.Axis.X);
            case WEST:
                return side.rotateAround(EnumFacing.Axis.Z);
            case EAST:
                return side.getOpposite().rotateAround(EnumFacing.Axis.Z);
            default:
                return null;
        }
    }

    public static EnumFacing toGlobalSide(EnumFacing facing, EnumFacing side) {
        switch (facing) {
            case DOWN:
                return side.getOpposite();
            case UP:
                return side;
            case SOUTH:
                return side.getOpposite().rotateAround(EnumFacing.Axis.X);
            case NORTH:
                return side.rotateAround(EnumFacing.Axis.X);
            case WEST:
                return side.getOpposite().rotateAround(EnumFacing.Axis.Z);
            case EAST:
                return side.rotateAround(EnumFacing.Axis.Z);
            default:
                return null;
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        boolean hasCapability = false;
        if (processor != null)
            hasCapability = processor.hasCapability(capability, toLocalSide(getFacing(),facing));
        if (!hasCapability)
            hasCapability = super.hasCapability(capability, facing);
        return hasCapability;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        T instance = null;
        if (processor != null)
            instance = processor.getCapability(capability, toLocalSide(getFacing(),facing));
        if (instance == null)
            instance = super.getCapability(capability, facing);
        return instance;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public void breakBlock(World world, BlockPos pos) {
        processor.machineBroken(world,new Vec3d(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("assembly", block.toString());
        if (processor != null)
            processor.writeToNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        block = new ResourceLocation(tag.getString("assembly"));
        if (processor == null)
            initProcessor();
        processor.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        shouldSync = true;
    }

    @Override
    public void update() {
        if (processor == null)
            initProcessor();
        else {
            processor.update();
            if (processor.isDirty())
                markDirty();
        }
        if(shouldSync)
            Misc.syncTE(this, false);
    }

    private void initProcessor() {
        processor = getData().constructProcessor();
        processor.setTile(this);
    }

    public EnumFacing getFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        return state.getValue(BlockAssembly.facing);
    }

    @Override
    public ILaserStorage getLaserStorage(EnumFacing laserDirection) {
        if(processor == null) return null;
        return processor.getLaserAcceptor(toLocalSide(getFacing(),laserDirection.getOpposite()));
    }

    @Override
    public boolean isValid() {
        return !isInvalid();
    }

    @Override
    public BlockPos getPosition() {
        return getPos();
    }
}