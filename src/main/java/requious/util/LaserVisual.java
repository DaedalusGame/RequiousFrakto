package requious.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import requious.Requious;
import requious.particle.IParticleAnchor;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
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

    public static class FireBeam extends LaserVisual {
        Random random = new Random();

        float size;
        float wildness;
        float length;
        int time;
        int amount;

        public FireBeam() {
            super("fire_beam");
        }

        public FireBeam(Color color, float size, float wildness, float length, int amount, int time) {
            this();
            this.color = color;
            this.size = size;
            this.wildness = wildness;
            this.length = length;
            this.time = time;
            this.amount = amount;
        }

        @Override
        public void render(World world, BlockPos emit, BlockPos target, int sent) {
            double x1 = emit.getX() + 0.5;
            double y1 = emit.getY() + 0.5;
            double z1 = emit.getZ() + 0.5;

            double dx = target.getX() + 0.5 - x1;
            double dy = target.getY() + 0.5 - y1;
            double dz = target.getZ() + 0.5 - z1;

            for(int i = 0; i < amount; i++) {
                double rx = (random.nextDouble() - 0.5) * 2 * wildness;
                double ry = (random.nextDouble() - 0.5) * 2 * wildness;
                double rz = (random.nextDouble() - 0.5) * 2 * wildness;

                Requious.PROXY.emitGlow(world, IParticleAnchor.zero(), x1 + rx, y1 + ry, z1 + rz, dx * 2 * length / time, dy * 2 * length / time, dz * 2 * length / time, color, 0, size, time, 0);
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            size = compound.getFloat("size");
            wildness = compound.getFloat("wildness");
            length = compound.getFloat("length");
            amount = compound.getInteger("amount");
            time = compound.getInteger("time");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound = super.writeToNBT(compound);
            compound.setFloat("size",size);
            compound.setFloat("wildness",wildness);
            compound.setFloat("length",length);
            compound.setInteger("amount",amount);
            compound.setInteger("time",time);
            return compound;
        }
    }
}
