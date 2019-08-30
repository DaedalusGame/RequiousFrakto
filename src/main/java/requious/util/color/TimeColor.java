package requious.util.color;

import net.minecraft.item.ItemStack;
import requious.util.Misc;

import java.awt.*;
import java.util.List;

public class TimeColor implements ICustomColor {
    List<Color> colors;
    double pulseLength;
    boolean onlyRisingEdge;
    boolean hsbMix;

    public TimeColor(List<Color> colors, double pulseLength, boolean onlyRisingEdge, boolean hsbMix) {
        this.colors = colors;
        this.pulseLength = pulseLength;
        this.onlyRisingEdge = onlyRisingEdge;
        this.hsbMix = hsbMix;
    }

    @Override
    public Color get() {
        double lerp;
        if (onlyRisingEdge)
            lerp = (System.currentTimeMillis() % pulseLength) / pulseLength;
        else
            lerp = Math.sin(System.currentTimeMillis() / pulseLength) * 0.5 + 0.5;

        if(hsbMix)
            return Misc.lerpColorHSB(colors, lerp);
        else
            return Misc.lerpColorRGB(colors, lerp);
    }
}
