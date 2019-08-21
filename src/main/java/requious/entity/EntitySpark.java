package requious.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import requious.Requious;
import requious.particle.IParticleAnchor;
import requious.tile.ISparkAcceptor;
import requious.util.ExtraSerializers;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntitySpark extends Entity {
    static List<ISparkValue.Deserializer> valueDeserializers = new ArrayList<>();
    static List<ISparkTarget.Deserializer> targetDeserializers = new ArrayList<>();

    public static void registerValue(ISparkValue.Deserializer deserializer) {
        valueDeserializers.add(deserializer);
    }

    public static void registerTarget(ISparkTarget.Deserializer deserializer) {
        targetDeserializers.add(deserializer);
    }

    public static final DataParameter<Float> size = EntityDataManager.createKey(EntitySpark.class, DataSerializers.FLOAT);
    public static final DataParameter<Integer> color = EntityDataManager.createKey(EntitySpark.class, DataSerializers.VARINT);
    public static final DataParameter<SparkEffect> effect = EntityDataManager.createKey(EntitySpark.class, ExtraSerializers.SPARK_EFFECT);

    public int lifetime = 80;
    public ISparkTarget target;
    public ISparkValue value;
    List<Comparable> history = new ArrayList<>();
    public double lastX, lastY, lastZ;
    boolean received;

    public EntitySpark(World worldIn) {
        super(worldIn);
    }

    public void init(double x, double y, double z, double vx, double vy, double vz, @Nonnull ISparkTarget target, @Nonnull ISparkValue value) {
        setPosition(x, y, z);
        this.motionX = vx;
        this.motionY = vy;
        this.motionZ = vz;
        this.target = target;
        this.value = value;
        this.lifetime = value.getLifetime(this);
    }

    public void pushHistory(Comparable token) {
        if(history.contains(token))
            this.setDead();
        history.add(token);
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    @Override
    protected void entityInit() {
        this.getDataManager().register(size, 1f);
        this.getDataManager().register(color, 0xFFFFFFFF);
        this.getDataManager().register(effect, SparkEffect.LightningBall);
    }

    private void updateVisuals() {
        this.getDataManager().set(size,value.getSize(this));
        this.getDataManager().set(color,value.getColor(this).getRGB());
        this.getDataManager().set(effect,value.getEffect(this));
    }

    @Override
    public void setDead() {
        super.setDead();
        setReceived(true);
    }

    @Override
    public void onUpdate(){
        super.onUpdate();
        lifetime --;
        if (!isDead && lifetime <= 0 || received){
            setDead();
        }
        if(lastX == 0 && lastY == 0 && lastZ == 0)
        {
            lastX = posX;
            lastY = posY;
            lastZ = posZ;
        }
        if (world.isRemote){
            double deltaX = posX - lastX;
            double deltaY = posY - lastY;
            double deltaZ = posZ - lastZ;
            double dist = Math.ceil(Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ) * 20);
            for (double i = 0; i < dist; i ++){
                double coeff = i/dist;
                //Requious.PROXY.emitGlow(world,(partialTicks) -> new Vec3d(0,0,0),(float)(prevPosX+ deltaX *coeff), (float)(prevPosY+ deltaY *coeff), (float)(prevPosZ+ deltaZ *coeff), 0.0125f*(rand.nextFloat()-0.5f), 0.0125f*(rand.nextFloat()-0.5f), 0.0125f*(rand.nextFloat()-0.5f), new Color(255, 64, 16), 1.0f, 2.0f, 12, 0);
            }
            int segments = 1;
            Requious.PROXY.spawnLightning(world,  IParticleAnchor.zero(), lastX,  lastY,  lastZ, posX, posY, posZ, 4, 0.2, new Color(255, 16, 16), 1.4f, 10);
            double distanceMod = rand.nextDouble();
            for(int i = 0; i < segments; i++) {
                double distanceA = rand.nextDouble() * 0.2 + 0.2;
                double distanceB = rand.nextDouble() * 0.4 + 0.2;
                Vec3d offsetA = Vec3d.fromPitchYaw((float) (rand.nextDouble() * 360), (float) (rand.nextDouble() * 360)).scale(0);
                Vec3d offsetB = Vec3d.fromPitchYaw((float) (rand.nextDouble() * 360), (float) (rand.nextDouble() * 360)).scale(distanceB * distanceMod);
                //Requious.PROXY.spawnLightning(world, IParticleAnchor.get(this), offsetA.x, offsetA.y, offsetA.z, offsetB.x, offsetB.y, offsetB.z, 4, 0.1, new Color(255, 16, 16), 0.5f, 5);
            }
            lastX = posX;
            lastY = posY;
            lastZ = posZ;
        }
        if(!world.isRemote && (target == null || !target.isValid(this)))
            setDead();
        if (!received && !world.isRemote){
            Vec3d dest = target.getPosition(this);
            boolean cont = true;
            for(int i = 0; i < 4 && cont && !received; i++) {
                cont = moveStep(dest);
            }
            updateVisuals();
        }
        setPosition(posX,posY,posZ);
    }

    private boolean moveStep(Vec3d dest) {
        double targetX = dest.x;
        double targetY = dest.y;
        double targetZ = dest.z;
        Vec3d targetVector = new Vec3d(targetX-posX,targetY-posY,targetZ-posZ);
        double length = targetVector.lengthVector();
        if(length != 0) {
            targetVector = targetVector.scale(0.3 / length);
            double weight = 0;
            if (length <= 3) {
                weight = 0.9 * ((3.0 - length) / 3.0);
            }
            motionX = (0.9 - weight) * motionX + (0.1 + weight) * targetVector.x;
            motionY = (0.9 - weight) * motionY + (0.1 + weight) * targetVector.y;
            motionZ = (0.9 - weight) * motionZ + (0.1 + weight) * targetVector.z;
        }
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        double distanceSq = this.getDistanceSq(targetX, targetY, targetZ);
        if (distanceSq < 0.1){
            ISparkAcceptor acceptor = target.getAcceptor(this);
            if(acceptor != null) {
                posX = targetX;
                posY = targetY;
                posZ = targetZ;
                acceptor.receive(this);
                return false;
            }
        }
        return true;
    }

    private ISparkValue deserializeValue(NBTTagCompound compound) {
        for (ISparkValue.Deserializer deserializer : valueDeserializers) {
            ISparkValue value = deserializer.deserialize(compound);
            if (value != null)
                value.readFromNBT(compound);
            return value;
        }
        return null;
    }

    private ISparkTarget deserializeTarget(NBTTagCompound compound) {
        for (ISparkTarget.Deserializer deserializer : targetDeserializers) {
            ISparkTarget target = deserializer.deserialize(compound);
            if (target != null) {
                target.readFromNBT(compound);
                return target;
            }
        }
        return null;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        target = deserializeTarget(compound);
        value = deserializeValue(compound);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (target != null)
            target.writeToNBT(compound);
        if (value != null)
            value.writeToNBT(compound);
    }
}
