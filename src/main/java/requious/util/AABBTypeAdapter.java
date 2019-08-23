package requious.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.io.IOException;

public class AABBTypeAdapter extends TypeAdapter<AxisAlignedBB> {
    @Override
    public void write(JsonWriter out, AxisAlignedBB value) throws IOException {
        out.beginObject();
        out.name("x1").value(value.minX);
        out.name("y1").value(value.minY);
        out.name("z1").value(value.minZ);
        out.name("x2").value(value.maxX);
        out.name("y2").value(value.maxY);
        out.name("z2").value(value.maxZ);
        out.endObject();
    }

    @Override
    public AxisAlignedBB read(JsonReader in) throws IOException {
        in.beginObject();
        int x1 = 0;
        int y1 = 0;
        int z1 = 0;
        int x2 = 1;
        int y2 = 1;
        int z2 = 1;
        while (in.hasNext()) {
            switch (in.nextName()) {
                case ("x1"):
                    x1 = in.nextInt();
                    break;
                case ("y1"):
                    y1 = in.nextInt();
                    break;
                case ("z1"):
                    z1 = in.nextInt();
                    break;
                case ("x2"):
                    x2 = in.nextInt();
                    break;
                case ("y2"):
                    y2 = in.nextInt();
                    break;
                case ("z2"):
                    z2 = in.nextInt();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        return new AxisAlignedBB(x1,y1,z1,x2,y2,z2);
    }
}
