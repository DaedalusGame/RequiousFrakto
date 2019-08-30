package requious.util.color;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import requious.util.FluidColorHelper;

import java.awt.*;

public class FluidColor implements ICustomColor {
    @Override
    public Color get() {
        return new Color(0,0,0,0);
    }

    @Override
    public Color get(ItemStack stack) {
        FluidStack fluid = FluidUtil.getFluidContained(stack);
        return new Color(FluidColorHelper.getColor(fluid),true);
    }
}
