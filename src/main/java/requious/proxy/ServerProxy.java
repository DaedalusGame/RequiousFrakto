package requious.proxy;

import net.minecraft.world.World;
import requious.particle.IParticleAnchor;

import java.awt.*;

public class ServerProxy implements IProxy {
    @Override
    public void preInit() {

    }

    @Override
    public void init() {

    }

    @Override
    public void emitGlow(World world, IParticleAnchor anchor, double x, double y, double z, double vx, double vy, double vz, Color color, float scaleMin, float scaleMax, int lifetime, float partialTime) {

    }

    @Override
    public void spawnLightning(World world, IParticleAnchor anchor, double x1, double y1, double z1, double x2, double y2, double z2, int segments, double wildness, Color color, double thickness, int lifetime) {

    }

    @Override
    public void spawnSpark(World world, IParticleAnchor anchor, double x1, double y1, double z1, double x2, double y2, double z2, Color color, double thickness, int lifetime) {

    }

    @Override
    public boolean shouldRenderArcs() {
        return false;
    }
}
