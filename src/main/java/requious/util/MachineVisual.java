package requious.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.client.particle.ParticleSmokeLarge;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import requious.Requious;
import requious.data.AssemblyProcessor;
import requious.particle.IParticleAnchor;
import requious.tile.TileEntityAssembly;

import java.awt.*;
import java.util.Random;

public abstract class MachineVisual {
    Parameter variableActive;

    public MachineVisual(Parameter variableActive) {
        this.variableActive = variableActive;
    }

    protected boolean isActive(AssemblyProcessor assembly) {
        return variableActive.getDouble(assembly,0) > 0;
    }

    public void update(TileEntityAssembly tile) {
        //NOOP
    }

    @SideOnly(Side.CLIENT)
    public void render(TileEntityAssembly tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        //NOOP
    }

    public static class DisplayModel extends MachineVisual {
        Parameter variableModel;
        Parameter variablePosition;
        Parameter variableScale;
        Parameter variableRotation;
        boolean global;

        public DisplayModel(Parameter variableActive, Parameter variableModel, Parameter variablePosition, Parameter variableScale, Parameter variableRotation, boolean global) {
            super(variableActive);
            this.variableModel = variableModel;
            this.variablePosition = variablePosition;
            this.variableScale = variableScale;
            this.variableRotation = variableRotation;
            this.global = global;
        }

