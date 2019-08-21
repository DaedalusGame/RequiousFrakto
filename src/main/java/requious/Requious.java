package requious;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import requious.entity.EntitySpark;
import requious.gui.GuiHandler;
import requious.network.PacketHandler;
import requious.proxy.IProxy;
import requious.recipe.RecipeRegistry;
import requious.tile.TileEntityAssembly;
import requious.tile.TileEntityFluidEmitter;
import requious.tile.TileEntityRedEmitter;

import java.io.File;

@Mod(modid = Requious.MODID, name = Requious.MODNAME, acceptedMinecraftVersions = "[1.12, 1.13)", clientSideOnly = true)
@Mod.EventBusSubscriber
public class Requious
{
    public static final String MODID = "requious";
    public static final String MODNAME = "Requious Frakto";

    @SidedProxy(clientSide = "requious.proxy.ClientProxy", serverSide = "requious.proxy.ServerProxy")
    public static IProxy PROXY;

    public static Configuration configuration;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = event.getSuggestedConfigurationFile();

        configuration = new Configuration(configFile);

        Registry.loadEmitterData(event.getModConfigurationDirectory());

        MinecraftForge.EVENT_BUS.register(new Registry());
        MinecraftForge.EVENT_BUS.register(new RecipeRegistry());

        PacketHandler.registerMessages();

        PROXY.preInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityRedEmitter.class, new ResourceLocation(MODID, "red_emitter"));
        GameRegistry.registerTileEntity(TileEntityFluidEmitter.class, new ResourceLocation(MODID, "fluid_emitter"));
        GameRegistry.registerTileEntity(TileEntityAssembly.class, new ResourceLocation(MODID, "assembly"));

        int id = 0;

        EntityRegistry.registerModEntity(new ResourceLocation(Requious.MODID,"spear"), EntitySpark.class, "spark", id++, this, 64, 1, true);

        Registry.init();

        PROXY.init();
    }
}
