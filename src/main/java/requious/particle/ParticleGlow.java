package requious.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

public class ParticleGlow extends Particle implements ISpecialParticle {
    static Random random = new Random();

    public IParticleAnchor anchor;
    public Color color;
    public float minScale;
    public float maxScale;
    public float initAlpha;
    public float partialTime;
    public ResourceLocation texture = new ResourceLocation("requious:entity/particle_glow");
    public ParticleGlow(World worldIn, IParticleAnchor anchor, double x, double y, double z, double vx, double vy, double vz, Color color, float scaleMin, float scaleMax, int lifetime, float partialTime) {
        super(worldIn, x,y,z,0,0,0);
        this.anchor = anchor;
        this.color = color;
        this.setRBGColorF(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
        this.particleMaxAge = (int)((float)lifetime*0.5f);
        this.particleScale = scaleMin;
        this.minScale = scaleMin;
        this.maxScale = scaleMax;
        this.motionX = vx;
        this.motionY = vy;
        this.motionZ = vz;
        this.canCollide = false;
        this.initAlpha = color.getAlpha()/255f;
        this.particleAngle = rand.nextFloat()*2.0f*(float)Math.PI;
        this.partialTime = partialTime;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    @Override
    public int getBrightnessForRender(float pTicks){
        return super.getBrightnessForRender(pTicks);
    }

    @Override
    public boolean shouldDisableDepth(){
        return false;
    }

    @Override
    public int getFXLayer(){
        return 1;
    }

    @Override
    public void onUpdate(){
        super.onUpdate();
        float lifeCoeff = (particleAge + partialTime)/(float)particleMaxAge;
        float dScale = (maxScale - minScale) / 2;
        this.particleScale = minScale + dScale + dScale*(float)Math.sin(lifeCoeff*Math.PI);
        this.particleAlpha = (float) MathHelper.clampedLerp(initAlpha,0, lifeCoeff);
        this.prevParticleAngle = particleAngle;
        particleAngle += 1.0f;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Vec3d anchorPos = anchor.getPosition(partialTicks);
        posX += anchorPos.x;
        posY += anchorPos.y;
        posZ += anchorPos.z;
        prevPosX += anchorPos.x;
        prevPosY += anchorPos.y;
        prevPosZ += anchorPos.z;
        //interpPosX += anchorPos.x;
        //interpPosY += anchorPos.y;
        //interpPosZ += anchorPos.z;
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        posX -= anchorPos.x;
        posY -= anchorPos.y;
        posZ -= anchorPos.z;
        prevPosX -= anchorPos.x;
        prevPosY -= anchorPos.y;
        prevPosZ -= anchorPos.z;
        //interpPosX -= anchorPos.x;
        //interpPosY -= anchorPos.y;
        //interpPosZ -= anchorPos.z;
    }

    @Override
    public boolean isAdditive() {
        return true;
    }

    @Override
    public boolean renderThroughBlocks() {
        return false;
    }
}
