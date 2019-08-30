package requious.util.color;

import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import requious.util.Misc;

import java.awt.Color;
import java.util.List;

public class VariableColor implements ICustomColor {
    ResourceLocation variableId;
    List<Color> colors;
    boolean hsbMix;

    public VariableColor(List<Color> colors, ResourceLocation variableId, boolean hsbMix) {
        this.variableId = variableId;
        this.colors = colors;
        this.hsbMix = hsbMix;
    }

    @Override
    public Color get() {
        return new Color(0,0,0,0);
    }

    @Override
    public Color get(ItemStack stack) {
        IItemPropertyGetter getter = stack.getItem().getPropertyGetter(variableId);
        float lerp = 0;
        if(getter != null)
            lerp = getter.apply(stack,null,null);

        if(hsbMix)
            return Misc.lerpColorHSB(colors, lerp);
        else
            return Misc.lerpColorRGB(colors, lerp);
    }
}