        @Override
        public void update(TileEntityAssembly tile) {
            AssemblyProcessor processor = tile.getProcessor();
            variablePosition.stashValue(processor);
            variableScale.stashValue(processor);
            variableRotation.stashValue(processor);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(TileEntityAssembly tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
            AssemblyProcessor assembly = tile.getProcessor();
            if(!isActive(assembly))
                return;
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            ModelResourceLocation model = new ModelResourceLocation(variableModel.getString(assembly));
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            if (model != null) {
                if (Minecraft.getMinecraft().world != null) {
                    GL11.glPushMatrix();

                    IBakedModel ibakedmodel = modelmanager.getModel(model);

                    EnumFacing facing = assembly.getFacing();

                    Vec3d pos = variablePosition.getVector(assembly, partialTicks);
                    Vec3d rotation = variableRotation.getVector(assembly, partialTicks);
                    double scale = variableScale.getDouble(assembly, partialTicks);
                    if(!global) {
                        pos = Misc.getLocalVector(pos.addVector(-0.5, -0.5,-0.5), facing).addVector(0.5,0.5,0.5);
                        rotation = Misc.getLocalVector(rotation, facing);
                    }
                    GL11.glTranslated(x + pos.x, y + pos.y, z + pos.z);
                    //GL11.glRotated(variableRotateY.getDouble(assembly, partialTicks), 0, 1, 0);
                    GL11.glRotated(rotation.x, 1, 0, 0);
                    GL11.glRotated(rotation.y, 0, 1, 0);
                    GL11.glRotated(rotation.z, 0, 0, 1);

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
                    GL11.glScaled(scale, scale, scale);
                    blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public static class DisplayItem extends MachineVisual {
        Parameter variableItem;
        Parameter variablePosition;
        Parameter variableScale;
        Parameter variableRotation;
        boolean global;

        public DisplayItem(Parameter variableActive, Parameter variableItem, Parameter variablePosition, Parameter variableScale, Parameter variableRotation, boolean global) {
            super(variableActive);
            this.variableItem = variableItem;
            this.variablePosition = variablePosition;
            this.variableScale = variableScale;
            this.variableRotation = variableRotation;
            this.global = global;
        }

        @Override
        public void update(TileEntityAssembly tile) {
            AssemblyProcessor processor = tile.getProcessor();
            variablePosition.stashValue(processor);
            variableScale.stashValue(processor);
            variableRotation.stashValue(processor);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(TileEntityAssembly tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
            AssemblyProcessor assembly = tile.getProcessor();
            if(!isActive(assembly))
                return;
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            ItemStack stack = variableItem.getItem(assembly);
            if (!stack.isEmpty()) {
                if (Minecraft.getMinecraft().world != null) {
                    GL11.glPushMatrix();

                    EnumFacing facing = assembly.getFacing();

                    Vec3d pos = variablePosition.getVector(assembly, partialTicks);
                    Vec3d rotation = variableRotation.getVector(assembly, partialTicks);
                    double scale = variableScale.getDouble(assembly, partialTicks);
                    if(!global) {
                        pos = Misc.getLocalVector(pos.addVector(-0.5, -0.5,-0.5), facing).addVector(0.5,0.5,0.5);
                        rotation = Misc.getLocalVector(rotation, facing);
                    }
                    GL11.glTranslated(x + pos.x, y + pos.y, z + pos.z);
                    //GL11.glRotated(variableRotateY.getDouble(assembly, partialTicks), 0, 1, 0);
                    GL11.glRotated(rotation.x, 1, 0, 0);
                    GL11.glRotated(rotation.y, 0, 1, 0);
                    GL11.glRotated(rotation.z, 0, 0, 1);

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
                    GL11.glScaled(scale, scale, scale);
                    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public static class DisplayFluid extends MachineVisual {
        Parameter variableFluid;
        Parameter variableCapacity;
        Parameter variableDirection;
        Parameter variableStart;
        Parameter variableEnd;
        boolean global;

        public DisplayFluid(Parameter variableActive, Parameter variableFluid, Parameter variableCapacity, Parameter variableDirection, Parameter variableStart, Parameter variableEnd, boolean global) {
            super(variableActive);
            this.variableFluid = variableFluid;
            this.variableCapacity = variableCapacity;
            this.variableDirection = variableDirection;
            this.variableStart = variableStart;
            this.variableEnd = variableEnd;
            this.global = global;
        }

        @Override
        public void update(TileEntityAssembly tile) {
            AssemblyProcessor processor = tile.getProcessor();
            variableFluid.stashValue(processor);
            variableStart.stashValue(processor);
            variableEnd.stashValue(processor);
            variableCapacity.stashValue(processor);
        }

        @Override
        public void render(TileEntityAssembly tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
            super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
            AssemblyProcessor assembly = tile.getProcessor();
            if(!isActive(assembly))
                return;
            FluidStack stack = variableFluid.getFluid(assembly);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            if(stack != null) {
                EnumFacing facing = assembly.getFacing();
                Fluid fluid = stack.getFluid();
                int capacity = variableCapacity.getInteger(assembly,partialTicks);
                int amount = stack.amount;
                int c = fluid.getColor(stack);
                double slide = (double)amount / capacity;

                Vec3d start = variableStart.getVector(assembly, partialTicks);
                Vec3d end = variableEnd.getVector(assembly, partialTicks);

                if(!global) {
                    start = Misc.getLocalVector(start.addVector(-0.5, -0.5,-0.5), facing).addVector(0.5,0.5,0.5);
                    end = Misc.getLocalVector(end.addVector(-0.5, -0.5,-0.5), facing).addVector(0.5,0.5,0.5);
                }

                EnumFacing direction = variableDirection.getFacing(assembly);

                double xWest = Math.min(start.x, end.x);
                double xEast = Math.max(start.x, end.x);
                double yDown = Math.min(start.y, end.y);
                double yUp = Math.max(start.y, end.y);
                double zNorth = Math.min(start.z, end.z);
                double zSouth = Math.max(start.z, end.z);

                double width = (xEast - xWest) * slide;
                double height = (yUp - yDown) * slide;
                double depth = (zSouth - zNorth) * slide;

                switch (direction) {
                    case DOWN:
                        yDown = yUp - height;
                        break;
                    case UP:
                        yUp = yDown + height;
                        break;
                    case NORTH:
                        zNorth = zSouth - depth;
                        break;
                    case SOUTH:
                        zSouth = zNorth + depth;
                        break;
                    case WEST:
                        xWest = xEast - width;
                        break;
                    case EAST:
                        xEast = xWest + width;
                        break;
                }

                GlStateManager.disableCull();
                GlStateManager.disableLighting();
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                RenderUtil.renderFluidCuboid(stack, tile.getPos(), xWest, yDown, zNorth, xEast, yUp, zSouth, c);

                GlStateManager.disableBlend();
                GlStateManager.enableLighting();
            }
            GlStateManager.popMatrix();
        }
    }

    public static class DisplayCube extends MachineVisual {
        Parameter variableTexture;
        Parameter variableAmount;
        Parameter variableCapacity;
        Parameter variableDirection;
        Parameter variableStart;
        Parameter variableEnd;
        boolean global;

        public DisplayCube(Parameter variableActive, Parameter variableTexture, Parameter variableAmount, Parameter variableCapacity, Parameter variableDirection, Parameter variableStart, Parameter variableEnd, boolean global) {
            super(variableActive);
            this.variableTexture = variableTexture;
            this.variableAmount = variableAmount;
            this.variableCapacity = variableCapacity;
            this.variableDirection = variableDirection;
            this.variableStart = variableStart;
            this.variableEnd = variableEnd;
            this.global = global;
        }

        @Override
        public void update(TileEntityAssembly tile) {
            AssemblyProcessor processor = tile.getProcessor();
            variableStart.stashValue(processor);
            variableEnd.stashValue(processor);
            variableCapacity.stashValue(processor);
        }

        @Override
        public void render(TileEntityAssembly tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
            super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
            AssemblyProcessor assembly = tile.getProcessor();
            if(!isActive(assembly))
                return;
            ResourceLocation texture = new ResourceLocation(variableTexture.getString(assembly));
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            if(texture != null) {
                EnumFacing facing = assembly.getFacing();
                int capacity = variableCapacity.getInteger(assembly,partialTicks);
                int amount = variableAmount.getInteger(assembly,partialTicks);
                int c = 0xFFFFFFFF;
                double slide = (double)amount / capacity;

                Vec3d start = variableStart.getVector(assembly, partialTicks);
                Vec3d end = variableEnd.getVector(assembly, partialTicks);

                if(!global) {
                    start = Misc.getLocalVector(start.addVector(-0.5, -0.5,-0.5), facing).addVector(0.5,0.5,0.5);
                    end = Misc.getLocalVector(end.addVector(-0.5, -0.5,-0.5), facing).addVector(0.5,0.5,0.5);
                }

                EnumFacing direction = variableDirection.getFacing(assembly);

                double xWest = Math.min(start.x, end.x);
                double xEast = Math.max(start.x, end.x);
                double yDown = Math.min(start.y, end.y);
                double yUp = Math.max(start.y, end.y);
                double zNorth = Math.min(start.z, end.z);
                double zSouth = Math.max(start.z, end.z);

                double width = (xEast - xWest) * slide;
                double height = (yUp - yDown) * slide;
                double depth = (zSouth - zNorth) * slide;

                switch (direction) {
                    case DOWN:
                        yDown = yUp - height;
                        break;
                    case UP:
                        yUp = yDown + height;
                        break;
                    case NORTH:
                        zNorth = zSouth - depth;
                        break;
                    case SOUTH:
                        zSouth = zNorth + depth;
                        break;
                    case WEST:
                        xWest = xEast - width;
                        break;
                    case EAST:
                        xEast = xWest + width;
                        break;
                }

                GlStateManager.disableCull();
                GlStateManager.disableLighting();
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                RenderUtil.renderTextureCuboid(texture, tile.getPos(), xWest, yDown, zNorth, xEast, yUp, zSouth, c);

                GlStateManager.disableBlend();
                GlStateManager.enableLighting();
            }
            GlStateManager.popMatrix();
        }
    }

    public static class Flame extends MachineVisual {
        Parameter variableStart;
        Parameter variableEnd;
        Parameter variableVelocity;
        Parameter variableLifetime;
        Parameter variableSizeMin;
        Parameter variableSizeMax;
        Parameter variableColor;
        boolean global;
        Random random = new Random();

        public Flame(Parameter variableActive, Parameter variableStart, Parameter variableEnd, Parameter variableVelocity, Parameter variableLifetime, Parameter variableSizeMin, Parameter variableSizeMax, Parameter variableColor, boolean global) {
            super(variableActive);
            this.variableStart = variableStart;
            this.variableEnd = variableEnd;
            this.variableVelocity = variableVelocity;
            this.variableLifetime = variableLifetime;
            this.variableSizeMin = variableSizeMin;
            this.variableSizeMax = variableSizeMax;
            this.variableColor = variableColor;
            this.global = global;
        }

        @Override
        public void update(TileEntityAssembly tile) {
            AssemblyProcessor assembly = tile.getProcessor();
            if(!isActive(assembly))
                return;
            BlockPos pos = tile.getPos();
            Vec3d start = variableStart.getVector(assembly, 0);
            Vec3d end = variableEnd.getVector(assembly, 0);
            Color color = variableColor.getColor(assembly, 0);
            double x = start.x + random.nextDouble() * (end.x - start.x);
            double y = start.y + random.nextDouble() * (end.y - start.y);
            double z = start.z + random.nextDouble() * (end.z - start.z);

            Vec3d offset = new Vec3d(x, y, z);
            Vec3d velocity = variableVelocity.getVector(assembly,0);
            float minSize = (float) variableSizeMin.getDouble(assembly,0);
            float maxSize = (float) variableSizeMax.getDouble(assembly,0);
            int lifetime = variableLifetime.getInteger(assembly,0);
            lifetime = lifetime / 2 + random.nextInt(lifetime / 2);

            if (!global) {
                offset = Misc.getLocalVector(offset.addVector(-0.5, -0.5, -0.5), assembly.getFacing()).addVector(0.5, 0.5, 0.5);
                velocity = Misc.getLocalVector(velocity, assembly.getFacing());
            }

            Requious.PROXY.emitGlow(tile.getWorld(), IParticleAnchor.zero(), pos.getX() + offset.x, pos.getY() + offset.y, pos.getZ() + offset.z, velocity.x, velocity.y, velocity.z, color, minSize, maxSize, lifetime, 0);
        }
    }

    public static class Smoke extends MachineVisual {
        Parameter variableStart;
        Parameter variableEnd;
        Parameter variableVelocity;
        Parameter variableLifetime;
        Parameter variableColor;
        private boolean fullBright;
        boolean global;
        Random random = new Random();

        public Smoke(Parameter variableActive, Parameter variableStart, Parameter variableEnd, Parameter variableVelocity, Parameter variableLifetime, Parameter variableColor, boolean fullBright, boolean global) {
            super(variableActive);
            this.variableStart = variableStart;
            this.variableEnd = variableEnd;
            this.variableVelocity = variableVelocity;
            this.variableLifetime = variableLifetime;
            this.variableColor = variableColor;
            this.fullBright = fullBright;
            this.global = global;
        }

        @Override
        public void update(TileEntityAssembly tile) {
            AssemblyProcessor assembly = tile.getProcessor();
            if(!isActive(assembly))
                return;
            BlockPos pos = tile.getPos();
            Vec3d start = variableStart.getVector(assembly, 0);
            Vec3d end = variableEnd.getVector(assembly, 0);
            Color color = variableColor.getColor(assembly, 0);
            double x = start.x + random.nextDouble() * (end.x - start.x);
            double y = start.y + random.nextDouble() * (end.y - start.y);
            double z = start.z + random.nextDouble() * (end.z - start.z);

            Vec3d offset = new Vec3d(x, y, z);
            Vec3d velocity = variableVelocity.getVector(assembly,0);
            int lifetime = variableLifetime.getInteger(assembly,0);
            lifetime = lifetime / 2 + random.nextInt(lifetime / 2);

            if (!global) {
                offset = Misc.getLocalVector(offset.addVector(-0.5, -0.5, -0.5), assembly.getFacing()).addVector(0.5, 0.5, 0.5);
                velocity = Misc.getLocalVector(velocity, assembly.getFacing());
            }

            Requious.PROXY.emitSmoke(tile.getWorld(), pos.getX() + offset.x, pos.getY() + offset.y, pos.getZ() + offset.z, velocity.x, velocity.y, velocity.z, color, lifetime, fullBright);
        }
    }
}
