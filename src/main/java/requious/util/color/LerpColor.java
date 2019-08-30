package requious.util.color;

import requious.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class LerpColor implements ICustomColor {
    List<Color> colors;
    boolean hsbMix;

    public LerpColor(List<Color> colors, boolean hsbMix) {
        this.colors = colors;
        this.hsbMix = hsbMix;
    }

    @Override
    public Color get() {
        return get(0);
    }

    @Override
    public Color get(double lerp) {
        if(hsbMix)
            return Misc.lerpColorHSB(colors, lerp);
        else
            return Misc.lerpColorRGB(colors, lerp);
    }
}
