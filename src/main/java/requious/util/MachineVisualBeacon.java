package requious.util;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import requious.data.AssemblyProcessor;
import requious.tile.TileEntityAssembly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MachineVisualBeacon extends MachineVisual {
    public static class BeamSegment {
        private final float[] colors;
        private int height;

        public BeamSegment(float[] colorsIn)
        {
            this.colors = colorsIn;
            this.height = 1;
        }

        protected void incrementHeight()
        {
            ++this.height;
        }

        public float[] getColors()
        {
            return this.colors;
        }

        @SideOnly(Side.CLIENT)
        public int getHeight()
        {
            return this.height;
        }
    }

    Parameter variableFacing;
    Parameter variableLength;
    boolean cancelOnHit;
    boolean global;

    public MachineVisualBeacon(Parameter variableActive, Parameter variableFacing, Parameter variableLength, boolean cancelOnHit, boolean global) {
        super(variableActive);
        this.variableFacing = variableFacing;
        this.variableLength = variableLength;
        this.cancelOnHit = cancelOnHit;
        this.global = global;
    }

    @Override
    public void render(TileEntityAssembly tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        if(!isActive(tile.getProcessor()))
            return;
        this.renderBeacon(tile, x, y, z, partialTicks);
    }

    @SideOnly(Side.CLIENT)
    private List<BeamSegment> getSegments(World world, BlockPos pos, EnumFacing facing, int length) {
        List<BeamSegment> segments = new ArrayList<>();
        BeamSegment segment = new BeamSegment(EnumDyeColor.WHITE.getColorComponentValues());
        segments.add(segment);
        boolean flag = true;
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int i1 = 1; i1 < length; ++i1)
        {
            IBlockState state = world.getBlockState(pos.offset(facing,i1));
            float[] color;

            if (state.getBlock() == Blocks.STAINED_GLASS)
            {
                color = state.getValue(BlockStainedGlass.COLOR).getColorComponentValues();
            }
            else
            {
                if (state.getBlock() != Blocks.STAINED_GLASS_PANE)
                {
                    if (state.getLightOpacity(world, checkPos) >= 15 && state.getBlock() != Blocks.BEDROCK)
                    {
                        if(cancelOnHit)
                            segments.clear();
                        break;
                    }
                    float[] customColor = state.getBlock().getBeaconColorMultiplier(state, world, checkPos, pos);
                    if (customColor != null)
                        color = customColor;
                    else {
                        segment.incrementHeight();
                        continue;
                    }
                }
                else
                    color = state.getValue(BlockStainedGlassPane.COLOR).getColorComponentValues();
            }

            float[] segmentColor = segment.getColors();
            if (!flag)
            {
                color = new float[] {(segmentColor[0] + color[0]) / 2.0F, (segmentColor[1] + color[1]) / 2.0F, (segmentColor[2] + color[2]) / 2.0F};
            }

            if (Arrays.equals(color, segmentColor))
            {
                segment.incrementHeight();
            }
            else
            {
                segment = new BeamSegment(color);
                segments.add(segment);
            }

            flag = false;
        }

        return segments;
    }

    public void renderBeacon(TileEntityAssembly tile, double x, double y, double z, float partialTicks) {
        GlStateManager.alphaFunc(516, 0.1F);
        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        manager.bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);

        AssemblyProcessor assembly = tile.getProcessor();

        EnumFacing facing = variableFacing.getFacing(assembly);

        double textureScale = 1;
        long totalWorldTime = tile.getWorld().getTotalWorldTime();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        switch(facing) {
            case DOWN:
                GlStateManager.rotate(180, 1, 0, 0);
                break;
            case UP:
                //NOOP
                break;
            case NORTH:
                GlStateManager.rotate(-90, 1, 0, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(90, 1, 0, 0);
                break;
            case WEST:
                GlStateManager.rotate(90, 0, 0, 1);
                break;
            case EAST:
                GlStateManager.rotate(-90, 0, 0, 1);
                break;
        }
        GlStateManager.translate(-0.5, -0.5, -0.5);

        if (textureScale > 0.0D) {
            GlStateManager.disableFog();
            int i = 0;

            List<BeamSegment> segments = getSegments(tile.getWorld(),tile.getPos(), facing, variableLength.getInteger(assembly, partialTicks));

            for (int j = 0; j < segments.size(); ++j) {
                BeamSegment segment = segments.get(j);
                TileEntityBeaconRenderer.renderBeamSegment(0, 0, 0, partialTicks, textureScale, totalWorldTime, i, segment.getHeight(), segment.getColors());
                i += segment.getHeight();
            }

            GlStateManager.enableFog();
        }

        GlStateManager.popMatrix();
    }
}
