package requious.block;

import net.minecraft.util.ResourceLocation;

import java.awt.*;

public interface IDynamicModel {
    ResourceLocation getRedirect();

    Color getTint(int tintIndex);
}
