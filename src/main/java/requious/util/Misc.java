package requious.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.awt.*;
import java.util.List;

public class Misc {
    public static void syncTE(TileEntity tile, boolean broken) {
        World world = tile.getWorld();
        if (!tile.isInvalid() && !broken && world instanceof WorldServer) {
            SPacketUpdateTileEntity packet = tile.getUpdatePacket();
            if (packet != null) {
                PlayerChunkMap chunkMap = ((WorldServer) world).getPlayerChunkMap();
                int i = tile.getPos().getX() >> 4;
                int j = tile.getPos().getZ() >> 4;
                PlayerChunkMapEntry entry = chunkMap.getEntry(i, j);
                if (entry != null) {
                    entry.sendPacket(packet);
                }
            }
        }
    }

    public static AxisAlignedBB rotateAABB(AxisAlignedBB aabb, EnumFacing direction) {
        Vec3d pointA = new Vec3d(aabb.minX - 0.5, aabb.minY - 0.5, aabb.minZ - 0.5);
        Vec3d pointB = new Vec3d(aabb.maxX - 0.5, aabb.maxY - 0.5, aabb.maxZ - 0.5);
        return new AxisAlignedBB(0.5+xOffset(pointA, direction),
                0.5+yOffset(pointA, direction),
                0.5+zOffset(pointA, direction),
                0.5+xOffset(pointB, direction),
                0.5+yOffset(pointB, direction),
                0.5+zOffset(pointB, direction));
    }

    public static double xOffset(Vec3d offset, EnumFacing direction) {
        switch (direction) {
            case DOWN:
            case NORTH:
                return -offset.x;
            case WEST:
                return -offset.y;
            case EAST:
                return offset.y;
            case UP:
            case SOUTH:
            default:
                return offset.x;
        }
    }

    public static double yOffset(Vec3d offset, EnumFacing direction) {
        switch (direction) {
            case DOWN:
                return -offset.y;
            case NORTH:
                return offset.z;
            case SOUTH:
                return -offset.z;
            case WEST:
                return offset.x;
            case EAST:
                return -offset.x;
            case UP:
            default:
                return offset.y;
        }
    }

    public static double zOffset(Vec3d offset, EnumFacing direction) {
        switch (direction) {
            case DOWN:
            case WEST:
                return -offset.z;
            case NORTH:
                return -offset.y;
            case SOUTH:
                return offset.y;
            case UP:
            case EAST:
            default:
                return offset.z;
        }

    }

    public static BlockPos posOffset(BlockPos pos, Vec3i offset, EnumFacing direction) {
        int ox, oy, oz;

        switch (direction) {
            case DOWN:
                ox = -offset.getX();
                oy = -offset.getY();
                oz = -offset.getZ();
                break;

            case NORTH:
                ox = -offset.getX();
                oy = offset.getZ();
                oz = -offset.getY();
                break;
            case SOUTH:
                ox = offset.getX();
                oy = -offset.getZ();
                oz = offset.getY();
                break;
            case WEST:
                ox = -offset.getY();
                oy = offset.getX();
                oz = -offset.getZ();
                break;
            case EAST:
                ox = offset.getY();
                oy = -offset.getX();
                oz = offset.getZ();
                break;
            case UP:
            default:
                ox = offset.getX();
                oy = offset.getY();
                oz = offset.getZ();
                break;
        }

        return pos.add(ox, oy, oz);
    }

    public static String getCountString(ItemStack itemstack) {
        String s = null;
        int count = itemstack.getCount();
        if (count >= 1000000000)
            s = count / 1000000000 + "B";
        else if (count >= 100000000)
            s = "." + count / 100000000 + "B";
        else if (count >= 1000000)
            s = count / 1000000 + "M";
        else if (count >= 100000)
            s = "." + count / 100000 + "M";
        else if (count >= 1000)
            s = count / 1000 + "k";
        return s;
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        drawTexturedModalRect(x,y,100,textureX,textureY,width,height);
    }

    public static void drawTexturedModalRect(int x, int y, int z, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) z).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + height), (double) z).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) z).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) z).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }

    public static Color lerpColorRGB(List<Color> colors, double lerp) {
        if(colors.size() <= 1)
            throw new IllegalArgumentException("Needs atleast two colors.");
        if(lerp >= 1)
            return colors.get(colors.size()-1);
        int index = MathHelper.clamp((int)(lerp * (colors.size()-1)), 0, colors.size() - 2);
        return lerpColorRGB(colors.get(index),colors.get(index+1),lerp * (colors.size()-1) % 1);
    }

    public static Color lerpColorHSB(List<Color> colors, double lerp) {
        if(colors.size() <= 1)
            throw new IllegalArgumentException("Needs atleast two colors.");
        if(lerp >= 1)
            return colors.get(colors.size()-1);
        int expectedIndex = (int) (lerp * (colors.size() - 1));
        int index = MathHelper.clamp(expectedIndex, 0, colors.size() - 2);
        return lerpColorHSB(colors.get(index),colors.get(index+1),lerp * (colors.size()-1) % 1);
    }

    public static Color lerpColorRGB(Color colorA, Color colorB, double lerp) {
        int r = (int)MathHelper.clampedLerp(colorA.getRed(),colorB.getRed(),lerp);
        int g = (int)MathHelper.clampedLerp(colorA.getGreen(),colorB.getGreen(),lerp);
        int b = (int)MathHelper.clampedLerp(colorA.getBlue(),colorB.getBlue(),lerp);
        int a = (int)MathHelper.clampedLerp(colorA.getAlpha(),colorB.getAlpha(),lerp);
        return new Color(r,g,b,a);
    }

    public static Color lerpColorHSB(Color colorA, Color colorB, double lerp) {
        float[] hsbA = Color.RGBtoHSB(colorA.getRed(),colorA.getGreen(),colorA.getBlue(),null);
        float[] hsbB = Color.RGBtoHSB(colorB.getRed(),colorB.getGreen(),colorB.getBlue(),null);
        float h = (float)MathHelper.clampedLerp(hsbA[0],hsbB[0],lerp);
        float s = (float)MathHelper.clampedLerp(hsbA[1],hsbB[1],lerp);
        float b = (float)MathHelper.clampedLerp(hsbA[2],hsbB[2],lerp);
        int a = (int)MathHelper.clampedLerp(colorA.getAlpha(),colorB.getAlpha(),lerp);
        Color hsbColor = Color.getHSBColor(h, s, b);
        return new Color(hsbColor.getRed(),hsbColor.getGreen(),hsbColor.getBlue(),a);
    }

    public static Color parseColor(int[] rgb) {
        Color color = Color.WHITE;
        if (rgb != null && rgb.length >= 3 && rgb.length <= 4)
            color = new Color(rgb[0], rgb[1], rgb[2], rgb.length == 4 ? rgb[3] : 255);
        return color;
    }

    public static Vec3d getLocalVector(Vec3d vector, EnumFacing facing) {
        return new Vec3d(xOffset(vector, facing), yOffset(vector,facing), zOffset(vector,facing));
    }
}
