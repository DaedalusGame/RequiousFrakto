package requious.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import requious.Requious;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {
        // NO-OP
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new GuiConfig(parentScreen);
    }


    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }


    public static class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig {

        public GuiConfig(GuiScreen parentScreen) {
            super(parentScreen, getAllElements(), Requious.MODID, false, false, net.minecraftforge.fml.client.config.GuiConfig.getAbridgedConfigPath(Requious.configuration.toString()));
        }

        public static List<IConfigElement> getAllElements() {
            List<IConfigElement> list = new ArrayList<>();

            Set<String> categories = Requious.configuration.getCategoryNames();
            list.addAll(categories.stream().filter(s -> !s.contains(".")).map(s -> new DummyConfigElement.DummyCategoryElement(s, s, new ConfigElement(Requious.configuration.getCategory(s)).getChildElements())).collect(Collectors.toList()));

            return list;
        }
    }

}
