package requious.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import requious.entity.SparkEffect;

import java.io.IOException;

public class ExtraSerializers {
    public static final DataSerializer<SparkEffect> SPARK_EFFECT = new DataSerializer<SparkEffect>() {
        @Override
        public void write(PacketBuffer buf, SparkEffect value) {
            buf.writeInt(value.ordinal());
        }

        @Override
        public SparkEffect read(PacketBuffer buf) throws IOException {
            return SparkEffect.values()[buf.readInt()];
        }

        @Override
        public DataParameter<SparkEffect> createKey(int id) {
            return new DataParameter<>(id,this);
        }

        @Override
        public SparkEffect copyValue(SparkEffect value) {
            return value;
        }
    };

    static {
        DataSerializers.registerSerializer(SPARK_EFFECT);
    }
}
