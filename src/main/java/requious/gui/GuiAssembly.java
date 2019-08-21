package requious.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;
import requious.Requious;
import requious.data.AssemblyProcessor;
import requious.gui.slot.BaseSlot;
import requious.util.Misc;

import java.io.IOException;
import java.util.List;

public class GuiAssembly extends GuiContainer {
    private static final ResourceLocation boxGuiLocation = new ResourceLocation(Requious.MODID, "textures/gui/assembly.png");
    private AssemblyProcessor assembly;

    public GuiAssembly(EntityPlayer player, AssemblyProcessor assembly) {
        super(new ContainerAssembly(player, assembly));
        this.ySize = 184;
        this.assembly = assembly;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        if (slot instanceof BaseSlot && ((BaseSlot) slot).hasToolTip()) {
            List<String> tooltip = ((BaseSlot) slot).getTooltip();
            FontRenderer font = fontRenderer;
            this.drawHoveringText(tooltip, mouseX, mouseY, font);
        } else
            super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        List<String> tooltip = this.getItemToolTip(stack);
        if (tooltip.size() >= 1)
            tooltip.set(0, tooltip.get(0) + TextFormatting.GRAY + " x" + stack.getCount());
        this.drawHoveringText(tooltip, x, y, (font == null ? fontRenderer : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

    @Override
    public void drawSlot(Slot slotIn) {
        int i = slotIn.xPos;
        int j = slotIn.yPos;
        ItemStack itemstack = slotIn.getStack();
        boolean flag = false;
        boolean flag1 = slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
        ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
        String s = null;

        if (slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() / 2);
        } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty()) {
            if (this.dragSplittingSlots.size() == 1) {
                return;
            }

            if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slotIn)) {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slotIn.getItemStackLimit(itemstack));

                if (itemstack.getCount() > k) {
                    s = TextFormatting.YELLOW.toString() + k;
                    itemstack.setCount(k);
                }
            } else {
                this.dragSplittingSlots.remove(slotIn);
                this.updateDragSplitting();
            }
        }

        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;

        if (itemstack.isEmpty() && slotIn.isEnabled()) {
            TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();

            if (textureatlassprite != null) {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
                this.drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
                GlStateManager.enableLighting();
                flag1 = true;
            }
        }

        if (!flag1) {
            if (flag) {
                drawRect(i, j, i + 16, j + 16, -2130706433);
            }

            if(s == null)
                s = Misc.getCountString(itemstack);

            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, itemstack, i, j);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, i, j, s);
        }

        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mousex, int mousey) {
        //fontRenderer.drawString(I18n.format("tile.chute.name", new Object[0]), 8, 6, 4210752);

        for (Slot slot : inventorySlots.inventorySlots) {
            if (slot instanceof BaseSlot && ((BaseSlot) slot).shouldRender()) {
                ((BaseSlot) slot).renderForeground(this, slot.xPos, slot.yPos, mousex, mousey);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mousex, int mousey) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(boxGuiLocation);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);

        for (Slot slot : inventorySlots.inventorySlots) {
            this.mc.getTextureManager().bindTexture(boxGuiLocation);
            if (slot instanceof BaseSlot && ((BaseSlot) slot).shouldRender()) {
                ((BaseSlot) slot).renderBackground(this, xPos + slot.xPos, yPos + slot.yPos, partialTicks, mousex, mousey);
            }
        }

        /*for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 5; y++) {
                ComponentBase.Slot slot = assembly.getSlot(x, y);
                if (slot != null)
                    drawTexturedModalRect(xPos + 7 + x * 18, yPos + 6 + y * 18, 176, 0, 18, 18);
            }
        }*/
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);

        if (slotIn instanceof BaseSlot) {
            ((BaseSlot) slotIn).click(draggedStack, mouseButton, type);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            Slot slot = this.getSlotAtPosition(mouseX, mouseY);
            if (slot instanceof BaseSlot)
                ((BaseSlot) slot).clientScroll((int) Math.signum(wheel));
        }
    }
}
