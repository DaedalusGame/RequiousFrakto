package requious.gui.slot;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import requious.data.component.ComponentFluid;
import requious.gui.GuiAssembly;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FluidSlot extends BaseSlot<ComponentFluid.Slot> {
    public FluidSlot(ComponentFluid.Slot binding, int xPosition, int yPosition) {
        super(binding, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null);
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        return binding.getItem().getStack();
    }

    @Override
    public void putStack(ItemStack stack) {
        binding.getItem().setStack(stack);
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
        if(!binding.canPut())
            return binding.getItem().getAmount();
        return binding.getItem().getCapacity();
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if(!binding.canTake())
            return false;
        return !binding.getItem().extract(1, true).isEmpty();
    }

    @Override
    public void incrStack(int n) {
        binding.getItem().insert(n,false);
    }

    @Override
    public void renderBackground(GuiAssembly assembly, int x, int y, float partialTicks, int mousex, int mousey) {
        assembly.drawTexturedModalRect(x-1, y-1, 176+18, 0, 18, 18);
    }

    @Override
    public void renderForeground(GuiAssembly assembly,int x, int y, int mousex, int mousey) {
        SlotVisual visual = binding.getForeground();
        FluidStack contents = binding.getContents();
        if(contents != null) {
            assembly.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite textureSprite = assembly.mc.getTextureMapBlocks().getAtlasSprite(contents.getFluid().getStill(contents).toString());

            //assembly.drawTexturedModalRect(x,y+8, fluid, 16,8);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            int widthIn = 16;
            int heightIn = 16;

            int zLevel = 100;
            double fill = (double) contents.amount / binding.getCapacity();
            double v = MathHelper.clampedLerp(textureSprite.getMinV(),textureSprite.getMaxV(),fill);
            double heightLevel = MathHelper.clampedLerp(0,16, fill);

            double xCoord = x;
            double yCoord = y + heightIn - heightLevel;

            bufferbuilder.pos(xCoord + 0, yCoord + heightLevel, (double)zLevel).tex((double)textureSprite.getMinU(), v).endVertex();
            bufferbuilder.pos(xCoord + widthIn, yCoord + heightLevel, (double)zLevel).tex((double)textureSprite.getMaxU(), v).endVertex();
            bufferbuilder.pos(xCoord + widthIn, yCoord + 0, (double)zLevel).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMinV()).endVertex();
            bufferbuilder.pos(xCoord + 0, yCoord + 0, (double)zLevel).tex((double)textureSprite.getMinU(), (double)textureSprite.getMinV()).endVertex();
            tessellator.draw();
        }
        if(visual != null)
            visual.render(assembly.mc,x-1, y-1);
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return binding.getItem().extract(amount,false);
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
}
