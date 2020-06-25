package requious.particle;

import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleSmokeColored extends ParticleSmokeNormal {
    boolean fullBright;

    public ParticleSmokeColored(World worldIn, double x, double y, double z, double vx, double vy, double vz, int time, Color color, boolean fullBright) {
        super(worldIn, x, y, z, vx, vy, vz, 1.0f);
        particleMaxAge = time;
        particleRed = color.getRed() / 255f;
        particleGreen = color.getGreen() / 255f;
        particleBlue = color.getBlue() / 255f;
        particleAlpha = color.getAlpha() / 255f;
        this.fullBright = fullBright;
    }

    @Override
    public int getBrightnessForRender(float p_189214_1_) {
        int i = super.getBrightnessForRender(p_189214_1_);
        int j = 240;
        int k = i >> 16 & 255;

        if (fullBright)
            return j | k << 16;
        else
            return i;
    }
}
