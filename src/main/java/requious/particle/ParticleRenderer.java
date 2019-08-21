package requious.particle;

import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class ParticleRenderer {
    ArrayDeque<Particle> normalParticles = new ArrayDeque<>();
    ArrayDeque<Particle> additiveParticles = new ArrayDeque<>();
    ArrayDeque<Particle> throughParticles = new ArrayDeque<>();
    ArrayDeque<Particle> additiveThroughParticles = new ArrayDeque<>();
    Queue<Particle> queue = Queues.<Particle>newArrayDeque();

    public ParticleRenderer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent event) {
        ResourceLocation particleGlow = new ResourceLocation("requious:entity/particle_glow");
        event.getMap().registerSprite(particleGlow);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.START) {
            updateParticles();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderAfterWorld(RenderWorldLastEvent event) {
        GlStateManager.pushMatrix();
        renderParticles(Minecraft.getMinecraft().player, event.getPartialTicks());
        GlStateManager.popMatrix();
    }

    public void updateParticles() {
        updateParticles(normalParticles);
        updateParticles(additiveParticles);
        updateParticles(throughParticles);
        updateParticles(additiveThroughParticles);

        if (!this.queue.isEmpty()) {
            for (Particle particle = this.queue.poll(); particle != null; particle = this.queue.poll()) {
                ISpecialParticle emberParticle = (ISpecialParticle) particle;
                boolean additive = emberParticle.isAdditive();
                boolean rendersThrough = emberParticle.renderThroughBlocks();

                getParticleCollection(additive, rendersThrough).add(particle);
            }
        }
    }

    public void updateParticles(ArrayDeque<Particle> particles) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            if (particle.isAlive())
                particle.onUpdate();
            else {
                iterator.remove();
            }
        }
    }

    public void renderParticles(EntityPlayer dumbplayer, float partialTicks) {
        float f = ActiveRenderInfo.getRotationX();
        float f1 = ActiveRenderInfo.getRotationZ();
        float f2 = ActiveRenderInfo.getRotationYZ();
        float f3 = ActiveRenderInfo.getRotationXY();
        float f4 = ActiveRenderInfo.getRotationXZ();
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            Particle.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            Particle.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            Particle.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            Particle.cameraViewDir = player.getLook(partialTicks);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.alphaFunc(516, 0.003921569F);
            GlStateManager.disableCull();

            GlStateManager.depthMask(false);

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (Particle particle : normalParticles) {
                particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
            }
            tess.draw();

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (Particle particle : additiveParticles) {
                particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
            }
            tess.draw();

            GlStateManager.disableDepth();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (Particle particle : throughParticles) {
                particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
            }
            tess.draw();

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (Particle particle : additiveThroughParticles) {
                particle.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
            }
            tess.draw();
            GlStateManager.enableDepth();

            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
        }
    }

    public void addParticle(Particle particle) {
        if (particle instanceof ISpecialParticle) {
            queue.add(particle);
        }
    }

    private Collection<Particle> getParticleCollection(boolean additive, boolean rendersThroughBlocks) {
        if (!rendersThroughBlocks && !additive)
            return normalParticles;
        else if (!rendersThroughBlocks)
            return additiveParticles;
        else if (!additive)
            return throughParticles;
        else
            return additiveThroughParticles;
    }
}
