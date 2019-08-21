package requious.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import requious.Requious;
import requious.particle.IParticleAnchor;

import java.awt.*;
import java.util.HashMap;
import java.util.function.Supplier;

public abstract class LaserVisual {
    private static HashMap<String, Supplier<LaserVisual>> REGISTRY = new HashMap<>();

    public static void register(String type, Supplier<LaserVisual> supplier) {
        REGISTRY.put(type,supplier);
    }

    public static LaserVisual deserializeNBT(NBTTagCompound compound) {
        LaserVisual visual = REGISTRY.get(compound.getString("type")).get();
        visual.readFromNBT(compound);
        return visual;
    }

    String type;
    Color color = Color.WHITE;

    public LaserVisual(String type) {
        this.type = type;
    }

    public abstract void render(World world, BlockPos emit, BlockPos target, int sent);

    public final NBTTagCompound serializeNBT() {
        return writeToNBT(new NBTTagCompound());
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("type",type);
        compound.setInteger("color",color.getRGB());
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        color = new Color(compound.getInteger("color"),true);
    }

    public static class None extends LaserVisual {
        public None() {
            super("beam");
        }

        @Override
        public void render(World world, BlockPos emit, BlockPos target, int sent) {
            //NOOP
        }
    }

    public static class Beam extends LaserVisual {
        float thickness;

        public Beam() {
            super("beam");
        }

        public Beam(Color color, float thickness) {
            this();
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void render(World world, BlockPos emit, BlockPos target, int sent) {
            Requious.PROXY.spawnSpark(world, IParticleAnchor.zero(), emit.getX() + 0.5, emit.getY() + 0.5, emit.getZ() + 0.5, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, color, thickness, 5);
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            thickness = compound.getFloat("thickness");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound = super.writeToNBT(compound);
            compound.setFloat("thickness",thickness);
            return compound;
        }
    }

    public static class Lightning extends LaserVisual {
        float thickness;
        float wildness;
        int segments;

        public Lightning() {
            super("lightning");
        }

        public Lightning(Color color, float thickness, float wildness, int segments) {
            this();
            this.color = color;
            this.thickness = thickness;
            this.wildness = wildness;
            this.segments = segments;
        }

        @Override
        public void render(World world, BlockPos emit, BlockPos target, int sent) {
            Requious.PROXY.spawnLightning(world, IParticleAnchor.zero(), emit.getX() + 0.5, emit.getY() + 0.5, emit.getZ() + 0.5, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, segments, wildness, color, thickness, 5);
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            thickness = compound.getFloat("thickness");
            wildness = compound.getFloat("wildness");
            segments = compound.getInteger("segments");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound = super.writeToNBT(compound);
            compound.setFloat("thickness",thickness);
            compound.setFloat("wildness",wildness);
            compound.setInteger("segments",segments);
            return compound;
        }
    }
}
