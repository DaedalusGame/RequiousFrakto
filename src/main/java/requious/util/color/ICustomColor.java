package requious.util.color;

import net.minecraft.item.ItemStack;

import java.awt.*;

public interface ICustomColor {
    Color get();

    default Color get(double lerp) {
        return get();
    }

    default Color get(ItemStack stack) {
        return get();
    }
}
