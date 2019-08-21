package requious.entity;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderNull extends RenderEntity {
    public RenderNull(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        //
    }

    @Override
    public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float yaw, float pTicks){
        //
    }

    public static class Factory implements IRenderFactory {
        @Override
        public RenderNull createRenderFor(RenderManager manager) {
            return new RenderNull(manager);
        }
    }
}
