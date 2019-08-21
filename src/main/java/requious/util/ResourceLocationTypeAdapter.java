package requious.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {
    @Override
    public void write(JsonWriter out, ResourceLocation value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public ResourceLocation read(JsonReader in) throws IOException {
        return new ResourceLocation(in.nextString());
    }
}
