package requious.gui.slot;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentFluid;
import requious.gui.GuiAssembly;
import requious.network.PacketHandler;
import requious.network.message.MessageClickSlot;
import requious.util.Fill;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FluidSlot extends BaseSlot<ComponentFluid.Slot> {
    public FluidSlot(AssemblyProcessor assembly, ComponentFluid.Slot binding, int xPosition, int yPosition) {
        super(assembly, binding, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null);
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void putStack(ItemStack stack) {
        //binding.getItem().setStack(stack);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {

    }

    @Override
    public int getSlotStackLimit() {
        return binding.getCapacity();
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 0;
        //if(!binding.canPut())
        //    return binding.getItem().getAmount();
        //return binding.getItem().getCapacity();
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
        //if(!binding.canTake())
        //    return false;
        //return !binding.getItem().extract(1, true).isEmpty();
    }

    @Override
    public void incrStack(int n) {
        //binding.getItem().insert(n,false);
    }

    @Override
    public void renderBackground(GuiAssembly assembly, int x, int y, float partialTicks, int mousex, int mousey) {
        SlotVisual background = binding.getBackground();
        background.render(assembly.mc,x-1, y-1, 100, getFill());
        //assembly.drawTexturedModalRect(x-1, y-1, 176+18, 0, 18, 18);
    }

    private Fill getFill() {
        return new Fill(binding.getAmount(),binding.getCapacity());
    }

    @Override
    public void renderForeground(GuiAssembly assembly,int x, int y, int mousex, int mousey) {
        SlotVisual foreground = binding.getForeground();
        SlotVisual background = binding.getBackground();
        FluidStack contents = binding.getContents();
        if(contents != null) {
            assembly.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite textureSprite = assembly.mc.getTextureMapBlocks().getAtlasSprite(contents.getFluid().getStill(contents).toString());

            //assembly.drawTexturedModalRect(x,y+8, fluid, 16,8);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

            int widthSlot = background.getWidth();
            int heightSlot = background.getHeight();

            int widthIn = 18 * widthSlot - 2;
            int heightIn = 18 * heightSlot - 2;

            int zLevel = 100;
            double fill = (double) contents.amount / binding.getCapacity();
            double v = MathHelper.clampedLerp(textureSprite.getMinV(),textureSprite.getMaxV(),fill);
            double heightLevel = MathHelper.clampedLerp(0,heightIn, fill);

            double xCoord = x;
            double yCoord = y + heightIn - heightLevel;

            Color color = new Color(contents.getFluid().getColor(contents));
            GlStateManager.color(color.getRed() / 255f,color.getGreen() / 255f,color.getBlue() / 255f,color.getAlpha() / 255f);

            bufferbuilder.pos(xCoord + 0, yCoord + heightLevel, (double)zLevel).tex((double)textureSprite.getMinU(), v).endVertex();
            bufferbuilder.pos(xCoord + widthIn, yCoord + heightLevel, (double)zLevel).tex((double)textureSprite.getMaxU(), v).endVertex();
            bufferbuilder.pos(xCoord + widthIn, yCoord + 0, (double)zLevel).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMinV()).endVertex();
            bufferbuilder.pos(xCoord + 0, yCoord + 0, (double)zLevel).tex((double)textureSprite.getMinU(), (double)textureSprite.getMinV()).endVertex();
            tessellator.draw();
        }
        if(foreground != null)
            foreground.render(assembly.mc,x-1, y-1, 1000, getFill());
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return ItemStack.EMPTY;
        //return binding.getItem().extract(amount,false);
    }

    @Override
    public boolean hasToolTip() {
        return true;
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        FluidStack fluid = binding.getContents();
        int capacity = binding.getCapacity();

        if(fluid == null)
            tooltip.add(I18n.format("requious.fluid.empty"));
        else
            tooltip.add(I18n.format("requious.fluid",fluid.getLocalizedName(),fluid.amount,capacity));

        return tooltip;
    }

    @Override
    public boolean isEnabled() {
        return !binding.isHidden() && binding.isBucketAccepted();
    }

    @Override
    public boolean isHoverEnabled() {
        return true;
    }

    @Override
    public Vec3i getSize() {
        SlotVisual background = binding.getBackground();
        return new Vec3i(background.getWidth() * 18 - 2, background.getHeight() * 18 - 2, 0);
    }

    @Override
    public boolean canShiftPut() {
        return false;
    }

    @Override
    public boolean canShiftTake() {
        return false;
    }

    @Override
    public void clientClick(EntityPlayer player, ItemStack dragStack, int mouseButton, ClickType clickType) {
        PacketHandler.INSTANCE.sendToServer(new MessageClickSlot(slotNumber, dragStack, mouseButton, clickType));

        ItemStack actualStack = player.inventory.getItemStack();
        if (!actualStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
            return;
        if(clickType == ClickType.PICKUP) {
            handleFluidItem(player, actualStack);
        }
    }

    @Override
    public void serverClick(EntityPlayerMP player, ItemStack dragStack, int mouseButton, ClickType clickType) {
        ItemStack actualStack = player.inventory.getItemStack();
        if (!actualStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
            return;
        if(clickType == ClickType.PICKUP) {
            handleFluidItem(player, actualStack);
        }
    }

    private void handleFluidItem(EntityPlayer player, ItemStack actualStack) {
        SlotFluidHandler handler = new SlotFluidHandler();
        if(binding.canTake() && binding.getAmount() > 0) { //Empty
            FluidActionResult result = FluidUtil.tryFillContainer(actualStack, handler, Integer.MAX_VALUE, player, false);
            if(result.isSuccess()) {
                ItemStack resultStack = result.getResult();
                returnItem(player, resultStack);
                FluidUtil.tryFillContainer(actualStack, handler, Integer.MAX_VALUE, player, true);
                actualStack.shrink(1);
                player.inventory.setItemStack(actualStack);
                return;
            }
        }

        if(binding.canPut()) { //Fill
            FluidActionResult result = FluidUtil.tryEmptyContainer(actualStack, handler, Integer.MAX_VALUE, player, false);
            if(result.isSuccess()) {
                ItemStack resultStack = result.getResult();
                returnItem(player, resultStack);
                FluidUtil.tryEmptyContainer(actualStack, handler, Integer.MAX_VALUE, player, true);
                actualStack.shrink(1);
                player.inventory.setItemStack(actualStack);
                return;
            }
        }
    }

    private void returnItem(EntityPlayer player, ItemStack resultStack) {
        boolean added = player.inventory.addItemStackToInventory(resultStack);
        if(!added) {
            player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY + (double)(player.height / 2.0F), player.posZ, resultStack));
        }
    }

    public class SlotFluidHandler implements IFluidHandler {
        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[] { binding };
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return binding.fill(resource, !doFill);
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return binding.drain(resource, !doDrain);
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return binding.drain(maxDrain, !doDrain);
        }
    }
}
