package requious.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import requious.compat.crafttweaker.IWorldFunction;
import requious.compat.crafttweaker.MachineContainer;
import requious.compat.crafttweaker.RecipeContainer;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentBase.Collector;
import requious.data.component.ComponentBase.Slot;
import requious.data.component.ComponentEnergy;
import requious.data.component.ComponentLaser;
import requious.recipe.AssemblyRecipe;
import requious.recipe.ConsumptionResult;
import requious.tile.TileEntityAssembly;
import requious.util.CheckCache;
import requious.util.ILaserStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssemblyProcessor implements ICapabilityProvider {
    AssemblyData data;
    Slot[][] slots = new Slot[9][5];
    List<Collector> collectors = new ArrayList<>();
    TileEntity tile;
    Map<String, CheckCache> cache = new HashMap<>();
    Map<String, Object> variables = new HashMap<>();

    public AssemblyProcessor(AssemblyData data) {
        this.data = data;
    }

    public TileEntity getTile() {
        return tile;
    }

    public EnumFacing getFacing() {
        if(tile instanceof TileEntityAssembly)
            return ((TileEntityAssembly) tile).getFacing();
        return EnumFacing.UP;
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, Object value) {
        variables.put(name,value);
    }

    public boolean isCacheInvalid(String type, long time, long interval) {
        CheckCache check = cache.get(type);
        return check == null || check.getTicksSinceLastCheck(time) > interval;
    }

    public boolean getCacheResult(String type) {
        CheckCache check = cache.get(type);
        return check.getResult();
    }

    public void setCacheResult(String type, boolean result, long time) {
        CheckCache check = cache.computeIfAbsent(type, k -> new CheckCache());
        check.setResult(result, time);
    }

    public boolean check(IWorldFunction worldCheck, String group, long interval) {
        if (tile == null || tile.getWorld() == null)
            return false;
        long time = tile.getWorld().getTotalWorldTime();
        if (isCacheInvalid(group, time, interval)) {
            boolean checkResult = worldCheck.run(new MachineContainer(this));
            setCacheResult(group, checkResult, time);
            return checkResult;
        } else {
            return getCacheResult(group);
        }
    }

    public boolean run(IWorldFunction worldCheck) {
        if (tile == null || tile.getWorld() == null)
            return false;
        return worldCheck.run(new MachineContainer(this));
    }

    public NBTTagCompound serializeCache() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound cacheCompound = new NBTTagCompound();
        for (Map.Entry<String, CheckCache> entry : cache.entrySet()) {
            NBTTagCompound cacheEntry = new NBTTagCompound();
            CheckCache cache = entry.getValue();
            cacheEntry.setLong("time", cache.getCheckTime());
            cacheEntry.setBoolean("result", cache.getResult());
            cacheCompound.setTag(entry.getKey(),cacheEntry);
        }
        nbt.setTag("cache",cacheCompound);
        return nbt;
    }

    private void deserializeCache(NBTTagCompound nbt) {
        NBTTagCompound cacheCompound = nbt.getCompoundTag("cache");
        cache.clear();
        for (String key : cacheCompound.getKeySet()) {
            NBTTagCompound cacheEntry = nbt.getCompoundTag(key);
            cache.put(key,new CheckCache(cacheEntry.getBoolean("result"), cacheEntry.getLong("time")));
        }
    }

    public void setComponent(ComponentBase[][] components) {
        for(int x = 0; x < components.length; x++) {
            for (int y = 0; y < components[x].length; y++) {
                ComponentBase component = components[x][y];
                if(component != null)
                    slots[x][y] = component.createSlot();
            }
        }
    }

    private void addToCollector(Slot slot) {
        slot.addCollectors(collectors);
        for (Collector collector : collectors) {
            collector.accept(slot);
        }
    }

    public void setup() {
        for(int x = 0; x < slots.length; x++) {
            for(int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if(slot != null)
                    addToCollector(slot);
            }
        }
    }

    public void setTile(TileEntity tile) {
        this.tile = tile;
        for (Collector collector : collectors) {
            collector.setTile(tile);
        }
    }

    public Slot getSlot(int x, int y) {
        if(x < 0 || x >= slots.length || y < 0 || y >= slots[x].length)
            return null;
        return slots[x][y];
    }

    public List<Slot> getSlots() {
        List<Slot> rList = new ArrayList<>();
        for(int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if(slot != null)
                    rList.add(slot);
            }
        }
        return rList;
    }

    public void update() {
        for(int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if(slot != null) {
                    slot.update();
                }
            }
        }
        for (Collector collector : collectors) {
            collector.update();
        }
        for(List<AssemblyRecipe> recipes : data.recipes.values()) {
            for (AssemblyRecipe recipe : recipes) {
                RecipeContainer container = new RecipeContainer();
                List<ConsumptionResult> results = recipe.matches(this, container);
                if (results != null) {
                    recipe.calculate(container);
                    if (recipe.fitsResults(this, container)) {
                        recipe.consumeRequirements(results);
                        recipe.produceResults(this, container);
                        break;
                    }
                }
            }
        }
    }

    public void machineBroken(World world, Vec3d position) {
        for(int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if(slot != null) {
                    slot.machineBroken(world,position);
                }
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        for(int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if(slot != null)
                    compound.setTag(x+"_"+y, slot.serializeNBT());
            }
        }
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        for(int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if(slot != null)
                    slot.deserializeNBT(compound.getCompoundTag(x+"_"+y));
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        for (Collector collector : collectors) {
            if(collector.hasCapability() && collector.hasCapability(capability,facing))
                return true;
        }
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        T instance = null;
        for (Collector collector : collectors) {
            if(collector.hasCapability())
                instance = collector.getCapability(capability,facing);
            if(instance != null)
                break;
        }
        return instance;
    }

    public ILaserStorage getLaserAcceptor(EnumFacing facing) {
        for (Collector collector : collectors) {
            if(collector instanceof ComponentLaser.Collector && ((ComponentLaser.Collector)collector).getFace().matches(facing))
                return (ILaserStorage) collector;
        }
        return null;
    }

    public ComponentEnergy.CollectorIC2 getIC2Handler() {
        for (Collector collector : collectors) {
            if(collector instanceof ComponentEnergy.CollectorIC2)
                return (ComponentEnergy.CollectorIC2) collector;
        }
        return null;
    }

    public boolean isDirty() {
        boolean dirty = false;
        for(int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if(slot != null && slot.isDirty()) {
                    dirty = true;
                    slot.markClean();
                }
            }
        }
        return dirty;
    }
}
