package requious.particle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import requious.entity.EntitySpark;

public interface IParticleAnchor {
    Vec3d getPosition(float partialTicks);

    static IParticleAnchor get(Entity entity) {
        return partialTicks -> new Vec3d(
                MathHelper.clampedLerp(entity.prevPosX,entity.posX,partialTicks),
                MathHelper.clampedLerp(entity.prevPosY,entity.posY,partialTicks),
                MathHelper.clampedLerp(entity.prevPosZ,entity.posZ,partialTicks)
        );
    }

    static IParticleAnchor get(EntitySpark entity) {
        return partialTicks -> new Vec3d(
                MathHelper.clampedLerp(entity.lastX,entity.posX,partialTicks),
                MathHelper.clampedLerp(entity.lastY,entity.posY,partialTicks),
                MathHelper.clampedLerp(entity.lastZ,entity.posZ,partialTicks)
        );
    }

    static IParticleAnchor zero() {
        return partialTicks -> Vec3d.ZERO;
    }
}
