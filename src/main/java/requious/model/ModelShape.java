package requious.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import requious.Requious;
import requious.item.Shape;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class ModelShape implements IModel {
    public static final ResourceLocation TOP_LEFT = new ResourceLocation(Requious.MODID, "items/shape_topleft");
    public static final ResourceLocation TOP_RIGHT = new ResourceLocation(Requious.MODID, "items/shape_topright");
    public static final ResourceLocation BOTTOM_LEFT = new ResourceLocation(Requious.MODID, "items/shape_bottomleft");
    public static final ResourceLocation BOTTOM_RIGHT = new ResourceLocation(Requious.MODID, "items/shape_bottomright");

    public static ModelShape MODEL = new ModelShape();

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableList.of(
                new ResourceLocation(Requious.MODID, "items/shape_topleft"),
                new ResourceLocation(Requious.MODID, "items/shape_topright"),
                new ResourceLocation(Requious.MODID, "items/shape_bottomleft"),
                new ResourceLocation(Requious.MODID, "items/shape_bottomright"));
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakingModel(ImmutableList.of(), bakedTextureGetter.apply(new ResourceLocation("missingno")), state, format);
    }

    public static class Loader implements ICustomModelLoader {
        ResourceLocation resourceLocation;

        public Loader(ResourceLocation resourceLocation) {
            this.resourceLocation = resourceLocation;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {

        }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getResourceDomain().equals(resourceLocation.getResourceDomain()) && modelLocation.getResourcePath().contains(resourceLocation.getResourcePath());
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            return MODEL;
        }
    }

    public static class BakingModel extends BakedItemModel {
        private final Map<Shape, IBakedModel> cache = new HashMap<>(); // contains all the baked models since they'll never change
        private final IModelState state;
        private final VertexFormat format;

        private static final float NORTH_Z = 7.498f / 16f;
        private static final float SOUTH_Z = 8.502f / 16f;

        BakingModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, IModelState state, VertexFormat format)
        {
            super(quads, particle, Maps.immutableEnumMap(PerspectiveMapWrapper.getTransforms(state)), OverrideList.INSTANCE, state.apply(Optional.empty()).orElse(TRSRTransformation.identity()).isIdentity());
            this.state = state;
            this.format = format;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
        {
            return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
        }

        public IBakedModel bake(Shape shape, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

            bakeShape(shape, builder, bakedTextureGetter, 0);

            return new ModelShapeBaked(builder.build(),state);
        }

        private void bakeShape(Shape shape, ImmutableList.Builder<BakedQuad> builder, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, int scale) {
            if(shape == null)
                return;

            TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());

            transform = scale(transform, scale);

            bakePart(shape.getPart(Shape.Piece.TOP_LEFT), bakedTextureGetter.apply(TOP_LEFT), builder, bakedTextureGetter,transform);
            bakePart(shape.getPart(Shape.Piece.TOP_RIGHT), bakedTextureGetter.apply(TOP_RIGHT), builder, bakedTextureGetter,transform);
            bakePart(shape.getPart(Shape.Piece.BOTTOM_LEFT), bakedTextureGetter.apply(BOTTOM_LEFT), builder, bakedTextureGetter,transform);
            bakePart(shape.getPart(Shape.Piece.BOTTOM_RIGHT), bakedTextureGetter.apply(BOTTOM_RIGHT), builder, bakedTextureGetter,transform);

            bakeShape(shape.getInner(), builder, bakedTextureGetter, scale + 1);
        }

        private void bakePart(Shape.Part part, TextureAtlasSprite mask, ImmutableList.Builder<BakedQuad> builder, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, TRSRTransformation transform) {
            if(part == null)
                return;

            TextureAtlasSprite sprite = bakedTextureGetter.apply(part.getTexture());
            builder.addAll(TextureConverter.getQuadsForSprite(1, part.getColor().getRGB(), mask, sprite, format, transform));
        }

        private TRSRTransformation scale(TRSRTransformation transformation, int scaleMod) {
            Matrix4f matrix = transformation.getMatrix();

            float r = 0.66f;
            float scale = (float) Math.pow(r, scaleMod);
            float offset = (1 - scale);

            TRSRTransformation multTransform = new TRSRTransformation(new Vector3f(offset / 2, offset / 2, -0.005f * scaleMod), null, new Vector3f(scale,scale,1.0f + 0.01f * scaleMod), null);

            matrix.mul(multTransform.getMatrix());

            return new TRSRTransformation(matrix);
            //return new TRSRTransformation(new Vector3f(translation.x * mult.x,translation.y * mult.y,translation.z * mult.z), rotLeft, new Vector3f(scale.x * mult.x,scale.y * mult.y,scale.z * mult.z), rotRight);
        }
    }

    public static class OverrideList extends ItemOverrideList {
        public static final OverrideList INSTANCE = new OverrideList();

        public OverrideList() {
            super(Collections.emptyList());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            BakingModel model = (BakingModel)originalModel;

            //model.cache.clear();

            ResourceLocation circle = new ResourceLocation(Requious.MODID,"items/shape_circle");
            ResourceLocation square = new ResourceLocation(Requious.MODID,"items/shape_square");
            ResourceLocation star = new ResourceLocation(Requious.MODID,"items/shape_star");

            Shape shapeInnerInner = new Shape();
            shapeInnerInner.setPart(Shape.Piece.BOTTOM_LEFT, new Shape.Part(circle, "iron", Color.YELLOW));
            shapeInnerInner.setPart(Shape.Piece.BOTTOM_RIGHT, new Shape.Part(circle, "iron", Color.YELLOW));
            shapeInnerInner.setPart(Shape.Piece.TOP_RIGHT, new Shape.Part(circle, "iron", Color.YELLOW));
            shapeInnerInner.setPart(Shape.Piece.TOP_LEFT, new Shape.Part(circle, "iron", Color.YELLOW));

            Shape shapeInner = new Shape();
            shapeInner.setPart(Shape.Piece.BOTTOM_LEFT, new Shape.Part(star, "iron", Color.RED));
            shapeInner.setPart(Shape.Piece.BOTTOM_RIGHT, new Shape.Part(star, "iron", Color.RED));
            shapeInner.setPart(Shape.Piece.TOP_RIGHT, new Shape.Part(star, "iron", Color.RED));
            shapeInner.setPart(Shape.Piece.TOP_LEFT, new Shape.Part(star, "iron", Color.RED));
            shapeInner.setInner(shapeInnerInner);

            Shape shape = new Shape();
            shape.setPart(Shape.Piece.BOTTOM_LEFT, new Shape.Part(circle, "iron", Color.GRAY));
            shape.setPart(Shape.Piece.BOTTOM_RIGHT, new Shape.Part(circle, "iron", Color.GRAY));
            shape.setPart(Shape.Piece.TOP_RIGHT, new Shape.Part(square, "iron", Color.GRAY));
            shape.setPart(Shape.Piece.TOP_LEFT, new Shape.Part(star, "iron", Color.BLACK));
            shape.setInner(shapeInner);

            if (!model.cache.containsKey(shape))
            {
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

                IBakedModel bakedModel = model.bake(shape, textureGetter);
                model.cache.put(shape, bakedModel);
                return bakedModel;
            }

            return model.cache.get(shape);
        }
    }
}
