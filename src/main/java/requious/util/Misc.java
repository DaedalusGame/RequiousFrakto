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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class Misc {
    public static void syncTE(TileEntity tile, boolean broken) {
        World world = tile.getWorld();
        if(!tile.isInvalid() && !broken && world instanceof WorldServer) {
            SPacketUpdateTileEntity packet = tile.getUpdatePacket();
            if (packet != null) {
                PlayerChunkMap chunkMap = ((WorldServer) world).getPlayerChunkMap();
                int i = tile.getPos().getX() >> 4;
                int j = tile.getPos().getZ() >> 4;
                PlayerChunkMapEntry entry = chunkMap.getEntry(i, j);
                if(entry != null) {
                    entry.sendPacket(packet);
                }
            }
        }
    }

    public static BlockPos posOffset(BlockPos pos, Vec3i offset, EnumFacing direction) {
        int ox, oy,oz;

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

        return pos.add(ox,oy,oz);
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

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        int zlevel = 100;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)zlevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double)zlevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)zlevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)zlevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }
}
