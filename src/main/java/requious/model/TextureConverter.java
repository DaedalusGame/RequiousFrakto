package requious.model;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector4f;
import java.util.List;
import java.util.Optional;

public class TextureConverter {
    public static List<BakedQuad> getQuadsForSprite(int tint, int colorMod, TextureAtlasSprite template, TextureAtlasSprite sprite, VertexFormat format, TRSRTransformation transform)
    {
        List<BakedQuad> bakedQuads = Lists.newArrayList();

        int uSpriteMax = sprite.getIconWidth();
        int vSpriteMax = sprite.getIconHeight();

        int uMaskMax = template.getIconWidth();
        int vMaskMax = template.getIconHeight();

        int uMax = Math.max(uMaskMax,uSpriteMax);
        int vMax = Math.max(vMaskMax,vSpriteMax);

        float un = uSpriteMax / 16f;
        float vn = vSpriteMax / 16f;

        for(int f = 0; f < sprite.getFrameCount(); f++)
        {
            int[] spritepixels = sprite.getFrameTextureData(f)[0];
            int[] maskpixels = template.getFrameTextureData(0)[0];
            for(int v = 0; v < vMax; v++)
            {
                for(int u = 0; u < uMax; u++)
                {
                    int spriteindex = mapToSmallerPixel(u,v,uMax,vMax,uSpriteMax,vSpriteMax);
                    int maskindex = mapToSmallerPixel(u,v,uMax,vMax,uMaskMax,vMaskMax);

                    if(!isTransparent(spritepixels,maskpixels,spriteindex,maskindex))
                    {
                        int color = multiplyColors(colorMod,maskpixels[maskindex]);
                        float x1 = u / (float)uMax;
                        float x2 = (u + 1) / (float)uMax;
                        float y1 = (vMax - 1 - v) / (float)vMax;
                        float y2 = (vMax - v) / (float)vMax;

                        float u1 = sprite.getInterpolatedU(u / un);//sprite.getMinU() + u * un;
                        float u2 = sprite.getInterpolatedU((u+1) / un);//sprite.getMinU() + (u+1) * un;
                        float v1 = sprite.getInterpolatedV(v / vn);//sprite.getMinV() + v * vn;
                        float v2 = sprite.getInterpolatedV((v+1) / vn);//sprite.getMinV() + (v+1) * vn;

                        //front
                        bakedQuads.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint, color,
                                x1, y1, 7.5f / 16f, u1, v2,
                                x1, y2, 7.5f / 16f, u1, v1,
                                x2, y2, 7.5f / 16f, u2, v1,
                                x2, y1, 7.5f / 16f, u2, v2
                        ));
                        // back
                        bakedQuads.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint, color,
                                x1, y1, 8.5f / 16f, u1, v2,
                                x2, y1, 8.5f / 16f, u2, v2,
                                x2, y2, 8.5f / 16f, u2, v1,
                                x1, y2, 8.5f / 16f, u1, v1
                        ));
                        // north
                        bakedQuads.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint, color,
                                x2, y1, 7.5f / 16f, u2, v2,
                                x2, y1, 8.5f / 16f, u2, v1,
                                x1, y1, 8.5f / 16f, u1, v1,
                                x1, y1, 7.5f / 16f, u1, v2
                        ));
                        // south
                        bakedQuads.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint, color,
                                x1, y2, 7.5f / 16f, u1, v2,
                                x1, y2, 8.5f / 16f, u1, v1,
                                x2, y2, 8.5f / 16f, u2, v1,
                                x2, y2, 7.5f / 16f, u2, v2
                        ));
                        bakedQuads.add(buildQuad(format, transform, EnumFacing.EAST, sprite, tint, color,
                                x2, y1, 7.5f / 16f, u1, v2,
                                x2, y2, 7.5f / 16f, u1, v1,
                                x2, y2, 8.5f / 16f, u2, v1,
                                x2, y1, 8.5f / 16f, u2, v2
                        ));
                        bakedQuads.add(buildQuad(format, transform, EnumFacing.WEST, sprite, tint, color,
                                x1, y1, 8.5f / 16f, u2, v2,
                                x1, y2, 8.5f / 16f, u2, v1,
                                x1, y2, 7.5f / 16f, u1, v1,
                                x1, y1, 7.5f / 16f, u1, v2
                        ));
                    }
                }
            }
        }

        return bakedQuads;
    }

    private static int multiplyColors(int color1, int color2)
    {
        //associativity of multiplication
        int r = ((color1 & 0xFF) * ((color2 & 0xFF))) / 0xFF;
        int g = (((color1 >> 8) & 0xFF) * ((color2 >> 8) & 0xFF)) / 0xFF;
        int b = (((color1 >> 16) & 0xFF) * ((color2 >> 16) & 0xFF)) / 0xFF;
        int a = (((color1 >> 24) & 0xFF) * ((color2 >> 24) & 0xFF)) / 0xFF;

        return r | (g << 8) | (b << 16) | (a << 24);
    }

    private static int getPixelIndex(int u, int v, int uMax, int vMax)
    {
        //return u + (vMax - 1 - v) * uMax;
        return u + v * vMax;
    }

    //uMax must be multiple of uMin
    //vMax must be multiple of vMin
    private static int mapToSmallerPixel(int u, int v, int uMax, int vMax, int uMin, int vMin)
    {
        int uMod = uMax / uMin;
        int vMod = vMax / vMin;

        return getPixelIndex((u / uMod) % uMin, (v / vMod) % uMin, uMin, vMin);
    }

    private static boolean isTransparent(int[] pixels, int[] maskpixels, int index, int maskindex)
    {
        return (pixels[index] >> 24 & 0xFF) == 0 || (maskpixels[maskindex] >> 24 & 0xFF) == 0;
    }

    private static final BakedQuad buildQuad(
            VertexFormat format, TRSRTransformation transform, EnumFacing side, TextureAtlasSprite sprite, int tint, int color,
            float x0, float y0, float z0, float u0, float v0,
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3)
    {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadTint(tint);
        builder.setQuadOrientation(side);
        builder.setTexture(sprite);
        putVertex(builder, format, transform, side, color, x0, y0, z0, u0, v0);
        putVertex(builder, format, transform, side, color, x1, y1, z1, u1, v1);
        putVertex(builder, format, transform, side, color, x2, y2, z2, u2, v2);
        putVertex(builder, format, transform, side, color, x3, y3, z3, u3, v3);
        return builder.build();
    }

    private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, TRSRTransformation transform, EnumFacing side, int color, float x, float y, float z, float u, float v)
    {
        Vector4f vec = new Vector4f();
        for(int e = 0; e < format.getElementCount(); e++)
        {
            switch(format.getElement(e).getUsage())
            {
                case POSITION:
                        vec.x = x;
                        vec.y = y;
                        vec.z = z;
                        vec.w = 1;
                        transform.getMatrix().transform(vec);
                        builder.put(e, vec.x, vec.y, vec.z, vec.w);
                    break;
                case COLOR:
                    float r = ((color >> 16) & 0xFF) / 255f; // red
                    float g = ((color >> 8) & 0xFF) / 255f; // green
                    float b = ((color >> 0) & 0xFF) / 255f; // blue
                    float a = ((color >> 24) & 0xFF) / 255f; // alpha
                    builder.put(e, r, g, b, a);
                    break;
                case UV: if(format.getElement(e).getIndex() == 0)
                {
                    builder.put(e, u, v, 0f, 1f);
                    break;
                }
                case NORMAL:
                    builder.put(e, (float)side.getFrontOffsetX(), (float)side.getFrontOffsetY(), (float)side.getFrontOffsetZ(), 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }
}
