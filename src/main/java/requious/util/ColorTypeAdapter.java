package requious.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;

public class ColorTypeAdapter extends TypeAdapter<Color> {
    @Override
    public void write(JsonWriter out, Color value) throws IOException {
        out.beginObject();
        out.name("r").value(value.getRed());
        out.name("g").value(value.getGreen());
        out.name("b").value(value.getBlue());
        out.name("a").value(value.getAlpha());
        out.endObject();
    }

    @Override
    public Color read(JsonReader in) throws IOException {
        in.beginObject();
        int r = -1;
        int g = -1;
        int b = -1;
        int a = 255;
        while (in.hasNext()) {
            switch (in.nextName()) {
                case ("r"):
                    r = in.nextInt();
                    break;
                case ("g"):
                    g = in.nextInt();
                    break;
                case ("b"):
                    b = in.nextInt();
                    break;
                case ("a"):
                    a = in.nextInt();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        return new Color(r, g, b, a);
    }
}
