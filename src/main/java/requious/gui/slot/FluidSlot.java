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
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import requious.data.AssemblyProcessor;
import requious.data.component.ComponentFluid;
import requious.gui.GuiAssembly;
import requious.util.Fill;
import requious.util.SlotVisual;

import javax.annotation.Nonnull;
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

    @Override
    public boolean isEnabled() {
        return binding.isBucketAccepted() && (binding.canPut() || binding.canTake());
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
        return super.canShiftPut() && binding.canPut();
    }

    @Override
    public boolean canShiftTake() {
        return super.canShiftTake() && binding.canTake();
    }
}
