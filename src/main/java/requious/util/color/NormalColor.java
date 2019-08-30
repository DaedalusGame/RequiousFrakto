package requious.util.color;

import java.awt.*;

public class NormalColor implements ICustomColor {
    Color internal;

    public NormalColor(Color internal) {
        this.internal = internal;
    }

    @Override
    public Color get() {
        return internal;
    }
}
