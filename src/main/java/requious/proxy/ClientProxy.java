package requious.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import requious.Registry;
import requious.entity.EntitySpark;
import requious.entity.RenderNull;
import requious.item.ItemTuningFork;
import requious.particle.IParticleAnchor;
import requious.particle.ParticleGlow;
import requious.particle.ParticleRenderer;

import java.awt.*;
import java.util.Random;

public class ClientProxy implements IProxy {
    ParticleRenderer renderer;
    Random random = new Random();

    @Override
    public void preInit() {
        renderer = new ParticleRenderer();

        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmill.class, new TileEntityWindmillRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntitySpark.class, new RenderNull.Factory());
    }

    @Override
    public void init() {
        Registry.initColors();
    }

    @Override
    public void emitGlow(World world, IParticleAnchor anchor, double x, double y, double z, double vx, double vy, double vz, Color color, float scaleMin, float scaleMax, int lifetime, float partialTime) {
        renderer.addParticle(new ParticleGlow(world, anchor, x, y, z, vx, vy, vz, color, scaleMin, scaleMax, lifetime, partialTime));
    }

    @Override
    public void spawnLightning(World world, IParticleAnchor anchor, double x1, double y1, double z1, double x2, double y2, double z2, int segments, double wildness, Color color, double thickness, int lifetime)
    {
        double prevx = x1;
        double prevy = y1;
        double prevz = z1;

        for(int i = 1; i <= segments; i++)
        {
            double coeff = (double)i / segments;
            double wildCoeff = Math.sin(Math.PI * coeff) * wildness;
            double currx = MathHelper.clampedLerp(x1, x2, coeff) + (random.nextDouble() - 0.5) * 2 * wildCoeff;
            double curry = MathHelper.clampedLerp(y1, y2, coeff) + (random.nextDouble() - 0.5) * 2 * wildCoeff;
            double currz = MathHelper.clampedLerp(z1, z2, coeff) + (random.nextDouble() - 0.5) * 2 * wildCoeff;
            spawnSpark(world,anchor,prevx,prevy,prevz,currx,curry,currz, color, thickness, lifetime);
            prevx = currx;
            prevy = curry;
            prevz = currz;
        }
    }

    @Override
    public void spawnSpark(World world, IParticleAnchor anchor, double x1, double y1, double z1, double x2, double y2, double z2, Color color, double thickness, int lifetime)
    {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        double dist = Math.sqrt(dx*dx+dy*dy+dz*dz);
        double segments = Math.ceil(dist) * 10;
        for (int i = 0; i <= segments; i++) {
            double coeff = i / segments;
            double sparkx = MathHelper.clampedLerp(x1,x2,coeff);
            double sparky = MathHelper.clampedLerp(y1,y2,coeff);
            double sparkz = MathHelper.clampedLerp(z1,z2,coeff);
            emitGlow(world, anchor, sparkx, sparky, sparkz, 0, 0, 0, color, (float)thickness, (float)thickness, lifetime, 0);
        }
    }

    @Override
    public boolean shouldRenderArcs() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemStack mainStack = player.getHeldItemMainhand();
        ItemStack offStack = player.getHeldItemOffhand();
        return mainStack.getItem() instanceof ItemTuningFork || offStack.getItem() instanceof ItemTuningFork;
    }
}
