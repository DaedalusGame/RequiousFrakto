package requious.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import requious.data.EmitterData;

import javax.annotation.Nullable;
import java.awt.*;

public abstract class BlockEmitter extends Block implements IDynamicModel {
    public static final PropertyDirection facing = PropertyDirection.create("facing");

    EmitterData data;

    public BlockEmitter(Material materialIn, EmitterData data) {
        super(materialIn);
        this.data = data;
    }

    public EmitterData getData() {
        return data;
    }

    @Override
    public BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(facing).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return getDefaultState().withProperty(facing, EnumFacing.getFront(meta));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        return getDefaultState().withProperty(facing, face);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        BlockPos attachPos = pos.offset(state.getValue(facing), -1);
        IBlockState attachState = world.getBlockState(attachPos);
        if (attachState.getBlock().isReplaceable(world, attachPos)){
            world.setBlockToAir(pos);
            this.dropBlockAsItem(world, pos, state, 0);
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        switch (state.getValue(facing)){
            case UP:
                return new AxisAlignedBB(0.25,0,0.25,0.75,0.5,0.75);
            case DOWN:
                return new AxisAlignedBB(0.25,0.5,0.25,0.75,1.0,0.75);
            case NORTH:
                return new AxisAlignedBB(0.25,0.25,0.5,0.75,0.75,1.0);
            case SOUTH:
                return new AxisAlignedBB(0.25,0.25,0,0.75,0.75,0.5);
            case WEST:
                return new AxisAlignedBB(0.5,0.25,0.25,1.0,0.75,0.75);
            case EAST:
                return new AxisAlignedBB(0.0,0.25,0.25,0.5,0.75,0.75);
        }
        return new AxisAlignedBB(0.25,0,0.25,0.75,0.5,0.75);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);

    @Override
    public ResourceLocation getRedirect() {
        return data.model;
    }

    @Override
    public Color getTint(int tintIndex) {
        if(tintIndex >= 0 && tintIndex < data.colors.length)
            return data.colors[tintIndex];
        else
            return Color.WHITE;
    }
}
