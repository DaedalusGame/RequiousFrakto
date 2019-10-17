package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import requious.compat.crafttweaker.IWorldFunction;
import requious.compat.crafttweaker.MachineContainer;
import requious.util.ComponentFace;
import stanhebben.zenscript.annotations.ZenClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.requious.WorldSlot")
public class ComponentWorld extends ComponentBase {
    public ComponentWorld() {
        super(ComponentFace.None);
    }

    @Override
    public Slot createSlot() {
        return new Slot(this);
    }

    static class CheckCache {
        boolean result;
        long checkTime;

        public CheckCache() {
        }

        public CheckCache(boolean result, long checkTime) {
            this.result = result;
            this.checkTime = checkTime;
        }

        public void setResult(boolean result, long time) {
            this.result = result;
            this.checkTime = time;
        }

        public boolean getResult() {
            return result;
        }

        public long getTicksSinceLastCheck(long time) {
            return time - checkTime;
        }
    }

    public static class Slot extends ComponentBase.Slot<ComponentWorld> {
        Collector collector;
        HashMap<String, CheckCache> cache = new HashMap<>();

        public Slot(ComponentWorld component) {
            super(component);
        }

        public EnumFacing getFacing() {
            return collector.getTileFacing();
        }

        public TileEntity getTile() {
            return collector.getTile();
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

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            Collector world = new Collector(this);
            collectors.add(world);
        }

        @Override
        public net.minecraft.inventory.Slot createGui(int x, int y) {
            return null;
        }

        @Override
        public void update() {
            //NOOP
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            //NOOP
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagCompound cacheCompound = new NBTTagCompound();
            for (Map.Entry<String, CheckCache> entry : cache.entrySet()) {
                NBTTagCompound cacheEntry = new NBTTagCompound();
                cacheEntry.setLong("time",entry.getValue().checkTime);
                cacheEntry.setBoolean("result",entry.getValue().result);
                cacheCompound.setTag(entry.getKey(),cacheEntry);
            }
            nbt.setTag("cache",cacheCompound);
            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            NBTTagCompound cacheCompound = nbt.getCompoundTag("cache");
            cache.clear();
            for (String key : cacheCompound.getKeySet()) {
                NBTTagCompound cacheEntry = nbt.getCompoundTag(key);
                cache.put(key,new CheckCache(cacheEntry.getBoolean("result"), cacheEntry.getLong("time")));
            }
        }

        public void setCollector(Collector collector) {
            this.collector = collector;
        }

        public boolean check(IWorldFunction worldCheck, String group, long interval) {
            if(collector == null)
                return false;
            TileEntity tile = getTile();
            if (tile == null || tile.getWorld() == null)
                return false;
            EnumFacing facing = getFacing();
            long time = tile.getWorld().getTotalWorldTime();
            if (isCacheInvalid(group, time, interval)) {
                boolean checkResult = worldCheck.run(new MachineContainer(tile, facing));
                setCacheResult(group, checkResult, time);
                return checkResult;
            } else {
                return getCacheResult(group);
            }
        }

        public boolean run(IWorldFunction worldCheck) {
            TileEntity tile = getTile();
            if (tile == null || tile.getWorld() == null)
                return false;
            EnumFacing facing = getFacing();
            return worldCheck.run(new MachineContainer(tile, facing));
        }
    }

    public static class Collector extends ComponentBase.Collector {
        Slot slot;

        public Collector(Slot slot) {
            this.slot = slot;
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if(slot instanceof Slot) {
                ((Slot) slot).setCollector(this);
                return true;
            }
            return false;
        }

        @Override
        public void update() {
            //NOOP
        }
    }
}
