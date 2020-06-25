package requious;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import requious.block.BlockAssembly;
import requious.block.BlockFluidEmitter;
import requious.block.BlockRedEmitter;
import requious.block.IDynamicModel;
import requious.data.*;
import requious.entity.EntitySpark;
import requious.entity.spark.TargetTile;
import requious.entity.spark.ValueFluid;
import requious.entity.spark.ValueForgeEnergy;
import requious.item.IDynamicItemModel;
import requious.item.ItemBattery;
import requious.item.ItemFluidCell;
import requious.item.ItemTuningFork;
import requious.tile.TileEntityAssembly;
import requious.tile.TileEntityAssemblyRenderer;
import requious.util.AABBTypeAdapter;
import requious.util.ColorTypeAdapter;
import requious.util.LaserVisual;
import requious.util.ResourceLocationTypeAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Registry {
    public static List<RedEmitterData> RED_EMITTER_DATA = new ArrayList<>();
    public static List<FluidEmitterData> FLUID_EMITTER_DATA = new ArrayList<>();
    public static List<AssemblyData> ASSEMBLY_DATA = new ArrayList<>();
    public static List<FluidCellData> FLUID_CELL_DATA = new ArrayList<>();
    public static List<BatteryData> BATTERY_DATA = new ArrayList<>();

    public static List<BlockRedEmitter> RED_EMITTERS = new ArrayList<>();
    public static List<BlockFluidEmitter> FLUID_EMITTERS = new ArrayList<>();

    @GameRegistry.ObjectHolder("requious:tuning_fork")
    public static Item TUNING_FORK;

    public static AssemblyData getAssemblyData(String name) {
        return ASSEMBLY_DATA.stream().filter(data -> data.resourceName.equals(name)).findFirst().orElse(null);
    }

    public static BatteryData getBatteryData(String name) {
        return BATTERY_DATA.stream().filter(data -> data.resourceName.equals(name)).findFirst().orElse(null);
    }

    public static FluidCellData getFluidCellData(String name) {
        return FLUID_CELL_DATA.stream().filter(data -> data.resourceName.equals(name)).findFirst().orElse(null);
    }

    public static void init() {
        EntitySpark.registerValue(new ValueForgeEnergy.Deserializer());
        EntitySpark.registerValue(new ValueFluid.Deserializer());

        EntitySpark.registerTarget(new TargetTile.Deserializer());

        LaserVisual.register("beam", LaserVisual.Beam::new);
        LaserVisual.register("lightning", LaserVisual.Lightning::new);
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();


    }

    public static void initColors() {
        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        for (BlockRedEmitter emitter : RED_EMITTERS) {
            blockColors.registerBlockColorHandler(Registry::colorMultiplierDynamicBlock, emitter);
            itemColors.registerItemColorHandler(Registry::colorMultiplierDynamicItemBlock, emitter);
        }
        for (BlockFluidEmitter emitter : FLUID_EMITTERS) {
            blockColors.registerBlockColorHandler(Registry::colorMultiplierDynamicBlock, emitter);
            itemColors.registerItemColorHandler(Registry::colorMultiplierDynamicItemBlock, emitter);
        }
        for (AssemblyData assembly : ASSEMBLY_DATA) {
            BlockAssembly emitter = assembly.getBlock();
            blockColors.registerBlockColorHandler(Registry::colorMultiplierDynamicBlock, emitter);
            itemColors.registerItemColorHandler(Registry::colorMultiplierDynamicItemBlock, emitter);
        }
        for(FluidCellData cell : FLUID_CELL_DATA) {
            ItemFluidCell emitter = cell.getItem();
            itemColors.registerItemColorHandler(Registry::colorMultiplierDynamicItem, emitter);
        }
        for(BatteryData cell : BATTERY_DATA) {
            ItemBattery emitter = cell.getItem();
            itemColors.registerItemColorHandler(Registry::colorMultiplierDynamicItem, emitter);
        }
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        for (RedEmitterData data : RED_EMITTER_DATA) {
            BlockRedEmitter emitter = (BlockRedEmitter) new BlockRedEmitter(Material.IRON, data).setRegistryName(Requious.MODID, data.resourceName).setUnlocalizedName(data.resourceName).setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
            event.getRegistry().register(emitter);
            RED_EMITTERS.add(emitter);
        }

        for (FluidEmitterData data : FLUID_EMITTER_DATA) {
            BlockFluidEmitter emitter = (BlockFluidEmitter) new BlockFluidEmitter(Material.IRON, data).setRegistryName(Requious.MODID, data.resourceName).setUnlocalizedName(data.resourceName).setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
            event.getRegistry().register(emitter);
            FLUID_EMITTERS.add(emitter);
        }

        for (AssemblyData data : ASSEMBLY_DATA) {
            BlockAssembly assembly = (BlockAssembly) new BlockAssembly(Material.IRON, data).setRegistryName(Requious.MODID, data.resourceName).setUnlocalizedName(data.resourceName).setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
            event.getRegistry().register(assembly);
            data.setBlock(assembly);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        for (BlockRedEmitter emitter : RED_EMITTERS) {
            event.getRegistry().register(new ItemBlock(emitter).setRegistryName(emitter.getRegistryName()));
        }
        for (BlockFluidEmitter emitter : FLUID_EMITTERS) {
            event.getRegistry().register(new ItemBlock(emitter).setRegistryName(emitter.getRegistryName()));
        }
        for (AssemblyData data : ASSEMBLY_DATA) {
            BlockAssembly assembly = data.getBlock();
            event.getRegistry().register(new ItemBlock(assembly).setRegistryName(assembly.getRegistryName()));
        }

        for (FluidCellData data : FLUID_CELL_DATA) {
            ItemFluidCell cell = (ItemFluidCell) new ItemFluidCell(data).setRegistryName(data.resourceName).setUnlocalizedName(data.resourceName).setCreativeTab(CreativeTabs.BREWING);
            event.getRegistry().register(cell);
            data.setItem(cell);
        }
        for (BatteryData data : BATTERY_DATA) {
            ItemBattery cell = (ItemBattery) new ItemBattery(data).setRegistryName(data.resourceName).setUnlocalizedName(data.resourceName).setCreativeTab(CreativeTabs.REDSTONE);
            event.getRegistry().register(cell);
            data.setItem(cell);
        }

        event.getRegistry().register(TUNING_FORK = new ItemTuningFork().setRegistryName(new ResourceLocation(Requious.MODID, "tuning_fork")).setUnlocalizedName("tuning_fork").setCreativeTab(CreativeTabs.REDSTONE));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAssembly.class, new TileEntityAssemblyRenderer());

        for (BlockRedEmitter emitter : RED_EMITTERS) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(emitter), 0, new ModelResourceLocation(emitter.getRedirect(), "inventory"));

            ModelLoader.setCustomStateMapper(emitter, new DynamicStateMapper());
        }
        for (BlockFluidEmitter emitter : FLUID_EMITTERS) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(emitter), 0, new ModelResourceLocation(emitter.getRedirect(), "inventory"));

            ModelLoader.setCustomStateMapper(emitter, new DynamicStateMapper());
        }
        for (AssemblyData data : ASSEMBLY_DATA) {
            BlockAssembly assembly = data.getBlock();
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(assembly), 0, new ModelResourceLocation(assembly.getRedirect(), "inventory"));
            for(int i = 0; i < data.extraVariants.length; i++) {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(assembly), i+1, new ModelResourceLocation(data.extraVariants[i]));
            }
            ModelLoader.setCustomStateMapper(assembly, new DynamicStateMapper());
        }
        for(FluidCellData data : FLUID_CELL_DATA) {
            ItemFluidCell cell = data.getItem();
            ModelLoader.setCustomModelResourceLocation(cell, 0, new ModelResourceLocation(data.model, "inventory"));
        }

        for(BatteryData data : BATTERY_DATA) {
            ItemBattery cell = data.getItem();
            ModelLoader.setCustomModelResourceLocation(cell, 0, new ModelResourceLocation(data.model, "inventory"));
        }

        registerItemModel(TUNING_FORK, 0, "inventory");
    }

    private static int colorMultiplierDynamicBlock(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
        IDynamicModel redirector = (IDynamicModel) state.getBlock();
        return redirector.getTint(tintIndex).getRGB();
    }

    private static int colorMultiplierDynamicItemBlock(ItemStack stack, int tintIndex) {
        ItemBlock itemBlock = (ItemBlock) stack.getItem();
        Block block = itemBlock.getBlock();
        if (block instanceof IDynamicModel) {
            return ((IDynamicModel) block).getTint(tintIndex).getRGB();
        }
        return -1;
    }

    private static int colorMultiplierDynamicItem(ItemStack stack, int tintIndex) {
        Item item = stack.getItem();
        if (item instanceof IDynamicItemModel) {
            return ((IDynamicItemModel) item).getTint(stack,tintIndex).getRGB();
        }
        return -1;
    }


    @SideOnly(Side.CLIENT)
    public void registerItemModel(@Nonnull Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    public static void loadEmitterData(File file) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .setPrettyPrinting()
                .registerTypeAdapter(Color.class, new ColorTypeAdapter())
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter())
                .registerTypeAdapter(AxisAlignedBB.class, new AABBTypeAdapter())
                .create();

        File configFolder = new File(file, Requious.MODID);
        if (!configFolder.exists()) {
            configFolder.mkdir();
        }

        readEmitterData(RedEmitterData.class, gson, new File(configFolder, "red_emitter.json"), Registry::addRedEmitter, Registry::getRedEmitterDefaults);
        readEmitterData(FluidEmitterData.class, gson, new File(configFolder, "fluid_emitter.json"), Registry::addFluidEmitter, Registry::getFluidEmitterDefaults);
        readEmitterData(AssemblyData.class, gson, new File(configFolder, "assembly.json"), Registry::addAssembly, Registry::getAssemblyDefaults);
        readEmitterData(FluidCellData.class, gson, new File(configFolder, "fluid_cell.json"), Registry::addFluidCell, Registry::getFluidCellDefaults);
        readEmitterData(BatteryData.class, gson, new File(configFolder, "battery.json"), Registry::addBattery, Registry::getBatteryDefaults);
    }

    private static <T> void readEmitterData(Class<T> type, Gson gson, File configJson, Consumer<T> consumer, Supplier<Iterable<T>> defaultData) {
        try {
            if (configJson.exists()) {
                JsonReader reader = gson.newJsonReader(new FileReader(configJson));
                JsonParser parser = new JsonParser();
                JsonArray root = (JsonArray) parser.parse(reader);
                for (JsonElement element : root) {
                    if (element.isJsonObject()) {
                        T datum = gson.fromJson(element, type);
                        consumer.accept(datum);
                    }
                }
                reader.close();
            } else {
                JsonWriter writer = gson.newJsonWriter(new FileWriter(configJson));
                Iterable<T> data = defaultData.get();
                writer.beginArray();
                for (T datum : data) {
                    gson.toJson(datum, type, writer);
                    consumer.accept(datum);
                }
                writer.endArray();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Iterable<RedEmitterData> getRedEmitterDefaults() {
        List<RedEmitterData> list = new ArrayList<>();

        RedEmitterData basic = new RedEmitterData();
        basic.resourceName = "red_emitter_basic";
        basic.model = new ResourceLocation(Requious.MODID, "red_emitter");
        basic.capacity = 64;
        basic.interval = 40;
        list.add(basic);
        RedEmitterData advanced = new RedEmitterData();
        advanced.resourceName = "red_emitter_advanced";
        advanced.model = new ResourceLocation(Requious.MODID, "red_emitter");
        advanced.capacity = 128;
        advanced.interval = 20;
        list.add(advanced);

        return list;
    }

    private static Iterable<FluidEmitterData> getFluidEmitterDefaults() {
        List<FluidEmitterData> list = new ArrayList<>();

        FluidEmitterData liquidOnly = new FluidEmitterData();
        liquidOnly.resourceName = "liquid_emitter";
        liquidOnly.model = new ResourceLocation(Requious.MODID, "fluid_emitter");
        liquidOnly.capacity = 1000;
        liquidOnly.interval = 40;
        list.add(liquidOnly);
        FluidEmitterData gasOnly = new FluidEmitterData();
        gasOnly.resourceName = "gas_emitter";
        gasOnly.model = new ResourceLocation(Requious.MODID, "fluid_emitter");
        gasOnly.capacity = 1000;
        gasOnly.interval = 40;
        list.add(gasOnly);

        return list;
    }

    private static Iterable<AssemblyData> getAssemblyDefaults() {
        List<AssemblyData> list = new ArrayList<>();

        AssemblyData itemGate = new AssemblyData();
        itemGate.resourceName = "item_gate";
        itemGate.model = new ResourceLocation(Requious.MODID, "assembly_block");
        list.add(itemGate);
        AssemblyData laser = new AssemblyData();
        laser.resourceName = "laser";
        laser.model = new ResourceLocation(Requious.MODID, "assembly_laser");
        list.add(laser);
        AssemblyData assembler = new AssemblyData();
        assembler.resourceName = "assembler";
        assembler.model = new ResourceLocation(Requious.MODID, "assembly_slab");
        list.add(assembler);

        return list;
    }

    private static Iterable<FluidCellData> getFluidCellDefaults() {
        List<FluidCellData> list = new ArrayList<>();

        FluidCellData cell = new FluidCellData();
        cell.resourceName = "cell";
        cell.model = new ResourceLocation(Requious.MODID, "cell");
        cell.colorsSerialized = new Color[] {new Color(255,255,255)};
        cell.generateSubItems = true;
        cell.capacity = 1000;
        list.add(cell);

        return list;
    }

    private static Iterable<BatteryData> getBatteryDefaults() {
        List<BatteryData> list = new ArrayList<>();

        BatteryData cell = new BatteryData();
        cell.resourceName = "battery_copper";
        cell.model = new ResourceLocation(Requious.MODID, "battery_small");
        cell.colorsSerialized = new Color[] {new Color(220,64,16), new Color(255,192,64)};
        cell.capacity = 1000;
        list.add(cell);
        cell = new BatteryData();
        cell.resourceName = "battery_iron";
        cell.model = new ResourceLocation(Requious.MODID, "battery_medium_fill0");
        cell.colorsSerialized = new Color[] {new Color(255,255,255), new Color(255,192,64), new Color(255,0,0)};
        cell.capacity = 2000;
        list.add(cell);
        cell = new BatteryData();
        cell.resourceName = "energy_gem";
        cell.model = new ResourceLocation(Requious.MODID, "energy_diamond");
        cell.colorsSerialized = new Color[] {new Color(255,0,0), new Color(255,192,192)};
        cell.capacity = 64000;
        list.add(cell);
        cell = new BatteryData();
        cell.resourceName = "lapotron";
        cell.model = new ResourceLocation(Requious.MODID, "lapotron_diamond");
        cell.colorsSerialized = new Color[] {new Color(0,0,255), new Color(192,192,255), new Color(255,255,255)};
        cell.capacity = 256000;
        list.add(cell);
        cell = new BatteryData();
        cell.resourceName = "lapotron_orb";
        cell.model = new ResourceLocation(Requious.MODID, "containment_gem");
        cell.colorsSerialized = new Color[] {new Color(255,255,255),new Color(0,0,255)};
        cell.capacity = 256000*8;
        list.add(cell);
        cell = new BatteryData();
        cell.resourceName = "quantron";
        cell.model = new ResourceLocation(Requious.MODID, "lapotron_emerald");
        cell.colorsSerialized = new Color[] {new Color(128,255,0), new Color(255,255,192), new Color(64,64,64)};
        cell.capacity = 68000000;
        list.add(cell);
        cell = new BatteryData();
        cell.resourceName = "positron_unit";
        cell.model = new ResourceLocation(Requious.MODID, "containment_fill0");
        cell.colorsSerialized = new Color[] {new Color(128,255,192), new Color(255,255,0)};
        cell.capacity = 1000000000;
        list.add(cell);

        return list;
    }

    private static void addRedEmitter(RedEmitterData data) {
        RED_EMITTER_DATA.add(data);
        data.init();
    }

    private static void addFluidEmitter(FluidEmitterData data) {
        FLUID_EMITTER_DATA.add(data);
        data.init();
    }

    private static void addAssembly(AssemblyData data) {
        ASSEMBLY_DATA.add(data);
        data.init();
    }

    private static void addFluidCell(FluidCellData data) {
        FLUID_CELL_DATA.add(data);
        data.init();
    }

    private static void addBattery(BatteryData data) {
        BATTERY_DATA.add(data);
        data.init();
    }

    private static class DynamicStateMapper extends StateMapperBase {
        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            IDynamicModel redirector = (IDynamicModel) state.getBlock();
            Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
            return new ModelResourceLocation(redirector.getRedirect(), this.getPropertyString(map));
        }
    }

}
