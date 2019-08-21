package requious.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import requious.tile.ITargetable;

import java.util.List;

public class ItemTuningFork extends Item {
    public ItemTuningFork() {
        this.setMaxStackSize(1);
    }

    private NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound == null) {
            tagCompound = new NBTTagCompound();
            stack.setTagCompound(tagCompound);
        }
        return tagCompound;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ){
        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound tagCompound = getOrCreateTagCompound(stack);
        if (player.isSneaking()){
            tagCompound.setInteger("targetWorld", world.provider.getDimension());
            tagCompound.setInteger("targetX", pos.getX());
            tagCompound.setInteger("targetY", pos.getY());
            tagCompound.setInteger("targetZ", pos.getZ());
            world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 1.0f, 1.9f+itemRand.nextFloat()*0.2f, false);
            return EnumActionResult.SUCCESS;
        } else if (tagCompound.hasKey("targetX")) {
            boolean success = targetBlock(world, pos, face, stack);
            if (success)
                return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    public boolean targetBlock(World world, BlockPos pos, EnumFacing face, ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        int dimension = tagCompound.getInteger("targetWorld");
        if (world.provider.getDimension() != dimension)
            return false;
        TileEntity tile = world.getTileEntity(pos);
        int targetDimension = tagCompound.getInteger("targetWorld");
        World targetWorld = DimensionManager.getWorld(targetDimension);
        BlockPos targetPos = new BlockPos(tagCompound.getInteger("targetX"), tagCompound.getInteger("targetY"), tagCompound.getInteger("targetZ"));
        if (tile instanceof ITargetable) {
            ITargetable targetable = (ITargetable) tile;
            targetable.setTarget(targetWorld,targetPos,face);
            return true;
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced){
        if (stack.hasTagCompound()){
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound.hasKey("targetX")){
                int dimension = tagCompound.getInteger("targetWorld");
                if(world.provider.getDimension() == dimension) {
                    BlockPos pos = new BlockPos(tagCompound.getInteger("targetX"), tagCompound.getInteger("targetY"), tagCompound.getInteger("targetZ"));
                    IBlockState blockState = world.getBlockState(pos);
                    tooltip.add(I18n.format("embers.tooltip.targetingBlock",blockState.getBlock().getLocalizedName()));
                    tooltip.add(" X=" + pos.getX());
                    tooltip.add(" Y=" + pos.getY());
                    tooltip.add(" Z=" + pos.getZ());
                }
            }
        }
    }
}
