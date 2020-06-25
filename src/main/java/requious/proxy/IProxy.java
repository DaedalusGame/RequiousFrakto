package requious.proxy;

import net.minecraft.world.World;
import requious.particle.IParticleAnchor;

import java.awt.*;

public interface IProxy {
    void preInit();

    void init();

    void emitGlow(World world, IParticleAnchor anchor, double x, double y, double z, double vx, double vy, double vz, Color color, float scaleMin, float scaleMax, int lifetime, float partialTime);

    void spawnLightning(World world, IParticleAnchor anchor, double x1, double y1, double z1, double x2, double y2, double z2, int segments, double wildness, Color color, double thickness, int lifetime);

    void spawnSpark(World world, IParticleAnchor anchor, double x1, double y1, double z1, double x2, double y2, double z2, Color color, double thickness, int lifetime);

    void emitSmoke(World world, double x, double y, double z, double vx, double vy, double vz, Color color, int lifetime, boolean fullBright);

    boolean shouldRenderArcs();
}
